package com.mikesajak.ebooklibrary.controller

//@RestController
//class FileController {
//    private val logger = LoggerFactory.getLogger(FileController::class.java)
//
//    @Autowired
//    @Qualifier("nitriteFileStorageService")
//    lateinit var fileStorageService: FileStorageService
//
//    @PostMapping("/uploadFile")
//    fun uploadFile(@RequestParam("file") file: MultipartFile): UploadFileResponse {
//        val fileName = fileStorageService.storeFile(file)
//
//        val fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
//            .path("/downloadFile")
//            .path(fileName)
//            .toUriString()
//
//        return UploadFileResponse(fileName, fileDownloadUri, file.contentType ?: "", file.size)
//    }
//
////    @PostMapping("/uploadMultipleFiles")
////    fun uploadMultipleFiles(@RequestParam("files") files: Array<MultipartFile>): List<UploadFileResponse> =
////        files.map { uploadFile(it) }.toList()
//
//    @GetMapping("downloadFile/{fileName:.+}")
//    fun downloadFile(@PathVariable fileName: String, request: HttpServletRequest): ResponseEntity<Resource> {
//        val resource = fileStorageService.loadFileAsResource(fileName)
//
//        val contentType = try {
//            request.servletContext.getMimeType(resource.file.absolutePath)
//        } catch (ex: IOException) {
//            // fallback to default content type
//            "application/octet-stream"
//        }
//
//        return ResponseEntity.ok()
//            .contentType(MediaType.parseMediaType(contentType))
//            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${resource.filename}\"")
//            .body(resource)
//    }
//
//    @GetMapping("listFiles")
//    fun listFiles(request: HttpServletRequest): ResponseEntity<List<String>> {
//        val files = fileStorageService.listFiles()
//        return ResponseEntity(files, HttpStatus.OK)
//    }
//}