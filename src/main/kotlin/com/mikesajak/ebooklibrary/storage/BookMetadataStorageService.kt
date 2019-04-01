package com.mikesajak.ebooklibrary.storage

import com.mikesajak.ebooklibrary.payload.Book
import com.mikesajak.ebooklibrary.payload.BookDescriptor
import com.mikesajak.ebooklibrary.payload.BookId

interface BookMetadataStorageService {
    fun addBook(bookDescriptor: BookDescriptor): BookId
    fun updateBook(book: Book)
    fun getBook(id: BookId): Book?
    fun listBooks(): List<Book>
}