package com.example.data.repository

import com.example.data.database.ActionLogDao
import com.example.data.database.ActionLogEntity
import com.example.data.database.DeviceDao
import com.example.data.database.DeviceEntity
import com.example.data.network.RemoteNetworkClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DeviceRepository(
    private val deviceDao: DeviceDao,
    private val actionLogDao: ActionLogDao
) {
    private val repositoryScope = kotlinx.coroutines.CoroutineScope(Dispatchers.IO + kotlinx.coroutines.SupervisorJob())

    val allDevices: Flow<List<DeviceEntity>> = deviceDao.getAllDevices()
    val recentLogs: Flow<List<ActionLogEntity>> = actionLogDao.getRecentLogs()

    suspend fun getDeviceById(id: Int): DeviceEntity? = withContext(Dispatchers.IO) {
        deviceDao.getDeviceById(id)
    }

    suspend fun insertDevice(device: DeviceEntity): Long = withContext(Dispatchers.IO) {
        deviceDao.insertDevice(device)
    }

    suspend fun updateDevice(device: DeviceEntity) = withContext(Dispatchers.IO) {
        deviceDao.updateDevice(device)
    }

    suspend fun deleteDeviceById(id: Int) = withContext(Dispatchers.IO) {
        deviceDao.deleteDeviceById(id)
    }

    suspend fun clearLogs() = withContext(Dispatchers.IO) {
        actionLogDao.clearLogs()
    }

    suspend fun sendCommand(
        device: DeviceEntity,
        action: String,
        extraParams: Map<String, String> = emptyMap(),
        onResult: (Boolean, String) -> Unit = { _, _ -> }
    ) = withContext(Dispatchers.IO) {
        // Record log as "Pending" initially
        val logId = System.currentTimeMillis()
        
        // Update device connection timestamp
        deviceDao.updateDevice(device.copy(lastConnected = System.currentTimeMillis()))

        RemoteNetworkClient.sendCommand(
            ip = device.ipAddress,
            port = device.port,
            type = device.type,
            action = action,
            extraParams = extraParams
        ) { success, message ->
            // Insert log record in database
            val log = ActionLogEntity(
                deviceId = device.id,
                deviceName = device.name,
                deviceType = device.type,
                action = formatActionLabel(action, extraParams),
                status = if (success) "Success" else "Failed ($message)",
                timestamp = System.currentTimeMillis()
            )
            
            // Use background coroutine to save log
            repositoryScope.launch {
                try {
                    actionLogDao.insertLog(log)
                } catch (e: Exception) {
                    // Ignored
                }
            }
            
            onResult(success, message)
        }
    }

    suspend fun sendWakeOnLan(
        device: DeviceEntity,
        onResult: (Boolean, String) -> Unit = { _, _ -> }
    ) = withContext(Dispatchers.IO) {
        if (device.macAddress.isBlank()) {
            onResult(false, "No MAC Address configured")
            return@withContext
        }

        RemoteNetworkClient.sendWakeOnLan(device.macAddress, device.ipAddress) { success, message ->
            val log = ActionLogEntity(
                deviceId = device.id,
                deviceName = device.name,
                deviceType = device.type,
                action = "Wake on LAN (WoL)",
                status = if (success) "Sent" else "Failed ($message)",
                timestamp = System.currentTimeMillis()
            )
            
            repositoryScope.launch {
                try {
                    actionLogDao.insertLog(log)
                } catch (e: Exception) {
                    // Ignored
                }
            }
            onResult(success, message)
        }
    }

    private fun formatActionLabel(action: String, params: Map<String, String>): String {
        val type = params["key"] ?: params["text"] ?: params["x"]?.let { "X:$it Y:${params["y"]}" }
        return if (type != null) {
            "${action.replace("_", " ").capitalize()} ($type)"
        } else {
            action.replace("_", " ").capitalize()
        }
    }
}

// Utility extension
private fun String.capitalize(): String {
    return replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}
