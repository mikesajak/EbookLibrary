package com.mikesajak.ebooklibrary.model

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

data class BookFormat(val bookFormatId: BookFormatId, val metadata: BookFormatMetadata, val contents: ByteArray)
data class BookFormatMetadata(val bookFormatId: BookFormatId, val bookId: BookId, val formatType: String, val size: Int, val filename: String)
