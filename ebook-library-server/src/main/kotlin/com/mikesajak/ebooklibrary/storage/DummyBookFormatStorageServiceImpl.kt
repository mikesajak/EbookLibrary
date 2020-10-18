package com.mikesajak.ebooklibrary.storage

import com.mikesajak.ebooklibrary.model.BookFormat
import com.mikesajak.ebooklibrary.model.BookFormatId
import com.mikesajak.ebooklibrary.model.BookFormatMetadata
import com.mikesajak.ebooklibrary.model.BookId
import org.springframework.stereotype.Service

@Suppress("unused")
@Service
class DummyBookFormatStorageServiceImpl : BookFormatStorageService {
    override fun listFormatIds(bookId: BookId): List<BookFormatId> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun listFormatMetadata(bookId: BookId): List<BookFormatMetadata> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun storeFormat(bookId: BookId, formatType: String, filename: String, contents: ByteArray): BookFormatId {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getFormat(formatId: BookFormatId): BookFormat? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun numFormats(): Long {
        TODO("Not yet implemented")
    }

    override fun removeFormat(formatId: BookFormatId): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun removeFormats(bookId: BookId): Int {
        TODO("Not yet implemented")
    }
}