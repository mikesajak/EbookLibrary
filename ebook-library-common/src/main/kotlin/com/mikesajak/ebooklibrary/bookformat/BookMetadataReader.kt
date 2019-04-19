package com.mikesajak.ebooklibrary.bookformat

import com.mikesajak.ebooklibrary.payload.BookMetadata
import com.mikesajak.ebooklibrary.payload.CoverImage

abstract class BookMetadataReader(val bookType: String) {
    abstract fun canRead(bookData: ByteArray): Boolean
    abstract fun read(bookData: ByteArray): BookMetadata
    abstract fun readCover(bookData: ByteArray): CoverImage?
}