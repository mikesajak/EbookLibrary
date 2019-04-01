package com.mikesajak.ebooklibrary.storage

import com.mikesajak.ebooklibrary.payload.BookDataId
import com.mikesajak.ebooklibrary.payload.BookFormat
import com.mikesajak.ebooklibrary.payload.BookId

interface BookFormatStorageService {
    fun storeBookFormat(format: BookFormat)
    fun getBookFormats(bookId: BookId): List<BookDataId>
    fun getBookFormat(bookId: BookId, bookDataId: BookDataId): BookFormat
    fun listBookFormats(): List<BookId>
    fun removeBookFormat(bookId: BookId, bookDataId: BookDataId): Boolean
    fun removeBookFormats(bookId: BookId): Boolean
}