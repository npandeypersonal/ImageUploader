package com.assesment.imageuploader.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
import android.os.Build.VERSION.SDK_INT
import android.os.IBinder
import androidx.activity.viewModels
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import com.assesment.imageuploader.R
import com.assesment.imageuploader.dataModel.model.ImageData
import com.assesment.imageuploader.viewModel.ImageViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UploadService @Inject constructor() : LifecycleService() {
    private var imagePaths: List<ImageData> = emptyList()

    override fun onCreate() {
        super.onCreate()

        // Observe the upload progress and update the notification
        ImageListActivity.uploadViewModel.uploadProgress.observe(this) { progress ->
            updateNotification(progress.first)
            if (imagePaths.size == progress.first){
                stopForeground(STOP_FOREGROUND_DETACH)
                stopSelf()
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        // Start the service in the foreground
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification(0).build(),FOREGROUND_SERVICE_TYPE_DATA_SYNC)

        // Get the image paths from the intent
        imagePaths = intent?.getSerializableExtra(EXTRA_IMAGE_PATHS) as List<ImageData>

        // Start the image upload process in the ViewModel
        ImageListActivity.uploadViewModel.uploadImages(imagePaths)

        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun createNotification(progress: Int): NotificationCompat.Builder {
        val notificationIntent = Intent(this, ImageListActivity::class.java)
        val pendingFlags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            pendingFlags
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Uploading Images")
            .setContentText("$progress/${imagePaths.size} Uploaded")
            .setSmallIcon(R.drawable.ic_upload)
            .setContentIntent(pendingIntent)
            .setTicker("Upload in progress")
            .setOngoing(true)
    }

    private fun updateNotification(progress: Int) {
        val notification = createNotification(progress)
            .setProgress(imagePaths.size, progress, false)
            .build()

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        private const val CHANNEL_ID = "UploadServiceChannel"
        private const val CHANNEL_NAME = "Upload Service Channel"
        private const val NOTIFICATION_ID = 1
        const val EXTRA_IMAGE_PATHS = "extra_image_paths"
    }
}
