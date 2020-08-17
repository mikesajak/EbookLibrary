package com.mikesajak.ebooklibrary.controller

import com.mikesajak.ebooklibrary.exceptions.BookNotFoundException
import com.mikesajak.ebooklibrary.exceptions.ImageCoverTypeException
import com.mikesajak.ebooklibrary.payload.BookCover
import com.mikesajak.ebooklibrary.payload.BookId
import com.mikesajak.ebooklibrary.storage.BookCoverStorageService
import com.mikesajak.ebooklibrary.storage.BookMetadataStorageService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

@RestController
class BookCoverController {
    private val logger = LoggerFactory.getLogger(BookCoverController::class.java)

    @Autowired
    lateinit var bookMetadataStorageService: BookMetadataStorageService

    @Autowired
    @Qualifier("bookCoverAsFileStorageService")
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

        val filename = file.originalFilename ?: file.name
        // image format validation
        val (contentType, imageBytes) = readImage(file.bytes)

        bookCoverStorageService.storeCover(BookCover(bookId, filename, contentType, imageBytes))

        val fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/coverImages")
            .path(bookId.toString())
            .toUriString()

        return fileDownloadUri
    }

    private fun getImageFormat(imageBytes: ByteArray): String {
        val iis = ImageIO.createImageInputStream(imageBytes)
        val readersIt = ImageIO.getImageReaders(iis)
        if (readersIt.hasNext()) {
            val reader = readersIt.next()
            return reader.formatName
        }
        throw IllegalArgumentException("Image format not supported")
    }

    private fun readImage(imageBytes: ByteArray): Pair<String, ByteArray> {
        val image = ImageIO.read(ByteArrayInputStream(imageBytes))
        val imageBytesOutStream = ByteArrayOutputStream()
        val contentType = getImageFormat(imageBytes)
        ImageIO.write(image, contentType, imageBytesOutStream)
        val imageData = imageBytesOutStream.toByteArray()
        return Pair(contentType, imageData)
    }

    @GetMapping("coverImages/{bookId}")
    fun downloadCoverImage(@PathVariable bookId: BookId): ResponseEntity<ByteArray> {
        logger.debug("downloadCoverImage (GET /coverImages/$bookId")
        val cover = bookCoverStorageService.getCover(bookId)

        return if (cover != null)
            ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(cover.coverImage.contentType))
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${cover.coverImage.contentType}\"")
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"${cover.coverImage.name}\"")
//                .cacheControl(CacheControl.noCache())
                .body(cover.coverImage.imageData)
        else ResponseEntity.notFound().build()
    }

    @DeleteMapping("coverImages/{bookId}")
    fun deleteCoverImage(@PathVariable bookId: BookId) {
        logger.debug("deleteCoverImage (DELETE /coverImages/$bookId")
        if (!bookCoverStorageService.deleteCover(bookId))
            throw BookNotFoundException(bookId)
    }

    @GetMapping("coverImages")
    fun listCoverImages(): List<BookId> {
        val coversLst = bookCoverStorageService.listCovers()
        logger.debug("listCoverImages (GET /coverImages), result: $coversLst")
        return coversLst
    }
}