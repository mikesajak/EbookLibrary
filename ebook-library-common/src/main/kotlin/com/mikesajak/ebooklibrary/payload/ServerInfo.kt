package com.mikesajak.ebooklibrary.payload

data class ServerInfo(val name: String, val version: String,
                      val numBooks: Long, val numFormats: Long, val numCovers: Long) {
    override fun toString() = "$name:$version ($numBooks books, $numFormats formats, $numCovers covers)"
}