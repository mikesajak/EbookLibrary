package com.mikesajak.ebooklibrary.payload

import java.util.*

data class BookCover(val bookId: BookId, val coverImage: CoverImage) {
    constructor(bookId: BookId, name: String, contentType: String, imageData: ByteArray)
        : this(bookId, CoverImage(name, contentType, imageData))
}

data class CoverImage(val name: String, val contentType: String, val imageData: ByteArray)

data class BookFormatId(val value: String) {
    companion object {
        fun randomId() = BookFormatId(UUID.randomUUID().toString())
    }

    override fun toString(): String = value
}

//data class BookFormatDto(val bookId: BookId, val bookFormat: BookFormat)

data class BookFormatMetadataDto(val id: BookFormatId, val metadata: BookFormatMetadata)

data class BookFormat(val metadata: BookFormatMetadata, val contents: ByteArray)
data class BookFormatMetadata(val bookId: BookId, val type: String, val filename: String)
