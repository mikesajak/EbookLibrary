package com.mikesajak.ebooklibrary

import com.mikesajak.ebooklibrary.bookformat.EpubBookMetadataReader
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class EbookLibConfiguration {
    @Bean
    fun epubReader() = EpubBookMetadataReader()
}