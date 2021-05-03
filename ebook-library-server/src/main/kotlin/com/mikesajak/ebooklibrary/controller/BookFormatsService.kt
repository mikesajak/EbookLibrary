package com.mikesajak.ebooklibrary.controller

import com.mikesajak.ebooklibrary.bookformat.BookFormatReaderRegistry
import com.mikesajak.ebooklibrary.bookformat.BookMetadataReader
import com.mikesajak.ebooklibrary.controller.dto.BookDtoConverter
import com.mikesajak.ebooklibrary.controller.dto.BookFormatMetadataDto
import com.mikesajak.ebooklibrary.exceptions.BookFormatNotFoundException
import com.mikesajak.ebooklibrary.exceptions.BookFormatTypeException
import com.mikesajak.ebooklibrary.exceptions.BookFormatsNotFoundException
import com.mikesajak.ebooklibrary.exceptions.BookNotFoundException
import com.mikesajak.ebooklibrary.model.BookFormat
import com.mikesajak.ebooklibrary.model.BookFormatId
import com.mikesajak.ebooklibrary.model.BookId
import com.mikesajak.ebooklibrary.storage.BookFormatStorageService
import com.mikesajak.ebooklibrary.storage.BookMetadataStorageService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.io.ByteArrayInputStream

@Service
class BookFormatsService {
    private val logger = LoggerFactory.getLogger(BookFormatsService::class.java)

    @Autowired
    private lateinit var bookMetadataStorageService: BookMetadataStorageService

    @Autowired
    @Qualifier("bookFormatAsFileStorageService")
    private lateinit var bookFormatStorageService: BookFormatStorageService

    @Autowired
    private lateinit var bookFormatManager: BookFormatReaderRegistry

    @Autowired
    private lateinit var bookDtoConverter: BookDtoConverter

    fun getBookFormats(@PathVariable bookId: BookId): List<BookFormatMetadataDto> {
        val bookFormats = bookFormatStorageService.listFormatMetadata(bookId)
        logger.debug("Found book formats for bookId=$bookId: $bookFormats")
        return bookFormats
            .map { bookDtoConverter.mkBookFormatMetadataDto(it) }
    }

    private var dryRun = false

    fun uploadBookFormat(bookId: BookId, formatType: String?, file: MultipartFile): String {
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

    fun getBookFormatMetadata(formatId: BookFormatId): BookFormatMetadataDto =
        bookDtoConverter.mkBookFormatMetadataDto(getBookFormat(formatId).metadata)

    fun getBookFormat(formatId: BookFormatId): BookFormat {
        val bookFormat = bookFormatStorageService.getFormat(formatId)

        return if (bookFormat != null) {
            logger.debug("Returning book format contents for formatId=$formatId: ${bookFormat.contents}")
            bookFormat
        } else {
            logger.debug("Book format doesn't exist for formatId=$formatId")
            throw BookFormatNotFoundException(formatId)
        }
    }

    fun deleteBookFormat(bookFormatId: BookFormatId) {
        if (!bookFormatStorageService.removeFormat(bookFormatId)) {
            logger.debug("Book format doesn't exist, formatId=$bookFormatId")
            throw BookFormatNotFoundException(bookFormatId)
        }
    }

    fun deleteAllBookFormats(bookId: BookId) {
        val numDeleted = bookFormatStorageService.listFormatIds(bookId)
            .map { bookFormatStorageService.removeFormat(it) }
            .count { bookDeleted -> bookDeleted }

        logger.debug("deleteAllBookFormats for bookId=$bookId, num deleted=$numDeleted")

        if (numDeleted == 0)
            throw BookFormatsNotFoundException(bookId)
    }
}