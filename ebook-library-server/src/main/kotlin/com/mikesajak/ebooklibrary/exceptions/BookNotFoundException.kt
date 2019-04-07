package com.mikesajak.ebooklibrary.exceptions

import com.mikesajak.ebooklibrary.payload.BookId
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class BookNotFoundException(bookId: BookId)
    : RuntimeException("Requested book not found for bookId=$bookId")