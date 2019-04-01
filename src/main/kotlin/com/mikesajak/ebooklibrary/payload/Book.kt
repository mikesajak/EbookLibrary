package com.mikesajak.ebooklibrary.payload

import java.time.LocalDate
import java.util.*

data class BookId(val value: String) {
    companion object {
        fun randomBookId() = BookId(UUID.randomUUID().toString())
    }

    override fun toString(): String = value
}

data class Book(val id: BookId, val descriptor: BookDescriptor)

data class BookDescriptor(val title: String,
                          val author: String?,
                          val tags: List<String>,
                          val identifiers: List<String>,
                          val creationDate: LocalDate?,
                          val publicationDate: LocalDate?,
                          val publisher: String?,
                          val languages: List<String>,
                          val description: String?)