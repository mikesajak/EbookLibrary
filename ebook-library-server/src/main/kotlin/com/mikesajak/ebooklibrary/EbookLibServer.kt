package com.mikesajak.ebooklibrary

import com.mikesajak.ebooklibrary.payload.BookCover
import com.mikesajak.ebooklibrary.payload.BookMetadata
import com.mikesajak.ebooklibrary.payload.CoverImage
import com.mikesajak.ebooklibrary.storage.BookCoverAsFileStorageService
import com.mikesajak.ebooklibrary.storage.BookMetadataStorageService
import com.mikesajak.ebooklibrary.storage.FileStorageProperties
import com.mikesajak.ebooklibrary.storage.nitrite.NitriteStorageProperties
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import java.nio.file.Files
import java.nio.file.Paths
import javax.activation.MimetypesFileTypeMap

@SpringBootApplication
@EnableConfigurationProperties(FileStorageProperties::class,
                               NitriteStorageProperties::class)
class EbookLibServer {
    @Bean
    fun demoData(bookMetadataStorageService: BookMetadataStorageService,
                 bookCoverStorageService: BookCoverAsFileStorageService) =
        CommandLineRunner {
            if (bookMetadataStorageService.listBooks().isEmpty()) {
                println("Initialize DB with demo data")

                val demoBooks = listOf(
                    BookMetadata(
                        "Książka 1", "Grzegorz Brzęczyszczykiewicz", listOf("tag1", "tag2"),
                        listOf(), null, null, null, listOf("polski"), null
                    ),
                    BookMetadata(
                        "Książka 2", "Grzegorz Brzęczyszczykiewicz", listOf(),
                        listOf(), null, null, null, listOf("polski"), null
                    ),
                    BookMetadata(
                        "Książka 3", "Dan Brown", listOf("tag1", "tag2", "tag3"),
                        listOf(), null, null, null, listOf("angielski, polski"), null
                    )
                )

                demoBooks.map { bookMetadataStorageService.addBook(it) }
                    .withIndex()
                    .forEach { (idx, bookId) ->
                        val filename = "test/${idx+1}.jpg"
                        val imagePath = Paths.get(filename)
                        val imageData = Files.readAllBytes(imagePath)
                        val contentType = MimetypesFileTypeMap.getDefaultFileTypeMap().getContentType(filename)
                        val cover = BookCover(bookId, CoverImage(filename, contentType, imageData))
                        bookCoverStorageService.storeCover(cover)
                }

                println("Added test books:")
                bookMetadataStorageService.listBooks().forEach { println(it) }

                println("Added test image covers:")
                bookCoverStorageService.listCovers().forEach { bookId ->
                    println("Book: $bookId")
                    val cover = bookCoverStorageService.getCover(bookId)
                    val coverInfo =
                        if (cover != null) "${cover.coverImage.name} ${cover.coverImage.contentType}"
                        else "No cover image found"
                    println("    $coverInfo")
                }
            }
        }

}

fun main(args: Array<String>) {
    SpringApplication.run(EbookLibServer::class.java, *args)
}