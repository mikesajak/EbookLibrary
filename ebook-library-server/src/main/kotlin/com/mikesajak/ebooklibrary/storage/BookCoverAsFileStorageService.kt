package com.mikesajak.ebooklibrary.storage

import com.mikesajak.ebooklibrary.bookformat.DataType
import com.mikesajak.ebooklibrary.payload.BookCover
import com.mikesajak.ebooklibrary.payload.BookId
import org.springframework.stereotype.Service

@Service
class BookCoverAsFileStorageService(val fileStorageService: FileStorageService) : BookCoverStorageService{
    override fun storeCover(cover: BookCover) {
        fileStorageService.storeFile(cover.coverImage.name,
            cover.bookId, DataType.BookCover, cover.coverImage.contentType, cover.coverImage.imageData)
    }

    override fun getCover(bookId: BookId): BookCover? {
        val fileId = fileStorageService.listFiles(bookId, DataType.BookCover)
            .firstOrNull()
        return fileId?.let {
            val fileData = fileStorageService.getFileData(it)
            fileData?.let {
                BookCover(bookId, fileData.metadata.name, fileData.metadata.contentType, fileData.contents)
            }
        }
    }

    override fun listCovers(): List<BookId> =
        fileStorageService.listFiles()
            .map { fileStorageService.getFileData(it) }
            .filterNotNull()
            .map { it.metadata.bookId }


    override fun deleteCover(bookId: BookId): Boolean =
        fileStorageService.listFiles(bookId, DataType.BookCover)
            .map { fileStorageService.deleteFile(it) }
            .any { it == true }
}