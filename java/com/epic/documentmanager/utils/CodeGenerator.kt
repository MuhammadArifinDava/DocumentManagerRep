package com.epic.documentmanager.utils

import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

object CodeGenerator {

    private val dateFormat = SimpleDateFormat("yyyyMM", Locale.getDefault())

    fun generateCodeForPembelianRumah(): String {
        return generateCode(Constants.PREFIX_PEMBELIAN_RUMAH)
    }

    fun generateCodeForRenovasiRumah(): String {
        return generateCode(Constants.PREFIX_RENOVASI_RUMAH)
    }

    fun generateCodeForPemasanganAC(): String {
        return generateCode(Constants.PREFIX_PEMASANGAN_AC)
    }

    fun generateCodeForPemasanganCCTV(): String {
        return generateCode(Constants.PREFIX_PEMASANGAN_CCTV)
    }

    private fun generateCode(prefix: String): String {
        val yearMonth = dateFormat.format(Date())
        val randomNumber = Random.nextInt(1000, 9999)
        return "$prefix-$yearMonth-$randomNumber"
    }

    fun generateRandomCode(length: Int = 8): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..length)
            .map { chars.random() }
            .joinToString("")
    }

    fun generateSequentialCode(prefix: String, sequence: Int): String {
        val yearMonth = dateFormat.format(Date())
        val paddedSequence = sequence.toString().padStart(4, '0')
        return "$prefix-$yearMonth-$paddedSequence"
    }
}