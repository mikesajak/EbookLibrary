package com.mikesajak.ebooklibrary.storage

import com.mikesajak.ebooklibrary.model.BookFormat
import com.mikesajak.ebooklibrary.model.BookFormatId
import com.mikesajak.ebooklibrary.model.BookFormatMetadataDto
import com.mikesajak.ebooklibrary.model.BookId
import org.springframework.stereotype.Service

@Service
class DummyBookFormatStorageServiceImpl : BookFormatStorageService {
    override fun listFormatIds(bookId: BookId): List<BookFormatId> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun listFormatMetadata(bookId: BookId): List<BookFormatMetadataDto> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun storeFormat(format: BookFormat): BookFormatId {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getFormat(formatId: BookFormatId): BookFormat? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun numFormats(): Long {
        TODO("Not yet implemented")
    }

    override fun listFormats(): List<BookId> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun removeFormat(formatId: BookFormatId): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun removeFormats(bookId: BookId): Int {
        TODO("Not yet implemented")
    }
}