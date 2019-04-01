package com.mikesajak.ebooklibrary.bookformat

import org.springframework.stereotype.Service

@Service
class BookFormatManager {
    private var readers = mutableMapOf<String, BookFormatReader>()

    fun register(type: String, reader: BookFormatReader) {
        readers[type] = reader
    }

    fun readerFor(type: String): BookFormatReader? {
        return readers[type]
    }
}