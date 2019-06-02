package com.mikesajak.ebooklibrary.controller

import com.mikesajak.ebooklibrary.bookformat.BookFormatReaderRegistry
import com.mikesajak.ebooklibrary.exceptions.BookFormatNotFoundException
import com.mikesajak.ebooklibrary.exceptions.BookFormatTypeException
import com.mikesajak.ebooklibrary.exceptions.BookNotFoundException
import com.mikesajak.ebooklibrary.payload.BookFormat
import com.mikesajak.ebooklibrary.payload.BookFormatId
import com.mikesajak.ebooklibrary.payload.BookFormatMetadata
import com.mikesajak.ebooklibrary.payload.BookId
import com.mikesajak.ebooklibrary.storage.BookFormatStorageService
import com.mikesajak.ebooklibrary.storage.BookMetadataStorageService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.CacheControl
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

@RestController
class BookFormatController {
    private val logger = LoggerFactory.getLogger(BookFormatController::class.java)

    @Autowired
    lateinit var bookMetadataStorageService: BookMetadataStorageService

    @Autowired
    @Qualifier("bookFormatAsFileStorageService")
    lateinit var bookFormatStorageService: BookFormatStorageService

    @Autowired
    lateinit var bookFormatManager: BookFormatReaderRegistry

    @PostMapping("/bookFormats/{bookId}")
    fun uploadBookFormat(@PathVariable("bookId") bookId: BookId,
                         @RequestParam("file") file: MultipartFile): String {

        val contentType = file.contentType ?: throw BookFormatTypeException("No content type for book format provided. ${file.name}")
        val bookReader = bookFormatManager.readerFor(contentType) ?: throw BookFormatTypeException("Unsupported content type for ")
        val bookMetadata = bookMetadataStorageService.getBook(bookId) ?: throw BookNotFoundException(bookId)

        // TODO: book format validation

        val filename = file.originalFilename ?: file.name

        val formatId = bookFormatStorageService.storeFormat(
            BookFormat(BookFormatMetadata(bookId, contentType, filename), file.bytes))

        return ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/bookFormats")
            .path(formatId.toString())
            .toUriString()
    }

    @GetMapping("bookFormats/{bookId}")
    fun getBookFormats(@PathVariable bookId: BookId): List<BookFormatId> {
        return bookFormatStorageService.listFormatIds(bookId)
    }

    @GetMapping("bookFormats/{bookId}/{formatId}/contents")
    fun getBookFormatContents(@PathVariable("bookId") bookId: BookId,
                              @PathVariable("formatId") formatId: BookFormatId): ResponseEntity<ByteArray>? {

        val bookFormat = bookFormatStorageService.getFormat(formatId)

        return if (bookFormat != null) {
            if (bookId != bookFormat.metadata.bookId)
                logger.warn("Data inconsistent for bookId=$bookId, formatId=$formatId: $bookFormat")

            ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(bookFormat.metadata.type))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${bookFormat.metadata.filename}\"")
                .cacheControl(CacheControl.noCache())
                .body(bookFormat.contents)
        } else throw BookFormatNotFoundException(bookId, formatId)
    }

    @GetMapping("bookFormats/{bookId}/{formatId}/metadata")
    fun getBookFormatMetadata(@PathVariable("bookId") bookId: BookId,
                              @PathVariable("formatId") formatId: BookFormatId): BookFormatMetadata? {

        val bookFormat = bookFormatStorageService.getFormat(formatId)

        if (bookFormat != null) {
            if (bookId != bookFormat.metadata.bookId)
                logger.warn("Data inconsistent for bookId=$bookId, formatId=$formatId: $bookFormat")

            return bookFormat.metadata
        } else throw BookFormatNotFoundException(bookId, formatId)
    }

    @DeleteMapping("bookFormats/{bookId}/{bookFormatId}")
    fun deleteBookFormat(@PathVariable("bookId") bookId: BookId,
                         @PathVariable("bookFormatId") bookFormatId: BookFormatId) {
        if (!bookFormatStorageService.deleteFormat(bookFormatId))
            throw BookFormatNotFoundException(bookId, bookFormatId)
    }

    @DeleteMapping("bookFormats/{bookId}")
    fun deleteBookFormats(@PathVariable("bookId") bookId: BookId) {
        val numDeleted = bookFormatStorageService.listFormatIds(bookId)
            .map { bookFormatStorageService.deleteFormat(it) }
            .count { it == true}

        if (numDeleted == 0)
            throw BookFormatNotFoundException(bookId)
    }

    @GetMapping("bookFormats")
    fun listBookFormats(): List<BookId> {
        return bookFormatStorageService.listFormats()
    }
}