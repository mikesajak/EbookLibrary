package com.mikesajak.ebooklibrary.storage

import org.springframework.core.io.Resource
import org.springframework.web.multipart.MultipartFile

interface FileStorageService {
    fun storeFile(file: MultipartFile): String
    fun loadFileAsResource(fileName: String): Resource
    fun listFiles(): List<String>
}