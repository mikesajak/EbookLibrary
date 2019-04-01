package com.mikesajak.ebooklibrary.storage

import com.mikesajak.ebooklibrary.payload.BookCover
import com.mikesajak.ebooklibrary.payload.BookId

interface BookCoverStorageService {
    fun storeCover(cover: BookCover)
    fun getCover(bookId: BookId): BookCover
    fun listCovers(): List<BookId>
    fun deleteCover(bookId: BookId): Boolean
}