package com.mikesajak.ebooklibrary.payload

import java.util.*

data class BookCover(val bookId: BookId, val coverImage: CoverImage) {
    constructor(bookId: BookId, name: String, contentType: String, imageData: ByteArray)
        : this(bookId, CoverImage(name, contentType, imageData))
}

data class CoverImage(val name: String, val contentType: String, val imageData: ByteArray)

data class BookDataId(val value: String) {
    companion object {
        fun randomId() = BookDataId(UUID.randomUUID().toString())
    }

    override fun toString(): String = value
}

data class BookData(val id: BookDataId, val type: String, val filename: String, val data: ByteArray)

data class BookFormat(val bookId: BookId, val bookData: BookData)