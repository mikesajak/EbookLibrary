package com.mikesajak.ebooklibrary.storage

import com.mikesajak.ebooklibrary.payload.BookDataId
import com.mikesajak.ebooklibrary.payload.BookFormat
import com.mikesajak.ebooklibrary.payload.BookId
import org.springframework.stereotype.Service

@Service
class DummyBookFormatStorageServiceImpl : BookFormatStorageService {
    override fun storeBookFormat(format: BookFormat) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getBookFormats(bookId: BookId): List<BookDataId> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getBookFormat(bookId: BookId, bookDataId: BookDataId): BookFormat {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun listBookFormats(): List<BookId> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun removeBookFormat(bookId: BookId, bookDataId: BookDataId): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun removeBookFormats(bookId: BookId): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}