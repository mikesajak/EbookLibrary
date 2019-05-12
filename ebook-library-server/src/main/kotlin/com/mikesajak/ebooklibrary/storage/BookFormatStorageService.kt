package com.mikesajak.ebooklibrary.storage

import com.mikesajak.ebooklibrary.payload.BookFormatDto
import com.mikesajak.ebooklibrary.payload.BookFormatId
import com.mikesajak.ebooklibrary.payload.BookId

interface BookFormatStorageService {
    fun storeBookFormat(format: BookFormatDto)
    fun getBookFormats(bookId: BookId): List<BookFormatId>
    fun getBookFormat(bookId: BookId, bookFormatId: BookFormatId): BookFormatDto
    fun listBookFormats(): List<BookId>
    fun removeBookFormat(bookId: BookId, bookFormatId: BookFormatId): Boolean
    fun removeBookFormats(bookId: BookId): Boolean
}