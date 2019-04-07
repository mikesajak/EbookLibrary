package com.mikesajak.ebooklibrary.storage

import org.dizitart.kno2.documentOf
import org.dizitart.no2.NitriteCollection
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
class NitriteFileStorageService(nitriteDbService: NitriteDbService) : FileStorageService {
    private val filesCollection: NitriteCollection

    init {
        filesCollection = nitriteDbService.db.getCollection("images")
    }

    override fun storeFile(file: MultipartFile): String {
        val id = UUID.randomUUID().toString()
        val doc = documentOf("id" to id,
            "contentType" to (file.contentType ?: "application/octet-stream"),
            "name" to file.name,
            "filename" to (file.originalFilename ?: file.name),
            "data" to file.bytes)

        filesCollection.insert(doc)

        return id
    }

    override fun loadFileAsResource(fileName: String): Resource {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun listFiles(): List<String> {
        return filesCollection.find()
            .map { it["filename"].toString() }
            .toList()
    }
}