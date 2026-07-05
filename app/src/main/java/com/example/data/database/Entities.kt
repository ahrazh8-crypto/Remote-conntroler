package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "devices")
data class DeviceEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val type: String, // "mac", "windows", "android_tv"
    val ipAddress: String,
    val port: Int,
    val macAddress: String = "",
    val lastConnected: Long = System.currentTimeMillis()
)

@Entity(tableName = "action_logs")
data class ActionLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val deviceId: Int,
    val deviceName: String,
    val deviceType: String,
    val action: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String // "Success", "Failed", "Pending"
)
