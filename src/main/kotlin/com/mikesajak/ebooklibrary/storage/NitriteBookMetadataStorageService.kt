package com.mikesajak.ebooklibrary.storage

import com.mikesajak.ebooklibrary.payload.Book
import com.mikesajak.ebooklibrary.payload.BookMetadata
import com.mikesajak.ebooklibrary.payload.BookId
import org.dizitart.no2.IndexOptions
import org.dizitart.no2.IndexType
import org.dizitart.no2.objects.ObjectRepository
import org.dizitart.no2.objects.filters.ObjectFilters.eq
import org.springframework.stereotype.Service

@Service
class NitriteBookMetadataStorageService(nitriteDbService: NitriteDbService) : BookMetadataStorageService {
    private val bookRepo: ObjectRepository<Book>

    init {
        bookRepo = nitriteDbService.db.getRepository(Book::class.java)

        if (!bookRepo.hasIndex("id")) {
            bookRepo.createIndex("id", IndexOptions.indexOptions(IndexType.Unique))
        }
    }

    override fun addBook(bookMetadata: BookMetadata): BookId {
        val id = BookId.randomBookId()
        val book = Book(id, bookMetadata)

        bookRepo.insert(book)

        return id
    }

    override fun updateBook(book: Book) {
        bookRepo.update(book, true)
    }

    override fun getBook(id: BookId): Book? {
        val cursor = bookRepo.find(eq("id", id))
        return cursor.first()
    }

    override fun listBooks(): List<Book> {
        return bookRepo.find().toList()
    }
}
