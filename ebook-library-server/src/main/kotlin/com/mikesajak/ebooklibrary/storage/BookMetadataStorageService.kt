package com.mikesajak.ebooklibrary.storage

import com.mikesajak.ebooklibrary.payload.Book
import com.mikesajak.ebooklibrary.payload.BookId
import com.mikesajak.ebooklibrary.payload.BookMetadata

interface BookMetadataStorageService {
    fun addBook(bookMetadata: BookMetadata): BookId
    fun updateBook(book: Book)
    fun getBook(id: BookId): Book?
    fun listBooks(): List<Book>
    fun findBooks(query: String?): List<Book>
    fun numBooks(): Long
}