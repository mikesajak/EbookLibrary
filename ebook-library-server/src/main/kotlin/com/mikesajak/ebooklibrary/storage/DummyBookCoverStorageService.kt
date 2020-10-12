package com.mikesajak.ebooklibrary.storage

import com.mikesajak.ebooklibrary.model.BookCover
import com.mikesajak.ebooklibrary.model.BookId
import org.slf4j.LoggerFactory

//@Service
class DummyBookCoverStorageService : BookCoverStorageService {
    companion object {
        val logger = LoggerFactory.getLogger(DummyBookCoverStorageService::class.java)
    }
    override fun storeCover(cover: BookCover) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getCover(bookId: BookId): BookCover {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun numCovers(): Long {
        TODO("Not yet implemented")
    }

    override fun listCovers(): List<BookId> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteCover(bookId: BookId): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}