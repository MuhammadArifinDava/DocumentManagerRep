package com.epic.documentmanager.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.epic.documentmanager.R
import com.epic.documentmanager.repositories.StorageRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class UploadService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val storageRepository = StorageRepository()

    companion object {
        private const val CHANNEL_ID = "upload_channel"
        private const val NOTIFICATION_ID = 1

        fun startUpload(context: Context, uris: ArrayList<Uri>, documentType: String, fileNames: ArrayList<String>) {
            val intent = Intent(context, UploadService::class.java).apply {
                putParcelableArrayListExtra("uris", uris)
                putExtra("documentType", documentType)
                putStringArrayListExtra("fileNames", fileNames)
            }
            context.startForegroundService(intent)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val uris = intent?.getParcelableArrayListExtra<Uri>("uris") ?: arrayListOf()
        val documentType = intent?.getStringExtra("documentType") ?: ""
        val fileNames = intent?.getStringArrayListExtra("fileNames") ?: arrayListOf()

        startForeground(NOTIFICATION_ID, createNotification("Mengupload dokumen...", 0))

        serviceScope.launch {
            uploadFiles(uris, documentType, fileNames, startId)
        }

        return START_NOT_STICKY
    }

    private suspend fun uploadFiles(uris: List<Uri>, documentType: String, fileNames: List<String>, startId: Int) {
        try {
            val totalFiles = uris.size
            val uploadedUrls = mutableListOf<String>()

            uris.forEachIndexed { index, uri ->
                val fileName = fileNames.getOrNull(index) ?: "file_${index + 1}"
                val progress = ((index + 1) * 100) / totalFiles

                updateNotification("Mengupload ${fileName}...", progress)

                val result = storageRepository.uploadDocument(uri, documentType, fileName)
                if (result.isSuccess) {
                    uploadedUrls.add(result.getOrThrow())
                } else {
                    updateNotification("Upload gagal", 0)
                    stopSelf(startId)
                    return
                }
            }

            updateNotification("Upload selesai", 100)

            // Broadcast success result
            val resultIntent = Intent("UPLOAD_COMPLETE").apply {
                putStringArrayListExtra("uploadedUrls", ArrayList(uploadedUrls))
            }
            sendBroadcast(resultIntent)

        } catch (e: Exception) {
            updateNotification("Upload gagal: ${e.message}", 0)
        } finally {
            stopSelf(startId)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Upload Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Channel for file upload notifications"
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(text: String, progress: Int): android.app.Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Document Manager")
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_upload)
            .setProgress(100, progress, progress == 0)
            .setOngoing(true)
            .build()
    }

    private fun updateNotification(text: String, progress: Int) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, createNotification(text, progress))
    }
}