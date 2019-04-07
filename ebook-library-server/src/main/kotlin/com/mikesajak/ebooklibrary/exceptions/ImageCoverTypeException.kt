package com.mikesajak.ebooklibrary.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST)
class ImageCoverTypeException(message: String) : RuntimeException(message)
