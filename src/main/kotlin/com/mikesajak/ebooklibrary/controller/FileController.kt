package com.mikesajak.ebooklibrary.controller

import com.mikesajak.ebooklibrary.payload.UploadFileResponse
import com.mikesajak.ebooklibrary.storage.FileStorageService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.io.IOException
import javax.servlet.http.HttpServletRequest

@RestController
class FileController {
    private val logger = LoggerFactory.getLogger(FileController::class.java)

    @Autowired
    lateinit var fileStorageService: FileStorageService

    @PostMapping("/uploadFile")
    fun uploadFile(@RequestParam("file") file: MultipartFile): UploadFileResponse {
        val fileName = fileStorageService.storeFile(file)

        val fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/downloadFile")
            .path(fileName)
            .toUriString()

        return UploadFileResponse(fileName, fileDownloadUri, file.contentType ?: "", file.size)
    }

//    @PostMapping("/uploadMultipleFiles")
//    fun uploadMultipleFiles(@RequestParam("files") files: Array<MultipartFile>): List<UploadFileResponse> =
//        files.map { uploadFile(it) }.toList()

    @GetMapping("downloadFile/{fileName:.+}")
    fun downloadFile(@PathVariable fileName: String, request: HttpServletRequest): ResponseEntity<Resource> {
        val resource = fileStorageService.loadFileAsResource(fileName)

        val contentType = try {
            request.servletContext.getMimeType(resource.file.absolutePath)
        } catch (ex: IOException) {
            // fallback to default content type
            "application/octet-stream"
        }

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${resource.filename}\"")
            .body(resource)
    }

    @GetMapping("listFiles")
    fun listFiles(request: HttpServletRequest): ResponseEntity<List<String>> {
        val files = fileStorageService.listFiles()
        return ResponseEntity(files, HttpStatus.OK)
    }
}