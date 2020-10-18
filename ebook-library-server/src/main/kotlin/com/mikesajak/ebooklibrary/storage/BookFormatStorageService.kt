package com.mikesajak.ebooklibrary.storage

import com.mikesajak.ebooklibrary.model.BookFormat
import com.mikesajak.ebooklibrary.model.BookFormatId
import com.mikesajak.ebooklibrary.model.BookFormatMetadata
import com.mikesajak.ebooklibrary.model.BookId

interface BookFormatStorageService {
    fun storeFormat(bookId: BookId, formatType: String, filename: String, contents: ByteArray): BookFormatId
    fun getFormat(formatId: BookFormatId): BookFormat?
    fun listFormatIds(bookId: BookId): List<BookFormatId>
    fun listFormatMetadata(bookId: BookId): List<BookFormatMetadata>
    fun removeFormat(formatId: BookFormatId): Boolean
    fun removeFormats(bookId:BookId): Int
    fun numFormats(): Long
}