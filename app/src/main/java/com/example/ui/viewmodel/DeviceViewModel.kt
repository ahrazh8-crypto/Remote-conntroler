package com.example.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.database.DeviceEntity
import com.example.data.repository.DeviceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DeviceFormState(
    val name: String = "",
    val type: String = "mac", // "mac", "windows", "android_tv"
    val ipAddress: String = "",
    val port: String = "8080",
    val macAddress: String = ""
)

class DeviceViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: DeviceRepository

    val devices: StateFlow<List<DeviceEntity>>
    val logs: StateFlow<List<com.example.data.database.ActionLogEntity>>

    private val _selectedDevice = MutableStateFlow<DeviceEntity?>(null)
    val selectedDevice: StateFlow<DeviceEntity?> = _selectedDevice.asStateFlow()

    var activeScreen by mutableStateOf("dashboard") // "dashboard", "controller", "guide"
        private set

    var formState by mutableStateOf(DeviceFormState())
        private set

    var editingDevice by mutableStateOf<DeviceEntity?>(null)
        private set

    var showAddDialog by mutableStateOf(false)
        private set

    var statusMessage by mutableStateOf<Pair<Boolean, String>?>(null) // Pair(isSuccess, Message)
        private set

    init {
        val database = AppDatabase.getDatabase(application)
        repository = DeviceRepository(database.deviceDao(), database.actionLogDao())

        devices = repository.allDevices.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        logs = repository.recentLogs.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        // Seed default devices if none exist to avoid empty/dead states
        viewModelScope.launch {
            devices.collect { list ->
                if (list.isEmpty()) {
                    seedDefaultDevices()
                }
            }
        }
    }

    private suspend fun seedDefaultDevices() {
        val mac = DeviceEntity(
            name = "Work MacBook Pro",
            type = "mac",
            ipAddress = "192.168.1.15",
            port = 8080,
            macAddress = "3D:F2:A9:01:BC:4E"
        )
        val windows = DeviceEntity(
            name = "Gaming Rig PC",
            type = "windows",
            ipAddress = "192.168.1.22",
            port = 8080,
            macAddress = "E4:5A:F1:67:8B:D0"
        )
        val tv = DeviceEntity(
            name = "Living Room Android TV",
            type = "android_tv",
            ipAddress = "192.168.1.50",
            port = 8080,
            macAddress = "70:3E:AC:44:90:F1"
        )
        repository.insertDevice(mac)
        repository.insertDevice(windows)
        repository.insertDevice(tv)
    }

    fun navigateTo(screen: String) {
        activeScreen = screen
    }

    fun selectDevice(device: DeviceEntity?) {
        _selectedDevice.value = device
        if (device != null) {
            activeScreen = "controller"
        } else {
            activeScreen = "dashboard"
        }
    }

    fun openAddDeviceDialog() {
        editingDevice = null
        formState = DeviceFormState()
        showAddDialog = true
    }

    fun openEditDeviceDialog(device: DeviceEntity) {
        editingDevice = device
        formState = DeviceFormState(
            name = device.name,
            type = device.type,
            ipAddress = device.ipAddress,
            port = device.port.toString(),
            macAddress = device.macAddress
        )
        showAddDialog = true
    }

    fun closeAddDeviceDialog() {
        showAddDialog = false
        editingDevice = null
    }

    fun updateFormName(name: String) {
        formState = formState.copy(name = name)
    }

    fun updateFormType(type: String) {
        formState = formState.copy(type = type)
    }

    fun updateFormIp(ip: String) {
        formState = formState.copy(ipAddress = ip)
    }

    fun updateFormPort(port: String) {
        formState = formState.copy(port = port)
    }

    fun updateFormMac(mac: String) {
        formState = formState.copy(macAddress = mac)
    }

    fun saveDevice() {
        val name = formState.name.ifBlank { "Un-named ${formState.type.uppercase()}" }
        val ip = formState.ipAddress.ifBlank { "192.168.1.100" }
        val portInt = formState.port.toIntOrNull() ?: 8080
        val mac = formState.macAddress

        viewModelScope.launch {
            val currentEditing = editingDevice
            if (currentEditing != null) {
                // Update
                val updated = currentEditing.copy(
                    name = name,
                    type = formState.type,
                    ipAddress = ip,
                    port = portInt,
                    macAddress = mac
                )
                repository.updateDevice(updated)
                showToast(true, "Device updated: $name")
                // If we are currently controlling this device, update selected state too
                if (_selectedDevice.value?.id == currentEditing.id) {
                    _selectedDevice.value = updated
                }
            } else {
                // Create
                val newDevice = DeviceEntity(
                    name = name,
                    type = formState.type,
                    ipAddress = ip,
                    port = portInt,
                    macAddress = mac
                )
                repository.insertDevice(newDevice)
                showToast(true, "Device added: $name")
            }
            closeAddDeviceDialog()
        }
    }

    fun deleteDevice(deviceId: Int) {
        viewModelScope.launch {
            val currentSelected = _selectedDevice.value
            if (currentSelected?.id == deviceId) {
                _selectedDevice.value = null
                activeScreen = "dashboard"
            }
            repository.deleteDeviceById(deviceId)
            showToast(true, "Device deleted successfully")
        }
    }

    fun sendCommand(action: String, extraParams: Map<String, String> = emptyMap()) {
        val device = _selectedDevice.value ?: return
        viewModelScope.launch {
            repository.sendCommand(device, action, extraParams) { success, message ->
                viewModelScope.launch {
                    showToast(success, if (success) "Executed: $action" else "Failed to send: $message")
                }
            }
        }
    }

    fun wakeDevice(device: DeviceEntity) {
        viewModelScope.launch {
            repository.sendWakeOnLan(device) { success, message ->
                viewModelScope.launch {
                    showToast(success, if (success) "Magic Packet Sent!" else "Error: $message")
                }
            }
        }
    }

    fun clearLogHistory() {
        viewModelScope.launch {
            repository.clearLogs()
            showToast(true, "History cleared")
        }
    }

    fun dismissToast() {
        statusMessage = null
    }

    private fun showToast(success: Boolean, message: String) {
        statusMessage = Pair(success, message)
    }
}
