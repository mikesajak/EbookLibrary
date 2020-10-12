package com.mikesajak.ebooklibrary.controller

import com.mikesajak.ebooklibrary.controller.dto.LibraryInfo
import com.mikesajak.ebooklibrary.controller.dto.ServerInfo
import com.mikesajak.ebooklibrary.storage.BookCoverStorageService
import com.mikesajak.ebooklibrary.storage.BookFormatStorageService
import com.mikesajak.ebooklibrary.storage.BookMetadataStorageService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class StatusController {
    private val logger = LoggerFactory.getLogger(StatusController::class.java)

    @Autowired
    lateinit var bookMetadataStorageService: BookMetadataStorageService
    @Autowired
    @Qualifier("bookFormatAsFileStorageService")
    lateinit var bookFormatStorageService: BookFormatStorageService
    @Autowired
    @Qualifier("bookCoverAsFileStorageService")
    lateinit var bookCoverStorageService: BookCoverStorageService

    @GetMapping("info")
    fun getServerInfo(): ServerInfo {
        logger.trace("getServerInfo (GET /info)")

        return ServerInfo("EbookLibServer", "0.1")
    }

    @GetMapping("libraryInfo")
    fun getLibraryInfo(): LibraryInfo {
        logger.debug("getLibraryInfo (GET /stats)")

        return LibraryInfo(getServerInfo(), bookMetadataStorageService.numBooks(), bookFormatStorageService.numFormats(),
            bookCoverStorageService.numCovers())
    }
}
