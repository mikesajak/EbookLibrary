package com.mikesajak.ebooklibrary.controller.dto

data class ServerInfo(val name: String, val version: String) {
    override fun toString() = "$name:$version"
}