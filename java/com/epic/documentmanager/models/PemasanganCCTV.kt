package com.epic.documentmanager.models

data class PemasanganCCTV(
    val id: String = "",
    val uniqueCode: String = "",
    val nama: String = "",
    val alamat: String = "",
    val noTelepon: String = "",
    val jumlahUnit: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val createdBy: String = "",
    val updatedBy: String = "",
    val status: String = "active",
    val attachments: Map<String, String> = mapOf()
) {
    constructor() : this("", "", "", "", "", "", 0, 0L, 0L, "", "", "active", mapOf())
}