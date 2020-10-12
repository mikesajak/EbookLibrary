package com.mikesajak.ebooklibrary.storage.nitrite

import com.mikesajak.ebooklibrary.model.Book
import com.mikesajak.ebooklibrary.model.BookId
import com.mikesajak.ebooklibrary.model.BookMetadata
import com.mikesajak.ebooklibrary.storage.BookMetadataStorageService
import org.dizitart.no2.IndexOptions
import org.dizitart.no2.IndexType
import org.dizitart.no2.objects.ObjectRepository
import org.dizitart.no2.objects.filters.ObjectFilters.*
import org.springframework.stereotype.Service

@Service
class NitriteBookMetadataStorageService(nitriteDbService: NitriteDbService,
                                        private val queryParser: RSQLToNitriteQueryParser) : BookMetadataStorageService {

    @Suppress("JoinDeclarationAndAssignment")
    private val bookRepo: ObjectRepository<Book>

    init {
        bookRepo = nitriteDbService.db.getRepository(Book::class.java)

        if (!bookRepo.hasIndex("id.value")) {
            bookRepo.createIndex("id.value", IndexOptions.indexOptions(IndexType.Unique))
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
        val cursor = bookRepo.find(eq("id.value", id.value))
        return cursor.singleOrNull()
    }

    override fun removeBook(id: BookId): Int {
        val remove = bookRepo.remove(eq("id.value", id.value))
        return remove.affectedCount
    }

    override fun listBooks(): List<Book> {
        return bookRepo.find()
            .toList()
    }

    override fun findBooks(query: String?): List<Book> =
        if (query == null || query.isBlank()) listBooks()
        else queryBooks(query)

    private fun queryBooks(query: String): List<Book> =
        bookRepo.find(queryParser.parse(query)).toList()

    override fun findBooksWithTitle(title: String, exact: Boolean): List<Book> {
        return bookRepo.find(
            if (exact) eq("metadata.title", title) else regex("metadata.title", title))
            .toList()
    }

    override fun findBooksWithAuthor(author: String, exact: Boolean): List<Book> {
        return bookRepo.find(elemMatch("metadata.authors",
            if (exact) eq("$", author) else regex("$", author))).toList()
    }

    override fun findBooksWithTag(tag: String): List<Book> {
        return bookRepo.find(elemMatch("metadata.tags", eq("$",tag)))
            .toList()
    }

    override fun numBooks(): Long = bookRepo.size()
}
