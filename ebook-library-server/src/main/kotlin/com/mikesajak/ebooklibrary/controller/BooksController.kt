package com.mikesajak.ebooklibrary.controller

import com.mikesajak.ebooklibrary.exceptions.BookNotFoundException
import com.mikesajak.ebooklibrary.exceptions.InvalidRequestException
import com.mikesajak.ebooklibrary.payload.Book
import com.mikesajak.ebooklibrary.payload.BookId
import com.mikesajak.ebooklibrary.payload.BookMetadata
import com.mikesajak.ebooklibrary.storage.BookMetadataStorageService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.util.*

@RestController
class BooksController {
    private val logger = LoggerFactory.getLogger(BooksController::class.java)

    @Autowired
    lateinit var bookMetadataStorageService: BookMetadataStorageService

    @PostMapping("/books")
    fun addBook(@RequestBody bookDescr: BookMetadata): String {
        val bookId = bookMetadataStorageService.addBook(bookDescr)

        val bookUri = ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/books")
            .path(bookId.toString())
            .toUriString()

        logger.debug("addBook (POST /books) with $bookDescr, result: $bookUri")
        return bookUri
    }

    @PutMapping("/books/{bookId}")
    fun updateBook(@PathVariable bookId: BookId, @RequestBody book: Book) {
        logger.debug("updateBook (PUT /books/$bookId) with $book")
        if (bookId != book.id) {
            throw InvalidRequestException("The bookId=$bookId is inconsistent with the book id provided in book data (id=${book.id})")
        }
        if (bookMetadataStorageService.getBook(bookId) == null) {
            throw BookNotFoundException(bookId)
        }

        bookMetadataStorageService.updateBook(book)
    }

    @GetMapping("books/{bookId}")
    fun getBook(@PathVariable bookId: BookId): ResponseEntity<Book> {
        val book = bookMetadataStorageService.getBook(bookId)

        logger.debug("getBook (GET /books/$bookId), result: $book")
        return ResponseEntity.of(Optional.ofNullable(book))
    }

    @GetMapping("books")
    fun getBooks(@RequestParam(required = false) query: String?) : ResponseEntity<List<Book>> {
        val books = if (query == null) bookMetadataStorageService.listBooks()
                    else bookMetadataStorageService.findBooks(query)
        logger.debug("getBookIds (GET /books?query=$query), result: $books")
        return ResponseEntity.ok(books)
    }

    @GetMapping("bookIds")
    fun getBookIds(@RequestParam(required = false) query: String?) : ResponseEntity<List<BookId>> {
        val books = if (query == null) bookMetadataStorageService.listBooks()
                    else bookMetadataStorageService.findBooks(query)
        val bookIds = books.map { it.id }
        logger.debug("getBookIds (GET /books?query=$query), result: $books")
        return ResponseEntity.ok(bookIds)
    }
}