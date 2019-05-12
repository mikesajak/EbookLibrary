package com.mikesajak.ebooklibrary.storage.nitrite

import org.dizitart.kno2.nitrite
import org.dizitart.no2.Nitrite
import org.springframework.stereotype.Service
import java.nio.file.Paths

@Service
class NitriteDbService(val properties: NitriteStorageProperties) {
    final val db: Nitrite

    init {
        @Suppress("LeakingThis")
        val dbFile = Paths.get(properties.dbFile).toAbsolutePath().toFile()
        dbFile.parentFile.mkdirs()

        db = nitrite {
            file = dbFile
            autoCommitBufferSize = 2048
            compress = true
            autoCompact = true
        }
    }

}