package com.mikesajak.ebooklibrary.controller

import com.mikesajak.ebooklibrary.payload.ServerInfo
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class StatusController {
    private val logger = LoggerFactory.getLogger(StatusController::class.java)

    @GetMapping("status")
    fun getStatus(): ServerInfo {
        logger.debug("getStatus (GET /status)")
        return ServerInfo("EbookLibServer", "0.1")
    }

}