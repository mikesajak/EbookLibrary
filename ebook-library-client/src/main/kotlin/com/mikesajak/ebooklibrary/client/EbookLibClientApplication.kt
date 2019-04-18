package com.mikesajak.ebooklibrary.client

import com.mikesajak.ebooklibrary.payload.BookId
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class EbookLibClientApplication {
    @Bean
    fun demoData(booksClient: BooksClient) = CommandLineRunner {
//        val books = booksClient.listBooks()
//        println("Books:")
//        books.forEach { println(it) }
        val book = booksClient.getBook(BookId("5d2c6bac-f32d-446d-bc23-995ce1df0143"))
        println("Book:\n$book")
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(EbookLibClientApplication::class.java, *args)
}