package com.mikesajak.ebooklibrary.storage

import com.mikesajak.ebooklibrary.bookformat.DataType
import com.mikesajak.ebooklibrary.bookformat.FileData
import com.mikesajak.ebooklibrary.bookformat.FileId
import com.mikesajak.ebooklibrary.bookformat.FileMetadata
import com.mikesajak.ebooklibrary.payload.BookId

interface FileStorageService {
//    fun storeFile(file: MultipartFile, bookId: BookId, dataType: DataType): FileId
    fun storeFile(name: String, bookId: BookId, dataType: DataType, contentType: String, fileBytes: ByteArray): FileId
    fun listFiles(): List<FileId>
    fun listFiles(bookId: BookId?, dataType: DataType?): List<FileId>
    fun getFileMetadata(id: FileId): FileMetadata?
    fun getFileData(id: FileId): FileData?
    fun deleteFile(id: FileId): Boolean
}