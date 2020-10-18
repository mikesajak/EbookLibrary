package com.mikesajak.ebooklibrary.storage

import com.mikesajak.ebooklibrary.bookformat.DataType
import com.mikesajak.ebooklibrary.bookformat.FileId
import com.mikesajak.ebooklibrary.model.BookFormat
import com.mikesajak.ebooklibrary.model.BookFormatId
import com.mikesajak.ebooklibrary.model.BookFormatMetadata
import com.mikesajak.ebooklibrary.model.BookId
import org.springframework.stereotype.Service

@Service
class BookFormatAsFileStorageService(val fileStorageService: FileStorageService) : BookFormatStorageService {
    override fun storeFormat(bookId: BookId, formatType: String, filename: String, contents: ByteArray): BookFormatId {
        val fileId = fileStorageService.storeFile(filename, bookId, DataType.BookFormat, formatType,contents)
        return BookFormatId(fileId.value)
    }

    override fun getFormat(formatId: BookFormatId): BookFormat? {
        return fileStorageService.getFileData(FileId(formatId.value))?.let {
            BookFormat(formatId,
                BookFormatMetadata(formatId, it.metadata.bookId, it.metadata.contentType, it.metadata.size, it.metadata.name),
                it.contents)
        }
    }

    override fun listFormatIds(bookId: BookId): List<BookFormatId> =
        fileStorageService.listFiles(bookId, DataType.BookFormat)
            .map { BookFormatId(it.value) }

    override fun listFormatMetadata(bookId: BookId): List<BookFormatMetadata> {
        return fileStorageService.listFiles(bookId, DataType.BookFormat)
            .map { fileId -> fileStorageService.getFileMetadata(fileId)?.let {
                Pair(BookFormatId(fileId.value), it)
            } }
            .filterNotNull()
            .map { (formatId, fileMeta) -> BookFormatMetadata(formatId,
                bookId,
                fileMeta.contentType, fileMeta.size, fileMeta.name)
            }
    }

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