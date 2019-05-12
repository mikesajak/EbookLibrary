package com.mikesajak.ebooklibrary.controller

import com.mikesajak.ebooklibrary.bookformat.BookFormatReaderRegistry
import com.mikesajak.ebooklibrary.exceptions.BookFormatNotFoundException
import com.mikesajak.ebooklibrary.exceptions.BookFormatTypeException
import com.mikesajak.ebooklibrary.exceptions.BookNotFoundException
import com.mikesajak.ebooklibrary.payload.BookFormat
import com.mikesajak.ebooklibrary.payload.BookFormatDto
import com.mikesajak.ebooklibrary.payload.BookFormatId
import com.mikesajak.ebooklibrary.payload.BookId
import com.mikesajak.ebooklibrary.storage.BookFormatStorageService
import com.mikesajak.ebooklibrary.storage.BookMetadataStorageService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
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

        val bookDataId = BookFormatId.randomId()
        bookFormatStorageService.storeBookFormat(BookFormatDto(bookId,
            BookFormat(bookDataId, contentType, filename, file.bytes)))

        val fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/bookFormats")
            .path(bookId.toString())
            .toUriString()

        return fileDownloadUri
    }

    @GetMapping("bookFormats/{bookId}")
    fun getBookFormats(@PathVariable bookId: BookId): List<BookFormatId> {
        return bookFormatStorageService.getBookFormats(bookId)
    }

    @GetMapping("bookFormats/{bookId}/{bookFormatId}")
    fun getBookFormatData(@PathVariable("bookId") bookId: BookId,
                          @PathVariable("bookFormatId") bookFormatId: BookFormatId): ResponseEntity<ByteArray> {

        val bookFormat = bookFormatStorageService.getBookFormat(bookId, bookFormatId)

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(bookFormat.bookFormat.type))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${bookFormat.bookFormat.filename}\"")
            .cacheControl(CacheControl.noCache())
            .body(bookFormat.bookFormat.data)
    }

    @DeleteMapping("bookFormats/{bookId}/{bookFormatId}")
    fun deleteBookFormat(@PathVariable("bookId") bookId: BookId,
                         @PathVariable("bookFormatId") bookFormatId: BookFormatId) {
        if (!bookFormatStorageService.removeBookFormat(bookId, bookFormatId))
            throw BookFormatNotFoundException(bookId, bookFormatId)
    }

    @DeleteMapping("bookFormats/{bookId}")
    fun deleteBookFormats(@PathVariable("bookId") bookId: BookId) {
        if (!bookFormatStorageService.removeBookFormats(bookId)) {
            throw BookNotFoundException(bookId)
        }
    }

    @GetMapping("bookFormats")
    fun listBookFormats(): List<BookId> {
        return bookFormatStorageService.listBookFormats()
    }
}