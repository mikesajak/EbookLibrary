package com.mikesajak.ebooklibrary.storage.nitrite

import cz.jirutka.rsql.parser.RSQLParser
import cz.jirutka.rsql.parser.ast.*
import org.dizitart.no2.objects.ObjectFilter
import org.dizitart.no2.objects.filters.ObjectFilters.*
import org.springframework.stereotype.Component
import java.util.regex.Pattern

@Component
class RSQLToNitriteQueryParser {
    private val parser: RSQLParser

    init {
        val operators = RSQLOperators.defaultOperators()
        operators.add(ComparisonOperator("=rx=", false))
        operators.add(ComparisonOperator("=notrx=", false))
        operators.add(ComparisonOperator("=like=", false))
        parser = RSQLParser(operators)
    }

    fun parse(query: String): ObjectFilter {
        val rootNode = parser.parse(query)
        return rootNode.accept(NitriteQueryRSQLVisitor())
    }
}

class NitriteQueryRSQLVisitor : NoArgRSQLVisitorAdapter<ObjectFilter>() {

    override fun visit(node: AndNode): ObjectFilter? {
        println("And node: $node")
        val childFilters = node.children.map { it.accept(this) }
        return and(*childFilters.toTypedArray())
    }

    override fun visit(node: OrNode): ObjectFilter? {
        println("Or node: $node")
        val childFilters = node.children.map { it.accept(this) }
        return or(*childFilters.toTypedArray())
    }

    override fun visit(node: ComparisonNode): ObjectFilter? {
        println("Comparison node: $node")
        val fieldName = correctArrayFieldName(node.selector)
        val arrayField = isArrayField(fieldName)
        val dbFieldName = if (!arrayField) "metadata.$fieldName" else "$"
        val filter = when(node.operator.symbol) {
            "==" -> eq(dbFieldName, node.arguments[0])
            "!=" -> not(eq(dbFieldName, node.arguments[0]))
            "=in=" -> `in`(dbFieldName, node.arguments.toTypedArray())
            "=out=" -> not(`in`(dbFieldName, node.arguments.toTypedArray()))
            "~=" -> regex(dbFieldName, node.arguments[0])
            "=like=" -> regex(dbFieldName, prepareLikeRegexMatcher(node.arguments[0]))
            "=rx=" -> regex(dbFieldName, node.arguments[0])
            "=notrx=" -> not(regex(dbFieldName, node.arguments[0]))
            else -> throw UnknownOperatorException(node.toString())
        }
        return if (arrayField) elemMatch("metadata.${fieldName}", filter)
               else filter
    }

    private fun prepareLikeRegexMatcher(query: String) = "(?i).*?${Pattern.quote(query)}.*"

    private fun isArrayField(name: String) = when(name) {
        "authors" -> true
        "tags" -> true
        "languages" -> true
        else -> false
    }

    private fun correctArrayFieldName(name: String) = when(name) {
        "author" -> "authors"
        "tag" -> "tags"
        "language" -> "languages"
        "lang" -> "languages"
        else -> name
    }




    open class ParseException(msg: String) : Exception(msg)
    class UnknownOperatorException(msg: String) : ParseException(msg)
}