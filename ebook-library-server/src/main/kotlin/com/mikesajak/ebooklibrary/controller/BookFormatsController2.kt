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
class BookFormatsController2 {
    private val logger = LoggerFactory.getLogger(BookFormatsController2::class.java)

    @Autowired
    private lateinit var bookFormatsService: BookFormatsService

    @GetMapping("/bookFormats2/{formatId}")
    fun getBookFormatMetadata(@PathVariable("formatId") formatId: BookFormatId): BookFormatMetadataDto {
        logger.info("getBookFormatContents2 (GET /bookFormats2/$formatId")
        val bookFormat = bookFormatsService.getBookFormatMetadata(formatId)
        return bookFormat
    }

    @GetMapping("/bookFormats2/{formatId}/contents")
    fun getBookFormatContents(@PathVariable("formatId") formatId: BookFormatId): ResponseEntity<ByteArray> {
        logger.info("getBookFormatContents2 (GET /bookFormats2/$formatId")
        val bookFormat = bookFormatsService.getBookFormat(formatId)
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(bookFormat.metadata.formatType))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${bookFormat.metadata.filename}\"")
            .cacheControl(CacheControl.noCache())
            .body(bookFormat.contents)
    }

    @DeleteMapping("/bookFormats2/{bookFormatId}")
    fun deleteBookFormat(@PathVariable("bookFormatId") bookFormatId: BookFormatId) {
        logger.info("deleteBookFormat (DELETE /bookFormats2/$bookFormatId")
        bookFormatsService.deleteBookFormat(bookFormatId)
    }

}