package com.epic.documentmanager.utils

import android.util.Patterns

object ValidationUtils {
    fun isValidEmail(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    fun isValidPhone(phone: String): Boolean {
        return phone.isNotEmpty() && phone.matches(Regex("^[0-9+]{10,15}$"))
    }

    fun isValidNIK(nik: String): Boolean {
        return nik.length == 16 && nik.matches(Regex("^[0-9]{16}$"))
    }

    fun isValidNPWP(npwp: String): Boolean {
        return npwp.matches(Regex("^[0-9]{2}\\.[0-9]{3}\\.[0-9]{3}\\.[0-9]{1}-[0-9]{3}\\.[0-9]{3}$"))
    }

    fun validateForm(fields: Map<String, String>): List<String> {
        val errors = mutableListOf<String>()

        fields.forEach { (key, value) ->
            when {
                value.isEmpty() -> errors.add("$key tidak boleh kosong")
                key == "email" && !isValidEmail(value) -> errors.add("Format email tidak valid")
                key == "password" && !isValidPassword(value) -> errors.add("Password minimal 6 karakter")
                key == "noTelepon" && !isValidPhone(value) -> errors.add("Format nomor telepon tidak valid")
                key == "nik" && !isValidNIK(value) -> errors.add("Format NIK tidak valid (16 digit)")
                key == "npwp" && !isValidNPWP(value) -> errors.add("Format NPWP tidak valid")
            }
        }

        return errors
    }
}