package com.mikesajak.ebooklibrary.controller

import com.mikesajak.ebooklibrary.bookformat.BookFormatReaderRegistry
import com.mikesajak.ebooklibrary.bookformat.BookMetadataReader
import com.mikesajak.ebooklibrary.controller.dto.BookDtoConverter
import com.mikesajak.ebooklibrary.controller.dto.BookFormatMetadataDto
import com.mikesajak.ebooklibrary.exceptions.BookFormatNotFoundException
import com.mikesajak.ebooklibrary.exceptions.BookFormatTypeException
import com.mikesajak.ebooklibrary.exceptions.BookNotFoundException
import com.mikesajak.ebooklibrary.model.BookFormatId
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
    private lateinit var bookMetadataStorageService: BookMetadataStorageService

    @Autowired
    @Qualifier("bookFormatAsFileStorageService")
    private lateinit var bookFormatStorageService: BookFormatStorageService

    @Autowired
    private lateinit var bookFormatManager: BookFormatReaderRegistry

    @Autowired
    private lateinit var bookDtoConverter: BookDtoConverter

    @PostMapping("/bookFormats")
    fun uploadTestFormat(@RequestParam("file") file: MultipartFile,
                         redirectAttributes: RedirectAttributes): String {
        val contentType = file.contentType ?: throw BookFormatTypeException("No content type for book format provided. ${file.name}")
        logger.info("Received contentType=$contentType")

        redirectAttributes.addFlashAttribute("message", "You successfully uploaded ${file.originalFilename}")
        return "redirect:/"
    }

    private var dryRun = true

    @PostMapping("/bookFormats/{bookId}")
    fun uploadBookFormat(@PathVariable("bookId") bookId: BookId,
//                         @RequestParam("formatMetadata") bookFormatMetadataDto: BookFormatMetadataDto?,
                         @RequestParam("formatType") formatType: String?,
                         @RequestParam("file") file: MultipartFile): String {
        val endpointId = "uploadBookFormat (POST/bookFormats/$bookId)"

//        val mimeType = bookFormatMetadataDto?.formatType ?: getBookMetadataReader(file).mimeType
        val mimeType = formatType ?: getBookMetadataReader(file).mimeType

        val bookMetadata = bookMetadataStorageService.getBook(bookId) ?: throw BookNotFoundException(bookId)

        logger.debug("$endpointId, found bookMetadata for format: bookMetadata: $bookMetadata")
        // TODO: book format validation

        val filename = file.originalFilename ?: file.name

        val formatId = if (!dryRun) bookFormatStorageService.storeFormat(bookId, mimeType, filename, file.bytes)
                       else BookFormatId("testFormatId")

        logger.debug("$endpointId, result: $formatId")

        return ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/bookFormats")
            .path(formatId.toString())
            .toUriString()
    }

    private fun getBookMetadataReader(file: MultipartFile): BookMetadataReader {
        val bookReader = bookFormatManager.readers.firstOrNull { it.canRead(ByteArrayInputStream(file.bytes)) }
            ?: throw BookFormatTypeException("Unsupported content type, cannot read book format")
        return bookReader
    }

    @GetMapping("bookFormats/{bookId}")
    fun getBookFormats(@PathVariable bookId: BookId): List<BookFormatMetadataDto> {
        val formatMetadatas = bookFormatStorageService.listFormatMetadata(bookId)
        logger.debug("getBookFormats (GET /bookFormats/$bookId, result: $formatMetadatas")
        return formatMetadatas.map { bookDtoConverter.mkBookFormatMetadataDto(it) }
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
}