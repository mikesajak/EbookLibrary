package com.mikesajak.ebooklibrary.client

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class EbookLibClientApplication {
    @Bean
    fun demoData(booksClient: BooksClient) = CommandLineRunner {
        val books = booksClient.listBooks()
        println("Books:")
        books.forEach { println(it) }
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(EbookLibClientApplication::class.java, *args)
}