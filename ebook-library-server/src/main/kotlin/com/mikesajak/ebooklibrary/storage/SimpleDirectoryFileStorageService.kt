package com.mikesajak.ebooklibrary.storage

//@Service
//class SimpleDirectoryFileStorageService(properties: FileStorageProperties) : FileStorageService {
//    private val fileStorageLocation: Path
//
//    init {
//        fileStorageLocation = Paths.get(properties.uploadDir)
//            .toAbsolutePath().normalize()
//
//        try {
//            Files.createDirectories(this.fileStorageLocation)
//        } catch (e: Exception) {
//            throw FileStorageException(
//                "Could not create the directory where uploaded files will be stored.",
//                e
//            )
//        }
//    }
//
//    override fun storeFile(file: MultipartFile): String {
//        val fileName = StringUtils.cleanPath(file.originalFilename ?: "")
//        try {
//            if (fileName.contains("..")) {
//                throw FileStorageException("Sorry! Filename contains invalid path sequence $fileName")
//            }
//
//            val targetLocation = fileStorageLocation.resolve(fileName)
//            Files.copy(file.inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING)
//
//            return fileName
//        } catch (e: IOException) {
//            throw FileStorageException("Could not store file $fileName. Please try again.", e)
//        }
//    }
//
//    override fun loadFileAsResource(fileName: String): Resource {
//        try {
//            val filePath = fileStorageLocation.resolve(fileName).normalize()
//            val resource = UrlResource(filePath.toUri())
//            if (resource.exists()) return resource
//            else throw MyFileNotFoundException("File not found $fileName")
//        } catch (e: MalformedURLException) {
//            throw MyFileNotFoundException("File not found $fileName", e)
//        }
//    }
//
//    override fun listFiles(): List<String> {
//        return Files.list(fileStorageLocation)
//            .map { it.toString() }
//            .collect(toList())
//    }
//
//
//
//}