package com.mikesajak.ebooklibrary.controller

import com.mikesajak.ebooklibrary.bookformat.BookFormatManager
import com.mikesajak.ebooklibrary.exceptions.BookFormatNotFoundException
import com.mikesajak.ebooklibrary.exceptions.BookFormatTypeException
import com.mikesajak.ebooklibrary.exceptions.BookNotFoundException
import com.mikesajak.ebooklibrary.payload.BookData
import com.mikesajak.ebooklibrary.payload.BookDataId
import com.mikesajak.ebooklibrary.payload.BookFormat
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
    lateinit var bookFormatManager: BookFormatManager

    @PostMapping("/bookFormats/{bookId}")
    fun uploadBookFormat(@PathVariable("bookId") bookId: BookId,
                         @RequestParam("file") file: MultipartFile): String {

        val contentType = file.contentType ?: throw BookFormatTypeException("No content type for book format provided. ${file.name}")
        val bookReader = bookFormatManager.readerFor(contentType) ?: throw BookFormatTypeException("Unsupported content type for ")
        val bookMetadata = bookMetadataStorageService.getBook(bookId) ?: throw BookNotFoundException(bookId)

        // TODO: book format validation

        val filename = file.originalFilename ?: file.name

        val bookDataId = BookDataId.randomId()
        bookFormatStorageService.storeBookFormat(BookFormat(bookId,
            BookData(bookDataId, contentType, filename, file.bytes)))

        val fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/bookFormats")
            .path(bookId.toString())
            .toUriString()

        return fileDownloadUri
    }

    @GetMapping("bookFormats/{bookId}")
    fun getBookFormats(@PathVariable bookId: BookId): List<BookDataId> {
        return bookFormatStorageService.getBookFormats(bookId)
    }

    @GetMapping("bookFormats/{bookId}/{bookDataId}")
    fun getBookFormatData(@PathVariable("bookId") bookId: BookId,
                          @PathVariable("bookDataId") bookDataId: BookDataId): ResponseEntity<ByteArray> {

        val bookFormat = bookFormatStorageService.getBookFormat(bookId, bookDataId)

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(bookFormat.bookData.type))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${bookFormat.bookData.filename}\"")
            .cacheControl(CacheControl.noCache())
            .body(bookFormat.bookData.data)
    }

    @DeleteMapping("bookFormats/{bookId}/{bookDataId}")
    fun deleteBookFormat(@PathVariable("bookId") bookId: BookId,
                         @PathVariable("bookDataId") bookDataId: BookDataId) {
        if (!bookFormatStorageService.removeBookFormat(bookId, bookDataId))
            throw BookFormatNotFoundException(bookId, bookDataId)
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