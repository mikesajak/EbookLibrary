package com.mikesajak.ebooklibrary.controller

import com.mikesajak.ebooklibrary.controller.dto.BookDto
import com.mikesajak.ebooklibrary.controller.dto.BookDtoConverter
import com.mikesajak.ebooklibrary.exceptions.BookAlreadyExistsException
import com.mikesajak.ebooklibrary.exceptions.BookNotFoundException
import com.mikesajak.ebooklibrary.exceptions.InvalidRequestException
import com.mikesajak.ebooklibrary.model.BookId
import com.mikesajak.ebooklibrary.storage.BookFormatStorageService
import com.mikesajak.ebooklibrary.storage.BookMetadataStorageService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Suppress("unused")
@RestController
class BooksController {
    private val logger = LoggerFactory.getLogger(BooksController::class.java)

    @Autowired
    private lateinit var bookMetadataStorageService: BookMetadataStorageService

    @Autowired
    @Qualifier("bookFormatAsFileStorageService")
    private lateinit var bookFormatStorageService: BookFormatStorageService

    @Autowired
    private lateinit var bookDtoConverter: BookDtoConverter

    @PostMapping("/books")
    fun addBook(@RequestBody bookDto: BookDto): String {

        if (bookDto.id != null) {
            val book = bookMetadataStorageService.getBook(BookId(bookDto.id))
            if (book != null) {
                throw BookAlreadyExistsException(bookDto.id)
            }
        }

        val booksWithMatchingTitle = bookMetadataStorageService.findBooksWithTitle(bookDto.title, exact=true)
        if (booksWithMatchingTitle.isNotEmpty()) {
            throw BookAlreadyExistsException(booksWithMatchingTitle[0].id.value)
        }

        val book = bookDtoConverter.mkBookMetadata(bookDto)
        val bookId = bookMetadataStorageService.addBook(book)

        logger.debug("addBook (POST /books) with $bookDto, result: $bookId")
        return bookId.value
    }

    @PutMapping("/books/{bookId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateBook(@PathVariable bookId: String, @RequestBody bookDto: BookDto) = updateBook(BookId(bookId), bookDto)

    private fun updateBook(bookId: BookId, bookDto: BookDto) {
        logger.debug("updateBook (PUT /books/$bookId) with $bookDto")
        if (bookId.value != bookDto.id) {
            throw InvalidRequestException("The bookId=$bookId is inconsistent with the book id provided in book data (id=${bookDto.id})")
        }
        if (bookMetadataStorageService.getBook(bookId) == null) {
            throw BookNotFoundException(bookId)
        }

        val bookToUpdate = bookDtoConverter.mkBook(bookId, bookDto)
        bookMetadataStorageService.updateBook(bookToUpdate)
    }

    @GetMapping("books/{bookId}")
    fun getBook(@PathVariable bookId: String): BookDto = getBook(BookId(bookId))

    private fun getBook(bookId: BookId): BookDto {
        val book = bookMetadataStorageService.getBook(bookId)

        logger.debug("getBook (GET /books/$bookId), result: $book")

        if (book == null) {
            throw BookNotFoundException(bookId)
        }

        val formats = bookFormatStorageService.listFormatMetadata(bookId)
        logger.debug("Returning book formats for bookId=$bookId: $formats")

        return bookDtoConverter.mkBookDto(book, formats)
    }

    @DeleteMapping("books/{bookId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteBook(@PathVariable bookId: String) = deleteBook(BookId(bookId))

    private fun deleteBook(bookId: BookId) {
        val book = bookMetadataStorageService.getBook(bookId)

        logger.debug("deleteBook (DELETE /books/$bookId), result: $book")

        if (book == null) {
            throw BookNotFoundException(bookId)
        }

        val numFormatsRemoved = bookFormatStorageService.removeFormats(bookId)
        logger.debug("Remove all book formats for bookId=$bookId. Num removed=$numFormatsRemoved")

        val numRemoved = bookMetadataStorageService.removeBook(bookId)
        logger.debug("Remove book with bookId: $bookId. Num removed=$numRemoved")
    }

    @GetMapping("books")
    fun getBooks(@RequestParam(required = false) query: String?): List<BookDto> {
        val books = if (query == null) bookMetadataStorageService.listBooks()
        else bookMetadataStorageService.findBooks(query)
        logger.debug("getBooks (GET /books?query=$query), result: $books")

        return books.map { book ->
            val bookFormats = bookFormatStorageService.listFormatMetadata(book.id)
            bookDtoConverter.mkBookDto(book, bookFormats)
        }
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
