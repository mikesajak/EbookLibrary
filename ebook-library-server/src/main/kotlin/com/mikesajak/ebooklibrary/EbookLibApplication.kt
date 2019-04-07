package com.mikesajak.ebooklibrary

import com.mikesajak.ebooklibrary.payload.BookMetadata
import com.mikesajak.ebooklibrary.storage.BookMetadataStorageService
import com.mikesajak.ebooklibrary.storage.FileStorageProperties
import com.mikesajak.ebooklibrary.storage.NitriteStorageProperties
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean

@SpringBootApplication
@EnableConfigurationProperties(FileStorageProperties::class,
                               NitriteStorageProperties::class)
class EbookLibApplication {
    @Bean
    fun demoData(bookMetadataStorageService: BookMetadataStorageService) = CommandLineRunner {

        if (bookMetadataStorageService.listBooks().isEmpty()) {
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
                    listOf(), null, null, null, listOf("angielski"), null
                )
            )

            demoBooks.forEach { bookMetadataStorageService.addBook(it) }

            println("Added test books:")
            bookMetadataStorageService.listBooks().forEach { println(it) }
        }

    }

}

fun main(args: Array<String>) {
    SpringApplication.run(EbookLibApplication::class.java, *args)
}