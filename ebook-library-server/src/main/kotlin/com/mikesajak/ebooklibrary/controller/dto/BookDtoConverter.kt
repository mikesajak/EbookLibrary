package com.mikesajak.ebooklibrary.controller.dto

import com.mikesajak.ebooklibrary.model.*
import org.springframework.stereotype.Component

@Component
class BookDtoConverter {
    fun mkBookDto(book: Book, bookFormats: List<BookFormatMetadata>) =
        book.metadata.let { meta ->
            BookDto(book.id.value, meta.title, meta.authors, meta.tags,
                meta.identifiers, meta.creationDate, meta.publicationDate, meta.publisher, meta.languages,
                meta.series?.let { SeriesDto(it.title, it.number) },
                meta.description,
                bookFormats.map { fmt -> BookFormatMetadataDto(fmt.bookFormatId.value, fmt.bookId.value, fmt.formatType, fmt.size) }
            )
        }

    fun mkBook(bookId: BookId, bookDto: BookDto) = Book(bookId, mkBookMetadata(bookDto))

    fun mkBookMetadata(bookDto: BookDto) =
        BookMetadata(bookDto.title, bookDto.authors.distinct(), bookDto.tags.distinct(),
            bookDto.identifiers.distinct(), bookDto.creationDate, bookDto.publicationDate,
            bookDto.publisher, bookDto.languages.distinct(),
            bookDto.series?.let { Series(it.title, it.number) },
            bookDto.description)

    fun mkBookFormatMetadataDto(metadata: BookFormatMetadata) =
        BookFormatMetadataDto(metadata.bookFormatId.value, metadata.bookId.value, metadata.formatType, metadata.size)
}