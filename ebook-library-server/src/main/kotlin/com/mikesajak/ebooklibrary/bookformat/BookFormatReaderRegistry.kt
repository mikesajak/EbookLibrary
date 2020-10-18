package com.mikesajak.ebooklibrary.bookformat

import org.springframework.stereotype.Service

@Service
class BookFormatReaderRegistry(val readers: List<BookMetadataReader>) {
    private var readersMap = mutableMapOf<String, BookMetadataReader>()

    init {
        readers.forEach { register(it.mimeType, it) }
    }

    fun register(type: String, reader: BookMetadataReader) {
        readersMap[type] = reader
    }

    fun readerFor(type: String): BookMetadataReader? {
        return readersMap[type]
    }
}