package com.mikesajak.ebooklibrary.storage.nitrite

import cz.jirutka.rsql.parser.RSQLParser
import cz.jirutka.rsql.parser.ast.*
import org.dizitart.no2.objects.ObjectFilter
import org.dizitart.no2.objects.filters.ObjectFilters.*
import org.springframework.stereotype.Component

@Component
class RSQLToNitriteQueryParser {
    private val parser: RSQLParser

    init {
        val operators = RSQLOperators.defaultOperators()
        operators.add(ComparisonOperator("=rx=", false))
        operators.add(ComparisonOperator("=notrx=", false))
        parser = RSQLParser(operators)
    }

    fun parse(query: String): ObjectFilter {
        val rootNode = parser.parse(query)
        return rootNode.accept(NitriteQueryRSQLVisitor())
    }
}
//
//fun main(args: Array<String>) {
//    val filter = RSQLToNitriteQueryParser().parse("""(title =rx= "Książka" or author =rx= "Dan") and (tag =in= (tag1,tag2) or tag=="tag1") or title =notrx= "dupa"""".trimMargin())
//    println(filter)
//}

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
        val arrayField = isArrayField(node.selector)
        val fieldName = if (!arrayField) "metadata.${node.selector}" else "$"
        val filter = when(node.operator.symbol) {
            "==" -> eq(fieldName, node.arguments[0])
            "!=" -> not(eq(fieldName, node.arguments[0]))
            "=in=" -> `in`(fieldName, *node.arguments.toTypedArray())
            "=out=" -> not(`in`(fieldName, *node.arguments.toTypedArray()))
            "~=" -> regex(fieldName, node.arguments[0])
            "=rx=" -> regex(fieldName, node.arguments[0])
            "=notrx=" -> not(regex(fieldName, node.arguments[0]))
            else -> throw UnknownOperatorException(node.toString())
        }
        return if (arrayField) elemMatch("metadata.${node.selector}", filter)
               else filter
    }

    fun isArrayField(name: String) = when(name) {
        "tags" -> true
        "authors" -> true
        "languages" -> true
        else -> false
    }



    open class ParseException(msg: String) : Exception(msg)
    class UnknownOperatorException(msg: String) : ParseException(msg)
}