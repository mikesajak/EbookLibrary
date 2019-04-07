package com.mikesajak.ebooklibrary.bookformat

import org.springframework.stereotype.Service

@Service
class BookFormatReaderRegistry {
    private var readers = mutableMapOf<String, BookMetadataReader>()

    fun register(type: String, reader: BookMetadataReader) {
        readers[type] = reader
    }

    fun readerFor(type: String): BookMetadataReader? {
        return readers[type]
    }
}