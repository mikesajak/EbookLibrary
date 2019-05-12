package com.mikesajak.ebooklibrary.payload

data class ServerInfo(val name: String, val version: String) {
    override fun toString() = "$name:$version"
}