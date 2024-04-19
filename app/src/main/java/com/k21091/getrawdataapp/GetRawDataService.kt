package com.k21091.getrawdataapp

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.NotificationCompat

private const val CHANNEL_ID = "GetRAwDataServiceChannel"
private const val NOTIFICATION_ID = 12345 // 通知のID

var getCount= mutableStateOf("10")

class GetRAwDataService : Service() {
    private lateinit var createCsv: CreateCsv

    private var isServiceRunning = false // サービスが実行中かどうかを示すフラグ

    @SuppressLint("ForegroundServiceType")
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, createNotification(), ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION)
        }
        isServiceRunning = true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startScanning()
        return START_STICKY
    }

    private fun startScanning() {
        createCsv = CreateCsv(applicationContext, getCount.value.toInt())
        createCsv.createcsvdata { success ->
            if (success && isServiceRunning) {
                startScanning() // 再度スキャンを開始
            } else {
                // スキャンが完了したらサービスを停止
                stopForeground(true)
                stopSelf()
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "GetRAwDataService Channel"
            val descriptionText = "サービスの実行状態を表示するチャネル"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("GetRAwDataService")
            .setContentText("サービスが実行中です")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        // サービスが破棄されるときの処理
        cancelScan()
        isServiceRunning = false
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
