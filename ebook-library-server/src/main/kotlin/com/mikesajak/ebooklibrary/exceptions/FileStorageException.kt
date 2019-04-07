package com.mikesajak.ebooklibrary.exceptions

class FileStorageException(message: String, cause: Throwable?) : RuntimeException(message, cause) {
    constructor(message: String) : this(message, null) {}
}