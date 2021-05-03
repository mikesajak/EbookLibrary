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
import org.springframework.stereotype.Service

@Service
class BooksService {
    private val logger = LoggerFactory.getLogger(BooksService::class.java)

    @Autowired
    private lateinit var bookMetadataStorageService: BookMetadataStorageService

    @Autowired
    @Qualifier("bookFormatAsFileStorageService")
    private lateinit var bookFormatStorageService: BookFormatStorageService

    @Autowired
    private lateinit var bookDtoConverter: BookDtoConverter

    fun getBook(bookId: BookId): BookDto {
        val book = bookMetadataStorageService.getBook(bookId) ?: throw BookNotFoundException(bookId)
        logger.debug("Book found for bookId=$bookId: $book")

        val formats = bookFormatStorageService.listFormatMetadata(bookId)
        logger.debug("Book formats for bookId=$bookId: $formats")

        return bookDtoConverter.mkBookDto(book, formats)
    }

    fun addBook(bookDto: BookDto): BookId {
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

        return bookId
    }

    fun deleteBook(bookId: BookId) {
        val book = bookMetadataStorageService.getBook(bookId)
        logger.debug("Found book for bookId=$bookId: $book")

        if (book == null) {
            throw BookNotFoundException(bookId)
        }

        val numFormatsRemoved = bookFormatStorageService.removeFormats(bookId)
        logger.debug("Remove all book formats for bookId=$bookId. Num removed=$numFormatsRemoved")

        val numRemoved = bookMetadataStorageService.removeBook(bookId)
        logger.debug("Remove book with bookId: $bookId. Num removed=$numRemoved")
    }

    fun updateBook(bookId: BookId, bookDto: BookDto) {
        if (bookId.value != bookDto.id) {
            throw InvalidRequestException("The bookId=$bookId is inconsistent with the book id provided in book data (id=${bookDto.id})")
        }

        if (bookMetadataStorageService.getBook(bookId) == null) {
            throw BookNotFoundException(bookId)
        }

        val bookToUpdate = bookDtoConverter.mkBook(bookId, bookDto)
        bookMetadataStorageService.updateBook(bookToUpdate)
    }

    fun getBooks(query: String?): List<BookDto> {
        val books = findBooks(query)
        logger.debug("Found books for query=$query: $books")

        return books.map { book ->
            val bookFormats = bookFormatStorageService.listFormatMetadata(book.id)
            bookDtoConverter.mkBookDto(book, bookFormats)
        }
    }

    fun getBookIds(query: String?): List<BookId> {
        val books = findBooks(query)
        logger.debug("Found books for query=$query: $books")

        return books.map { it.id }
    }

    private fun findBooks(query: String?) =
        if (query == null) bookMetadataStorageService.listBooks()
        else bookMetadataStorageService.findBooks(query)


}