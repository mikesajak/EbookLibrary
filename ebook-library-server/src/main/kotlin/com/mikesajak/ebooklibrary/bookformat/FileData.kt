package com.mikesajak.ebooklibrary.bookformat

import com.mikesajak.ebooklibrary.payload.BookId

data class FileData(val id: FileId, val metadata: FileMetadata, val contents: ByteArray) {
    constructor(id: FileId, bookId: BookId, name: String, dataType: DataType, contentType: String, contents: ByteArray)
            : this(id, FileMetadata(name, bookId, dataType, contentType), contents)
}

data class FileId(val value: String)

data class FileMetadata(val name: String, val bookId: BookId, val dataType: DataType, val contentType: String)

enum class DataType {
    BookCover,
    BookFormat
}