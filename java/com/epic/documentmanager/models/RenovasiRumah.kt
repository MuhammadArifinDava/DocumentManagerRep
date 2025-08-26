package com.epic.documentmanager.models

data class RenovasiRumah(
    val id: String = "",
    val uniqueCode: String = "",
    val nama: String = "",
    val alamat: String = "",
    val noTelepon: String = "",
    val deskripsiRenovasi: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val createdBy: String = "",
    val updatedBy: String = "",
    val status: String = "active",
    val attachments: Map<String, String> = mapOf()
) {
    constructor() : this("", "", "", "", "", "", 0L, 0L, "", "", "active", mapOf())
}