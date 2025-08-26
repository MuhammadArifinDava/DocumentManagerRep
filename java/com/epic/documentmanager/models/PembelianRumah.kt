package com.epic.documentmanager.models

data class PembelianRumah(
    val id: String = "",
    val uniqueCode: String = "",
    val nama: String = "",
    val alamatKTP: String = "",
    val nik: String = "",
    val npwp: String = "",
    val noTelepon: String = "",
    val statusPernikahan: String = "",
    val namaPasangan: String = "",
    val pekerjaan: String = "",
    val gaji: String = "",
    val kontakDarurat: String = "",
    val tempatKerja: String = "",
    val namaPerumahan: String = "",
    val tipeRumah: String = "",
    val jenisPembayaran: String = "",
    val tipeRumahKategori: String = "", // subsidi/cluster/secondary
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val createdBy: String = "",
    val updatedBy: String = "",
    val status: String = "active",
    val attachments: Map<String, String> = mapOf()
) {
    constructor() : this("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", 0L, 0L, "", "", "active", mapOf())
}