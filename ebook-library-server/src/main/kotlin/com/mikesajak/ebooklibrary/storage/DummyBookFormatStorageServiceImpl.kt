package com.mikesajak.ebooklibrary.storage

import com.mikesajak.ebooklibrary.payload.BookFormat
import com.mikesajak.ebooklibrary.payload.BookFormatId
import com.mikesajak.ebooklibrary.payload.BookFormatMetadataDto
import com.mikesajak.ebooklibrary.payload.BookId
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

    override fun deleteFormat(formatId: BookFormatId): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}