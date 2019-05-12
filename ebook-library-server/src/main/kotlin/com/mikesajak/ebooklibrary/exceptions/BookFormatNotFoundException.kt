package com.mikesajak.ebooklibrary.exceptions

import com.mikesajak.ebooklibrary.payload.BookFormatId
import com.mikesajak.ebooklibrary.payload.BookId
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class BookFormatNotFoundException(bookId: BookId, bookFormatId: BookFormatId)
    : RuntimeException("Requested book format not found for bookId=$bookId, bookFormatId=$bookFormatId")