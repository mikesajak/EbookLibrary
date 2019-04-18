package com.mikesajak.ebooklibrary.client

import com.mikesajak.ebooklibrary.payload.Book
import com.mikesajak.ebooklibrary.payload.BookId
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Configuration
class BooksClientConfiguration {
    @Bean
    fun booksRestTemplate(builder: RestTemplateBuilder,
                          @Value("\${client.serverUrl:http://localhost:8080}") serverUri: String) =
        builder.rootUri(serverUri).build()
}

@Component
class BooksClient(private val booksRestTemplate: RestTemplate) {

    companion object {
        class BookList: MutableList<Book> by mutableListOf()
    }

    fun listBooks(): List<Book> {
        val result = booksRestTemplate.getForEntity("/books",
                                                    BookList::class.java)
        return result.body?.toList() ?: listOf()
    }

    fun getBook(id: BookId): Book? {
        val result = booksRestTemplate.getForEntity("/books/${id.value}", Book::class.java)
        return result.body
    }
}