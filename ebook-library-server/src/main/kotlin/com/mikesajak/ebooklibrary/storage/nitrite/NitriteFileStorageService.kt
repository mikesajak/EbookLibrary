package com.mikesajak.ebooklibrary.storage.nitrite

import com.mikesajak.ebooklibrary.bookformat.DataType
import com.mikesajak.ebooklibrary.bookformat.FileData
import com.mikesajak.ebooklibrary.bookformat.FileId
import com.mikesajak.ebooklibrary.bookformat.FileMetadata
import com.mikesajak.ebooklibrary.payload.BookId
import com.mikesajak.ebooklibrary.storage.FileStorageService
import org.dizitart.no2.IndexOptions
import org.dizitart.no2.IndexType
import org.dizitart.no2.objects.ObjectRepository
import org.dizitart.no2.objects.filters.ObjectFilters
import org.dizitart.no2.objects.filters.ObjectFilters.and
import org.dizitart.no2.objects.filters.ObjectFilters.eq
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
class NitriteFileStorageService(nitriteDbService: NitriteDbService) : FileStorageService {
    private val filesRepo: ObjectRepository<FileData>

    init {
        filesRepo = nitriteDbService.db.getRepository(FileData::class.java)

        if (!filesRepo.hasIndex("id.value")) {
            filesRepo.createIndex("id.value", IndexOptions.indexOptions(IndexType.Unique))
        }
    }

    fun storeFile(file: MultipartFile, bookId: BookId, dataType: DataType): FileId {
        val id = FileId(UUID.randomUUID().toString())
        val fileData = FileData(id, bookId,
            file.originalFilename ?: file.name,
            dataType,
            file.contentType ?: "application/octet-stream",
            file.bytes)

        filesRepo.insert(fileData)

        return id
    }

    override fun storeFile(name: String, bookId: BookId, dataType: DataType, contentType: String, fileBytes: ByteArray): FileId {
        val id = FileId(UUID.randomUUID().toString())
        val fileData = FileData(id, bookId, name, dataType, contentType, fileBytes)

        filesRepo.insert(fileData)

        return id
    }

    override fun listFiles(): List<FileId> {
        return filesRepo.find()
            .map { it.id }
            .toList()
    }

    override fun listFiles(bookId: BookId?, dataType: DataType?): List<FileId> {

        val filters = listOf(bookId?.let { eq("metadata.bookId.value", bookId.value) },
                            dataType?.let { eq("metadata.dataType", dataType.toString()) })
                          .filterNotNull()
        val filter = if (filters.isEmpty()) ObjectFilters.ALL
                     else filters.reduce { a, b -> and(a, b) }


//        val filter = ObjectFilters.ALL
//            .and(if (bookId != null) eq("bookId.value", bookId.value) else ObjectFilters.ALL)
//            .and(if (dataType != null) eq("metadata.dataType.ordinal", dataType.ordinal) else ObjectFilters.ALL)
        return filesRepo.find(filter)
            .map { it.id }
            .toList()
    }

    override fun getFileMetadata(id: FileId): FileMetadata? {
        val cursor = filesRepo.find(eq("id.value", id.value))
        return cursor.singleOrNull()?.metadata
    }

    override fun getFileData(id: FileId): FileData? {
        val cursor = filesRepo.find(eq("id.value", id.value))
        return cursor.singleOrNull()
    }

    override fun deleteFile(id: FileId): Boolean {
        return filesRepo.remove(eq("id.value", id.value))
            .affectedCount > 0
    }
}