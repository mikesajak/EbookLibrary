package com.mikesajak.ebooklibrary.bookformat

import com.mikesajak.ebooklibrary.model.BookMetadata
import com.mikesajak.ebooklibrary.model.CoverImage
import java.io.InputStream

abstract class BookMetadataReader(val mimeType: String) {
    abstract fun canRead(bookData: InputStream): Boolean
    abstract fun read(bookData: InputStream): BookMetadata
    abstract fun readCover(bookData: InputStream): CoverImage?
}