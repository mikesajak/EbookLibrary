package com.mikesajak.ebooklibrary.storage

import com.mikesajak.ebooklibrary.model.Book
import com.mikesajak.ebooklibrary.model.BookId
import com.mikesajak.ebooklibrary.model.BookMetadata

interface BookMetadataStorageService {
    fun addBook(bookMetadata: BookMetadata): BookId
    fun updateBook(book: Book)
    fun getBook(id: BookId): Book?
    fun removeBook(id: BookId): Int
    fun listBooks(): List<Book>
    fun findBooks(query: String?): List<Book>
    fun findBooksWithTitle(title: String, exact: Boolean): List<Book>
    fun findBooksWithAuthor(author: String, exact: Boolean): List<Book>
    fun findBooksWithTag(tag: String): List<Book>
    fun numBooks(): Long
}
