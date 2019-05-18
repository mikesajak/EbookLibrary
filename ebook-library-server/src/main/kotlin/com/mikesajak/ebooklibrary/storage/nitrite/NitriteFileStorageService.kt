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
        val filters = listOfNotNull(bookId?.let { eq("metadata.bookId.value", bookId.value) },
                                    dataType?.let { eq("metadata.dataType", dataType.toString()) })
        val filter = if (filters.isEmpty()) ObjectFilters.ALL
                     else filters.reduce { a, b -> and(a, b) }

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