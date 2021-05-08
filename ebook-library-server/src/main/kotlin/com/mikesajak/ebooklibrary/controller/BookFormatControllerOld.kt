package com.mikesajak.ebooklibrary.controller

import com.mikesajak.ebooklibrary.controller.dto.BookFormatMetadataDto
import com.mikesajak.ebooklibrary.exceptions.BookFormatTypeException
import com.mikesajak.ebooklibrary.model.BookFormatId
import com.mikesajak.ebooklibrary.model.BookId
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.CacheControl
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.support.RedirectAttributes


@Suppress("unused")
@RestController
@Deprecated("To be removed")
class BookFormatControllerOld {
    private val logger = LoggerFactory.getLogger(BookFormatControllerOld::class.java)

    @Autowired
    private lateinit var bookFormatsService: BookFormatsService

    @PostMapping("/bookFormatsOld")
    fun uploadTestFormat(@RequestParam("file") file: MultipartFile,
                         redirectAttributes: RedirectAttributes): String {
        val contentType = file.contentType ?: throw BookFormatTypeException("No content type for book format provided. ${file.name}")
        logger.info("Received contentType=$contentType")

        redirectAttributes.addFlashAttribute("message", "You successfully uploaded ${file.originalFilename}")
        return "redirect:/"
    }

    private var dryRun = false

    @PostMapping("/bookFormatsOld/{bookId}")
    fun uploadBookFormat(@PathVariable("bookId") bookId: BookId,
//                         @RequestParam("formatMetadata") bookFormatMetadataDto: BookFormatMetadataDto?,
                         @RequestParam("formatType") formatType: String?,
                         @RequestParam("file") file: MultipartFile): String {
        logger.info("uploadBookFormat (POST/bookFormatsOld/$bookId)")
        return bookFormatsService.uploadBookFormat(bookId, formatType, file)
    }

    @GetMapping("/bookFormatsOld/{bookId}")
    fun getBookFormats(@PathVariable bookId: BookId): List<BookFormatMetadataDto> {
        logger.info("getBookFormats (GET /bookFormatsOld/$bookId)")
        return bookFormatsService.getBookFormats(bookId)
    }

    @GetMapping("/bookFormatsOld/{bookId}/{formatId}/contents")
    fun getBookFormatContents(@PathVariable("bookId") bookId: BookId,
                              @PathVariable("formatId") formatId: BookFormatId): ResponseEntity<ByteArray>? {
        logger.info("getBookFormatContents (GET /bookFormatsOld/$bookId/$formatId/contents")

        val bookFormat = bookFormatsService.getBookFormat(formatId)
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(bookFormat.metadata.formatType))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${bookFormat.metadata.filename}\"")
            .cacheControl(CacheControl.noCache())
            .body(bookFormat.contents)
    }

    @DeleteMapping("/bookFormatsOld/{bookId}/{bookFormatId}")
    fun deleteBookFormat(@PathVariable("bookId") bookId: BookId,
                         @PathVariable("bookFormatId") bookFormatId: BookFormatId) {
        logger.info("deleteBookFormat (DELETE /bookFormatsOld/$bookId/$bookFormatId")
        bookFormatsService.deleteBookFormat(bookFormatId)
    }

    @DeleteMapping("/bookFormatsOld/{bookId}")
    fun deleteAllBookFormats(@PathVariable("bookId") bookId: BookId) {
        logger.info("deleteAllBookFormats (DELETE /bookFormatsOld/$bookId)")
        bookFormatsService.deleteAllBookFormats(bookId)
    }
}