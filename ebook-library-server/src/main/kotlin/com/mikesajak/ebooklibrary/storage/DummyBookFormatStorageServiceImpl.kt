package com.mikesajak.ebooklibrary.storage

import com.mikesajak.ebooklibrary.payload.BookFormatDto
import com.mikesajak.ebooklibrary.payload.BookFormatId
import com.mikesajak.ebooklibrary.payload.BookId
import org.springframework.stereotype.Service

@Service
class DummyBookFormatStorageServiceImpl : BookFormatStorageService {
    override fun storeBookFormat(format: BookFormatDto) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getBookFormats(bookId: BookId): List<BookFormatId> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getBookFormat(bookId: BookId, bookFormatId: BookFormatId): BookFormatDto {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun listBookFormats(): List<BookId> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun removeBookFormat(bookId: BookId, bookFormatId: BookFormatId): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun removeBookFormats(bookId: BookId): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}