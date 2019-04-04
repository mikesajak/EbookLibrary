package com.mikesajak.ebooklibrary.bookformat

import com.mikesajak.ebooklibrary.payload.BookData
import com.mikesajak.ebooklibrary.payload.BookMetadata

abstract class BookMetadataReader(type: String) {
    abstract fun read(bookData: BookData): BookMetadata
}