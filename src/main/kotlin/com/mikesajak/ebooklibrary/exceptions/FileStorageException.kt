package com.mikesajak.ebooklibrary.exceptions

import java.lang.RuntimeException

class FileStorageException(message: String, cause: Throwable?) : RuntimeException(message, cause) {
    constructor(message: String) : this(message, null) {}
}