package com.mikesajak.ebooklibrary.bookformat

import com.mikesajak.ebooklibrary.payload.BookData
import com.mikesajak.ebooklibrary.payload.BookMetadata
import nl.siegmann.epublib.domain.Date
import nl.siegmann.epublib.epub.EpubReader
import java.io.ByteArrayInputStream
import java.time.LocalDate

class EpubBookMetadataReader : BookMetadataReader("EPUB") {
    override fun read(bookData: BookData): BookMetadata {
        val reader = EpubReader()
        val epub = reader.readEpub(ByteArrayInputStream(bookData.data))

        val metadata = epub.metadata
        val title = metadata.firstTitle

        val authors = metadata.authors
            .map { "${it.firstname} ${it.lastname}" }
            .toList()

        val identifiers = metadata.identifiers
            .map { "${it.scheme}:${it.value}" }
            .toList()

        val creationDate = metadata.dates.filter { it.event == Date.Event.CREATION }
            .map { LocalDate.parse(it.value) }
            .firstOrNull()

        val publicationDate = metadata.dates.filter { it.event == Date.Event.PUBLICATION }
            .map { LocalDate.parse(it.value) }
            .firstOrNull()

        val publisher = metadata.publishers.firstOrNull()

        val description = metadata.descriptions
            .fold("", operation = { acc, d -> "$acc\n\n$d" })

        return BookMetadata(title, authors, listOf(), identifiers,
            creationDate, publicationDate, publisher, listOf(metadata.language),
            description)
    }
}