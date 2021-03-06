package com.mikesajak.ebooklibrary.controller.dto

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDate

@JsonInclude(JsonInclude.Include.NON_NULL)
data class BookDto(val id: String?,
                   val title: String,
                   val authors: List<String>,
                   val tags: List<String>,
                   val identifiers: List<String>,
                   val creationDate: LocalDate?,
                   val publicationDate: LocalDate?,
                   val publisher: String?,
                   val languages: List<String>,
                   val series: SeriesDto?,
                   val description: String?,
                   val formats: List<BookFormatMetadataDto>)

data class SeriesDto(val title: String, val number: Int)

data class BookFormatMetadataDto(val id: String?,
                                 val bookId: String,
                                 val formatType: String,
                                 val size: Int)