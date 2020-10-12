package com.mikesajak.ebooklibrary.storage

import com.mikesajak.ebooklibrary.model.BookCover
import com.mikesajak.ebooklibrary.model.BookId

interface BookCoverStorageService {
    fun storeCover(cover: BookCover)
    fun getCover(bookId: BookId): BookCover?
    fun numCovers(): Long
    fun listCovers(): List<BookId>
    fun deleteCover(bookId: BookId): Boolean
}