package com.mikesajak.ebooklibrary.payload

import java.util.*

data class BookCover(val bookId: BookId, val contentType: String, val filename: String,
                     val imageData: ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BookCover

        if (bookId != other.bookId) return false
        if (contentType != other.contentType) return false
        if (filename != other.filename) return false

        return true
    }

    override fun hashCode(): Int {
        var result = bookId.hashCode()
        result = 31 * result + contentType.hashCode()
        result = 31 * result + filename.hashCode()
        return result
    }
}

data class BookDataId(val value: String) {
    companion object {
        fun randomId() = BookDataId(UUID.randomUUID().toString())
    }

    override fun toString(): String = value
}

data class BookData(val id: BookDataId, val type: String, val filename: String, val data: ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BookData

        if (type != other.type) return false
        if (filename != other.filename) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + filename.hashCode()
        return result
    }
}

data class BookFormat(val bookId: BookId, val bookData: BookData)