package com.mikesajak.ebooklibrary.exceptions

import com.mikesajak.ebooklibrary.payload.BookDataId
import com.mikesajak.ebooklibrary.payload.BookId
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.lang.RuntimeException

@ResponseStatus(HttpStatus.NOT_FOUND)
class BookFormatNotFoundException(bookId: BookId, bookDataId: BookDataId)
    : RuntimeException("Requested book format not found for bookId=$bookId, bookDataId=$bookDataId")