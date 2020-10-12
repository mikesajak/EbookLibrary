package com.mikesajak.ebooklibrary.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST)
class BookAlreadyExistsException(bookId: String) : RuntimeException("Book already exisds, bookId=$bookId")
