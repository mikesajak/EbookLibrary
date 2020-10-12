package com.mikesajak.ebooklibrary.storage

import com.mikesajak.ebooklibrary.model.BookFormat
import com.mikesajak.ebooklibrary.model.BookFormatId
import com.mikesajak.ebooklibrary.model.BookFormatMetadataDto
import com.mikesajak.ebooklibrary.model.BookId

interface BookFormatStorageService {
//    fun storeBookFormat(format: BookFormatDto)
//    fun getBookFormats(bookId: BookId): List<BookFormatId>
//    fun getBookFormat(bookId: BookId, bookFormatId: BookFormatId): BookFormatDto
//    fun listBookFormats(): List<BookId>
//    fun removeBookFormat(bookId: BookId, bookFormatId: BookFormatId): Boolean
//    fun removeBookFormats(bookId: BookId): Boolean

    fun storeFormat(format: BookFormat): BookFormatId
    fun getFormat(formatId: BookFormatId): BookFormat?
    fun listFormatIds(bookId: BookId): List<BookFormatId>
    fun listFormatMetadata(bookId: BookId): List<BookFormatMetadataDto>
    fun listFormats(): List<BookId>
    fun removeFormat(formatId: BookFormatId): Boolean
    fun removeFormats(bookId:BookId): Int
    fun numFormats(): Long
}