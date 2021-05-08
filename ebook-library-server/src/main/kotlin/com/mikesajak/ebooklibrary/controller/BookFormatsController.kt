package com.mikesajak.ebooklibrary.controller

import com.mikesajak.ebooklibrary.controller.dto.BookFormatMetadataDto
import com.mikesajak.ebooklibrary.model.BookFormatId
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.CacheControl
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@Suppress("unused")
@RestController
class BookFormatsController {
    private val logger = LoggerFactory.getLogger(BookFormatsController::class.java)

    @Autowired
    private lateinit var bookFormatsService: BookFormatsService

    @GetMapping("/bookFormats/{formatId}")
    fun getBookFormatMetadata(@PathVariable("formatId") formatId: BookFormatId): BookFormatMetadataDto {
        logger.info("getBookFormatMetadata (GET /bookFormats/$formatId")
        val bookFormat = bookFormatsService.getBookFormatMetadata(formatId)
        return bookFormat
    }

    @GetMapping("/bookFormats/{formatId}/contents")
    fun getBookFormatContents(@PathVariable("formatId") formatId: BookFormatId): ResponseEntity<ByteArray> {
        logger.info("getBookFormatContents (GET /bookFormats/$formatId")
        val bookFormat = bookFormatsService.getBookFormat(formatId)
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(bookFormat.metadata.formatType))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${bookFormat.metadata.filename}\"")
            .cacheControl(CacheControl.noCache())
            .body(bookFormat.contents)
    }

    @DeleteMapping("/bookFormats/{bookFormatId}")
    fun deleteBookFormat(@PathVariable("bookFormatId") bookFormatId: BookFormatId) {
        logger.info("deleteBookFormat (DELETE /bookFormats/$bookFormatId")
        bookFormatsService.deleteBookFormat(bookFormatId)
    }

}