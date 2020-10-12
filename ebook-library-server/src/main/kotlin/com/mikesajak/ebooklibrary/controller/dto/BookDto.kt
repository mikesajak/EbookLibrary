package com.mikesajak.ebooklibrary.controller.dto

import java.time.LocalDate

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
                   val formatIds: List<String>?)

data class SeriesDto(val title: String, val number: Int)