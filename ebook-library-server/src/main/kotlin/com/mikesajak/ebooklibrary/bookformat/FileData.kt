package com.mikesajak.ebooklibrary.bookformat

import com.mikesajak.ebooklibrary.model.BookId

data class FileData(val id: FileId, val metadata: FileMetadata, val contents: ByteArray) {
    constructor(id: FileId, bookId: BookId, name: String, dataType: DataType, contentType: String, contents: ByteArray)
            : this(id, FileMetadata(name, bookId, dataType, contentType, contents.size), contents)
}

data class FileId(val value: String)

data class FileMetadata(val name: String, val bookId: BookId, val dataType: DataType, val contentType: String, val size: Int)

enum class DataType {
    BookCover,
    BookFormat
}