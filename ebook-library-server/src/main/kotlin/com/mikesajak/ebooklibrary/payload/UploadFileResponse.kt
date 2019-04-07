package com.mikesajak.ebooklibrary.payload

data class UploadFileResponse(val fileName: String,
                              val fileDownloadUri: String,
                              val fileType: String,
                              val size: Long)