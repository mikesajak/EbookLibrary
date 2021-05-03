package com.mikesajak.ebooklibrary.controller

import com.mikesajak.ebooklibrary.controller.dto.BookDto
import com.mikesajak.ebooklibrary.controller.dto.BookFormatMetadataDto
import com.mikesajak.ebooklibrary.model.BookId
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@Suppress("unused")
@RestController
class BooksController {
    private val logger = LoggerFactory.getLogger(BooksController::class.java)

    @Autowired
    private lateinit var booksService: BooksService

    @Autowired
    private lateinit var bookFormatsService: BookFormatsService

    @Autowired
    private lateinit var bookImageService: BookImageService

    @PostMapping("/books")
    fun addBook(@RequestBody bookDto: BookDto): String {
        logger.info("addBook (POST /books) with $bookDto")
        return booksService.addBook(bookDto).value
    }

    @PutMapping("/books/{bookId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateBook(@PathVariable bookId: String, @RequestBody bookDto: BookDto) {
        logger.info("updateBook (PUT /books/$bookId) with $bookDto")
        booksService.updateBook(BookId(bookId), bookDto)
    }

    @GetMapping("/books/{bookId}")
    fun getBook(@PathVariable bookId: String): BookDto {
        logger.info("getBook (GET /books/$bookId)")
        return booksService.getBook(BookId(bookId))
    }

    @DeleteMapping("/books/{bookId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteBook(@PathVariable bookId: String) {
        logger.info("deleteBook (DELETE /books/$bookId)")
        booksService.deleteBook(BookId(bookId))
    }

    @GetMapping("/books")
    fun getBooks(@RequestParam(required = false) query: String?): List<BookDto> {
        logger.info("getBooks (GET /books?query=$query)")
        return booksService.getBooks(query)
    }

    @GetMapping("/bookIds")
    fun getBookIds(@RequestParam(required = false) query: String?) : ResponseEntity<List<BookId>> {
        logger.info("getBookIds (GET /booksIds?query=$query)")
        val bookIds = booksService.getBookIds(query)
        return ResponseEntity.ok(bookIds)
    }

    // book formats

    @GetMapping("/books/{bookId}/formats")
    fun getBookFormats(@PathVariable bookId: BookId): List<BookFormatMetadataDto> {
        logger.info("getBookFormats (GET / books/{$bookId}/formats")
        return bookFormatsService.getBookFormats(bookId)
    }

    @PostMapping("/books/{bookId}/formats")
    fun uploadBookFormat(@PathVariable("bookId") bookId: BookId,
//                         @RequestParam("formatMetadata") bookFormatMetadataDto: BookFormatMetadataDto?,
                         @RequestParam("formatType") formatType: String?,
                         @RequestParam("file") file: MultipartFile): String {
        logger.info("uploadBookFormat (POST /books/$bookId/formats)")
        return bookFormatsService.uploadBookFormat(bookId, formatType, file)
    }

    // cover images

    @GetMapping("/books/{bookId}/cover")
    fun getBookCover(@PathVariable("bookId") bookId: BookId): ResponseEntity<ByteArray> {
        logger.info("getBookCover (GET /books/$bookId/cover)")
        val cover = bookImageService.getCoverImage(bookId)

        return if (cover != null)
            ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(cover.coverImage.contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"${cover.coverImage.name}\"")
//                .cacheControl(CacheControl.noCache())
                .body(cover.coverImage.imageData)
        else ResponseEntity.notFound().build()
    }

    @PostMapping("/books/{bookId}/cover")
    fun uploadCoverImage(@PathVariable("bookId") bookId: BookId,
                         @RequestParam("file") file: MultipartFile) {
        logger.info("uploadCoverImage (GET /books/$bookId/cover")
        bookImageService.setCoverImage(bookId, file)
    }

    @DeleteMapping("/books/{bookId}/cover")
    fun deleteCoverImage(@PathVariable bookId: BookId) {
        logger.info("deleteCoverImage (DELETE /books/$bookId/cover")
        bookImageService.deleteCoverImage(bookId)
    }
}
