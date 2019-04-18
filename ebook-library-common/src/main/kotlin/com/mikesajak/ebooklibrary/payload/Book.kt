package com.mikesajak.ebooklibrary.payload

import java.time.LocalDate
import java.util.*

data class BookId(val value: String) : Comparable<BookId>{
    override fun compareTo(other: BookId): Int =
        value.compareTo(other.value)

    companion object {
        fun randomBookId() = BookId(UUID.randomUUID().toString())
    }

    override fun toString(): String = value
}

data class Book(val id: BookId, val metadata: BookMetadata)

data class BookMetadata(val title: String,
                        val authors: List<String>,
                        val tags: List<String>,
                        val identifiers: List<String>,
                        val creationDate: LocalDate?,
                        val publicationDate: LocalDate?,
                        val publisher: String?,
                        val languages: List<String>,
                        val series: Series?,
                        val description: String?) {
    constructor(title: String,
                author: String,
                tags: List<String>,
                identifiers: List<String>,
                creationDate: LocalDate?,
                publicationDate: LocalDate?,
                publisher: String?,
                languages: List<String>,
                series: Series?,
                description: String?)
    : this(title, listOf(author), tags, identifiers, creationDate, publicationDate,
        publisher, languages, series, description)

    constructor(title: String,
                author: String,
                tags: List<String>,
                identifiers: List<String>,
                creationDate: LocalDate?,
                publicationDate: LocalDate?,
                publisher: String?,
                languages: List<String>,
                description: String?)
            : this(title, listOf(author), tags, identifiers, creationDate, publicationDate,
        publisher, languages, null, description)

    companion object {

        fun builder(title: String) = Builder(title)

        data class Builder(val title: String) {
            private val authors: MutableList<String> = mutableListOf()
            private val tags: MutableList<String> = mutableListOf()
            private val identifiers: MutableList<String> = mutableListOf()
            private var creationDate: LocalDate? = null
            private var publicationDate: LocalDate? = null
            private var publisher: String? = null
            private val languages: MutableList<String> = mutableListOf()
            private var series: Series? = null
            private var description: String? = null

            fun build() = BookMetadata(title, authors, tags, identifiers, creationDate,
                publicationDate, publisher, languages, series, description)

            fun author(name: String) = apply {
                authors += name
            }

            fun authors(names: List<String>) = apply {
                authors += names
            }

            fun tag(t: String) = apply {
                tags += t
            }

            fun tags(ts: List<String>) = apply {
                tags += ts
            }

            fun identifier(id: String) = apply {
                identifiers += id
            }

            fun identifiers(ids: List<String>) = apply {
                identifiers += ids
            }

            fun creationDate(date: LocalDate) = apply {
                creationDate = date
            }

            fun publicationDate(date: LocalDate) = apply {
                publicationDate = date
            }

            fun publisher(name: String) = apply {
                publisher = name
            }

            fun language(lang: String) = apply {
                languages += lang
            }

            fun languages(langs: List<String>) = apply {
                languages += langs
            }

            fun series(ser: Series) = apply {
                series = ser
            }

            fun description(descr: String) = apply {
                description = descr
            }

        }


    }
}

data class Series(val title: String, val number: Int)
