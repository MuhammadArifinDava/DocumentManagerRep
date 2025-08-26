package com.epic.documentmanager.utils

object Constants {
    // Firebase Collections
    const val USERS_COLLECTION = "users"
    const val DOCUMENTS_COLLECTION = "documents"
    const val PEMBELIAN_RUMAH_COLLECTION = "pembelian_rumah"
    const val RENOVASI_RUMAH_COLLECTION = "renovasi_rumah"
    const val PEMASANGAN_AC_COLLECTION = "pemasangan_ac"
    const val PEMASANGAN_CCTV_COLLECTION = "pemasangan_cctv"

    // Storage Paths
    const val STORAGE_DOCUMENTS = "documents"
    const val STORAGE_PROFILE_IMAGES = "profile_images"

    // User Roles
    const val ROLE_ADMIN = "admin"
    const val ROLE_STAFF = "staff"
    const val ROLE_MANAGER = "manager"

    // Document Types
    const val DOC_TYPE_PEMBELIAN_RUMAH = "pembelian_rumah"
    const val DOC_TYPE_RENOVASI_RUMAH = "renovasi_rumah"
    const val DOC_TYPE_PEMASANGAN_AC = "pemasangan_ac"
    const val DOC_TYPE_PEMASANGAN_CCTV = "pemasangan_cctv"

    // Document Code Prefixes
    const val PREFIX_PEMBELIAN_RUMAH = "PR"
    const val PREFIX_RENOVASI_RUMAH = "RR"
    const val PREFIX_PEMASANGAN_AC = "AC"
    const val PREFIX_PEMASANGAN_CCTV = "CC"

    // Request Codes
    const val REQUEST_IMAGE_PICK = 1001
    const val REQUEST_FILE_PICK = 1002
    const val REQUEST_CAMERA = 1003

    // Permissions
    const val PERMISSION_CAMERA = android.Manifest.permission.CAMERA
    const val PERMISSION_READ_STORAGE = android.Manifest.permission.READ_EXTERNAL_STORAGE
    const val PERMISSION_WRITE_STORAGE = android.Manifest.permission.WRITE_EXTERNAL_STORAGE
}