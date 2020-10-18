package com.mikesajak.ebooklibrary.controller

import com.mikesajak.ebooklibrary.bookformat.BookFormatReaderRegistry
import com.mikesajak.ebooklibrary.controller.dto.BookFormatMetadataDto
import com.mikesajak.ebooklibrary.exceptions.BookFormatNotFoundException
import com.mikesajak.ebooklibrary.exceptions.BookFormatTypeException
import com.mikesajak.ebooklibrary.exceptions.BookNotFoundException
import com.mikesajak.ebooklibrary.model.BookFormatId
import com.mikesajak.ebooklibrary.model.BookFormatMetadata
import com.mikesajak.ebooklibrary.model.BookId
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.io.ByteArrayInputStream

@Suppress("unused")
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

    @PostMapping("/bookFormats")
    fun uploadTestFormat(@RequestParam("file") file: MultipartFile,
                         redirectAttributes: RedirectAttributes): String {
        val contentType = file.contentType ?: throw BookFormatTypeException("No content type for book format provided. ${file.name}")
        logger.info("Received contentType=$contentType")

        redirectAttributes.addFlashAttribute("message", "You successfully uploaded ${file.originalFilename}")
        return "redirect:/"
    }

    @PostMapping("/bookFormats/{bookId}")
    fun uploadBookFormat(@PathVariable("bookId") bookId: BookId,
                         @RequestParam("file") file: MultipartFile): String {
        val bookReader = bookFormatManager.readers.firstOrNull { it.canRead(ByteArrayInputStream(file.bytes)) }
            ?: throw BookFormatTypeException("Unsupported content type, cannot read book format")

        val bookMetadata = bookMetadataStorageService.getBook(bookId) ?: throw BookNotFoundException(bookId)

        // TODO: book format validation

        val filename = file.originalFilename ?: file.name

        val formatId = bookFormatStorageService.storeFormat(bookId, bookReader.mimeType, filename, file.bytes)

        logger.debug("uploadBookFormat (POST/bookFormats/$bookId), result: $formatId")

        return ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/bookFormats")
            .path(formatId.toString())
            .toUriString()
    }

    @GetMapping("bookFormats/{bookId}")
    fun getBookFormats(@PathVariable bookId: BookId): List<BookFormatMetadataDto> {
        val formatMetadatas = bookFormatStorageService.listFormatMetadata(bookId)
        logger.debug("getBookFormats (GET /bookFormats/$bookId, result: $formatMetadatas")
        return formatMetadatas.map { mkBookFormatMetadataDto(it) }
    }

    @GetMapping("bookFormats/{bookId}/{formatId}/contents")
    fun getBookFormatContents(@PathVariable("bookId") bookId: BookId,
                              @PathVariable("formatId") formatId: BookFormatId): ResponseEntity<ByteArray>? {
        logger.debug("getBookFormatContents (GET /bookFormats/$bookId/$formatId/contents")

        val bookFormat = bookFormatStorageService.getFormat(formatId)

        return if (bookFormat != null) {
            if (bookId != bookFormat.metadata.bookId)
                logger.warn("Data inconsistent for bookId=$bookId, formatId=$formatId: $bookFormat")

            logger.debug("Returning book format contents for bookId=$bookId, formatId=$formatId: ${bookFormat.contents}")

            ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(bookFormat.metadata.formatType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${bookFormat.metadata.filename}\"")
                .cacheControl(CacheControl.noCache())
                .body(bookFormat.contents)
        } else {
            logger.debug("Book format doesn't exist for bookId=$bookId, formatId=$formatId")
            throw BookFormatNotFoundException(bookId, formatId)
        }
    }

    @DeleteMapping("bookFormats/{bookId}/{bookFormatId}")
    fun deleteBookFormat(@PathVariable("bookId") bookId: BookId,
                         @PathVariable("bookFormatId") bookFormatId: BookFormatId) {
        logger.debug("deleteBookFormat (DELETE /bookFormats/$bookId/$bookFormatId")

        if (!bookFormatStorageService.removeFormat(bookFormatId)) {
            logger.debug("Book format doesn't exist, bookId=$bookId, formatId=$bookFormatId")
            throw BookFormatNotFoundException(bookId, bookFormatId)
        }
    }

    @DeleteMapping("bookFormats/{bookId}")
    fun deleteAllBookFormats(@PathVariable("bookId") bookId: BookId) {
        val numDeleted = bookFormatStorageService.listFormatIds(bookId)
            .map { bookFormatStorageService.removeFormat(it) }
            .count { bookDeleted -> bookDeleted }

        logger.debug("deleteAllBookFormats (DELETE /bookFormats/$bookId, num deleted: $numDeleted")

        if (numDeleted == 0)
            throw BookFormatNotFoundException(bookId)
    }

    private fun mkBookFormatMetadataDto(metadata: BookFormatMetadata) =
        BookFormatMetadataDto(metadata.bookFormatId, metadata.bookId, metadata.formatType, metadata.size)
}