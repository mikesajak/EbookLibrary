package com.mikesajak.ebooklibrary.bookformat

import com.mikesajak.ebooklibrary.payload.BookMetadata
import com.mikesajak.ebooklibrary.payload.CoverImage
import nl.siegmann.epublib.domain.Date
import nl.siegmann.epublib.epub.EpubReader
import java.io.ByteArrayInputStream
import java.io.IOException
import java.time.LocalDate

class EpubBookMetadataReader : BookMetadataReader("EPUB") {

    override fun canRead(bookData: ByteArray): Boolean =
        try {
            val reader = EpubReader()
            reader.readEpub(ByteArrayInputStream(bookData))
            true
        } catch (e: IOException) {
            false
        }

    override fun read(bookData: ByteArray): BookMetadata {
        val reader = EpubReader()
        val epub = reader.readEpub(ByteArrayInputStream(bookData))

        val bookMetaBuilder = BookMetadata.builder(epub.metadata.firstTitle)
        epub.metadata.authors.map { "${it.firstname} ${it.lastname}" }
            .forEach { bookMetaBuilder.author(it) }

        epub.metadata.identifiers.map { "${it.scheme}:${it.value}" }
            .forEach { bookMetaBuilder.identifier(it) }

        epub.metadata.dates.filter { it.event == Date.Event.CREATION }
            .map { LocalDate.parse(it.value) }
            .take(1)
            .forEach { bookMetaBuilder.creationDate(it) }

        epub.metadata.dates.filter { it.event == Date.Event.PUBLICATION }
            .map { LocalDate.parse(it.value) }
            .take(1)
            .forEach { bookMetaBuilder.publicationDate(it) }

        epub.metadata.publishers.take(1)
            .forEach { bookMetaBuilder.publisher(it) }

        val descr = epub.metadata.descriptions
            .fold("", operation = { acc, d -> "$acc\n\n$d" })

        if (descr.isNotBlank())
            bookMetaBuilder.description(descr)

        return bookMetaBuilder.build()
    }

    override fun readCover(bookData: ByteArray): CoverImage? {
        val reader = EpubReader()
        val epub = reader.readEpub(ByteArrayInputStream(bookData))
        val epubImage = epub.coverImage

        return epubImage?.let { imgRes ->
            val imageName = imgRes.title ?: imgRes.href ?: imgRes.id
            CoverImage(imageName, epubImage.mediaType.name, epubImage.data)
        }
    }
}