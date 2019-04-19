package com.mikesajak.ebooklibrary.controller

import com.mikesajak.ebooklibrary.exceptions.BookNotFoundException
import com.mikesajak.ebooklibrary.exceptions.ImageCoverTypeException
import com.mikesajak.ebooklibrary.payload.BookCover
import com.mikesajak.ebooklibrary.payload.BookId
import com.mikesajak.ebooklibrary.storage.BookCoverStorageService
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
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

@RestController
class BookCoverController {
    private val logger = LoggerFactory.getLogger(BookCoverController::class.java)

    @Autowired
    lateinit var bookMetadataStorageService: BookMetadataStorageService

    @Autowired
    lateinit var bookCoverStorageService: BookCoverStorageService

    @PostMapping("/coverImages/{bookId}")
    fun uploadCoverImage(@PathVariable("bookId") bookId: BookId,
                         @RequestParam("file") file: MultipartFile): String {

        if (file.contentType == null) {
            throw ImageCoverTypeException("No content type for cover imageData provided. ${file.name}")
        }

        if (bookMetadataStorageService.getBook(bookId) == null) {
            throw BookNotFoundException(bookId)
        }

        // image format validation
        val image = ImageIO.read(file.inputStream)
        val imageBytesOutStream = ByteArrayOutputStream()
        ImageIO.write(image, file.contentType!!, imageBytesOutStream)
        val imageData = imageBytesOutStream.toByteArray()

        val filename = file.originalFilename ?: file.name
        bookCoverStorageService.storeCover(BookCover(bookId, file.contentType!!, filename, imageData))

        val fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/coverImages")
            .path(bookId.toString())
            .toUriString()

        return fileDownloadUri
    }

    @GetMapping("coverImages/{bookId}")
    fun downloadCoverImage(@PathVariable bookId: BookId): ResponseEntity<ByteArray> {
        val cover = bookCoverStorageService.getCover(bookId)

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(cover.coverImage.contentType))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${cover.coverImage.contentType}\"")
            .cacheControl(CacheControl.noCache())
            .body(cover.coverImage.imageData)
    }

    @DeleteMapping("coverImages/{bookId}")
    fun deleteCoverImage(@PathVariable bookId: BookId) {
        if (!bookCoverStorageService.deleteCover(bookId))
            throw BookNotFoundException(bookId)
    }

    @GetMapping("coverImages")
    fun listCoverImages(): List<BookId> {
        return bookCoverStorageService.listCovers()

    }
}