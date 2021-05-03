package com.mikesajak.ebooklibrary.controller

import com.mikesajak.ebooklibrary.exceptions.BookNotFoundException
import com.mikesajak.ebooklibrary.exceptions.ImageCoverTypeException
import com.mikesajak.ebooklibrary.model.BookCover
import com.mikesajak.ebooklibrary.model.BookId
import com.mikesajak.ebooklibrary.storage.BookCoverStorageService
import com.mikesajak.ebooklibrary.storage.BookMetadataStorageService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

@Service
class BookImageService {
    private val logger = LoggerFactory.getLogger(BookImageService::class.java)

    @Autowired
    lateinit var bookMetadataStorageService: BookMetadataStorageService

    @Autowired
    @Qualifier("bookCoverAsFileStorageService")
    lateinit var bookCoverStorageService: BookCoverStorageService

    fun setCoverImage(bookId: BookId, file: MultipartFile) {
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

    fun getCoverImage(bookId: BookId): BookCover? {
        return bookCoverStorageService.getCover(bookId)
    }

    fun deleteCoverImage(bookId: BookId) {
        if (!bookCoverStorageService.deleteCover(bookId))
            throw BookNotFoundException(bookId)
    }
}