package com.mikesajak.ebooklibrary.controller

import com.mikesajak.ebooklibrary.payload.ServerInfo
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class StatusController {
    @GetMapping("status")
    fun downloadFile(): ServerInfo =
        ServerInfo("EbookLibServer", "0.1")

}