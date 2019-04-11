package com.mikesajak.ebooklibrary.payload

import java.time.LocalDate
import java.util.*

data class BookId(val value: String) {
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
                        val description: String?) {
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
        publisher, languages, description)
}