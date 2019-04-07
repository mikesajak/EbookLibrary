package com.mikesajak.ebooklibrary.storage

import com.mikesajak.ebooklibrary.payload.BookCover
import com.mikesajak.ebooklibrary.payload.BookId
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
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

    override fun listCovers(): List<BookId> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteCover(bookId: BookId): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}