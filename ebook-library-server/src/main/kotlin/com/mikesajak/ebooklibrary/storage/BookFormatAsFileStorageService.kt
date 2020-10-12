package com.mikesajak.ebooklibrary.storage

import com.mikesajak.ebooklibrary.bookformat.DataType
import com.mikesajak.ebooklibrary.bookformat.FileId
import com.mikesajak.ebooklibrary.model.*
import org.springframework.stereotype.Service

@Service
class BookFormatAsFileStorageService(val fileStorageService: FileStorageService) : BookFormatStorageService {
    override fun storeFormat(format: BookFormat): BookFormatId {
        val fileId = fileStorageService.storeFile(format.metadata.filename, format.metadata.bookId,
            DataType.BookFormat, format.metadata.formatType, format.contents)
        return BookFormatId(fileId.value)
    }

    override fun getFormat(formatId: BookFormatId): BookFormat? {
//        val fileId = fileStorageService.listFiles(bookId, DataType.BookFormat)
//            .firstOrNull()
//        return fileId?.let {
//            val fileData = fileStorageService.getFileData(it)
//            fileData?.let {
//                BookFormat(bookId, fileData.metadata.name, fileData.metadata.contentType, fileData.contents)
//            }
//        }

        return fileStorageService.getFileData(FileId(formatId.value))?.let {
            BookFormat(BookFormatMetadata(it.metadata.bookId, it.metadata.contentType, it.metadata.name), it.contents)
        }
    }

    override fun listFormatIds(bookId: BookId): List<BookFormatId> =
        fileStorageService.listFiles(bookId, DataType.BookFormat)
            .map { BookFormatId(it.value) }

    override fun listFormatMetadata(bookId: BookId): List<BookFormatMetadataDto> {
        return fileStorageService.listFiles(bookId, DataType.BookFormat)
            .map { fileId -> fileStorageService.getFileMetadata(fileId)?.let {
                Pair(BookFormatId(fileId.value), it)
            } }
            .filterNotNull()
            .map { (formatId, fileMeta) -> BookFormatMetadataDto(formatId, BookFormatMetadata(bookId, fileMeta.contentType, fileMeta.name)) }
    }

    override fun listFormats(): List<BookId> =
        fileStorageService.listFiles()
            .map { fileStorageService.getFileData(it) }
            .filterNotNull()
            .map { it.metadata.bookId }

    override fun removeFormat(formatId: BookFormatId): Boolean =
        fileStorageService.deleteFile(FileId(formatId.value))

    override fun removeFormats(bookId: BookId): Int {
        val formatIds = fileStorageService.listFiles(bookId, DataType.BookFormat)
            .map { BookFormatId(it.value) }

        return formatIds.map { fId -> fileStorageService.deleteFile(FileId(fId.value)) }
            .filter{ deleted -> deleted }
            .count()
    }

    override fun numFormats(): Long = fileStorageService.numFiles()
}