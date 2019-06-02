package com.mikesajak.ebooklibrary.bookformat

import com.mikesajak.ebooklibrary.payload.BookMetadata
import com.mikesajak.ebooklibrary.payload.CoverImage
import java.io.InputStream

data class BookFormatType(val name: String, val mimeType: String)

abstract class BookMetadataReader(val bookFormatType: BookFormatType) {
    abstract fun canRead(bookData: InputStream): Boolean
    abstract fun read(bookData: InputStream): BookMetadata
    abstract fun readCover(bookData: InputStream): CoverImage?
}