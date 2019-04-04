package com.mikesajak.ebooklibrary.controller

import com.mikesajak.ebooklibrary.exceptions.BookNotFoundException
import com.mikesajak.ebooklibrary.exceptions.InvalidRequestException
import com.mikesajak.ebooklibrary.payload.Book
import com.mikesajak.ebooklibrary.payload.BookMetadata
import com.mikesajak.ebooklibrary.payload.BookId
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
    fun getBook(@RequestParam("book") bookDescr: BookMetadata): String {
        val bookId = bookMetadataStorageService.addBook(bookDescr)

        val bookUri = ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/books")
            .path(bookId.toString())
            .toUriString()

        return bookUri
    }

    @PutMapping("/books/{bookId}")
    fun updateBook(@PathVariable bookId: BookId, @RequestParam("book") book: Book) {
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

        return ResponseEntity.of(Optional.ofNullable(book))
    }

    @GetMapping("books")
    fun getBookIds() : ResponseEntity<List<Book>> {
        val books = bookMetadataStorageService.listBooks()
        return ResponseEntity.ok(books)
    }

}