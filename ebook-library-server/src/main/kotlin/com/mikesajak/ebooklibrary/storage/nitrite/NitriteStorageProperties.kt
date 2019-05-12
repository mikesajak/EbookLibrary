package com.mikesajak.ebooklibrary.storage.nitrite

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "nitrite-book-storage")
class NitriteStorageProperties {
    lateinit var dbFile: String
}