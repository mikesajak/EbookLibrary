package com.mikesajak.ebooklibrary.exceptions

import com.mikesajak.ebooklibrary.model.BookFormatId
import com.mikesajak.ebooklibrary.model.BookId
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class BookFormatNotFoundException(bookFormatId: BookFormatId? = null)
    : RuntimeException("Requested book format not found for bookFormatId=$bookFormatId")

@ResponseStatus(HttpStatus.NOT_FOUND)
class BookFormatsNotFoundException(bookId: BookId? = null)
    : RuntimeException("Requested book format not found for bookId=$bookId")