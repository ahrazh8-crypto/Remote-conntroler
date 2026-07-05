package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.ActionLogEntity
import com.example.data.database.DeviceEntity
import com.example.ui.theme.CarbonSurfaceLight
import com.example.ui.theme.GlowAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.PowerRed
import com.example.ui.theme.SlateGray
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.TextMuted
import com.example.ui.viewmodel.DeviceViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: DeviceViewModel) {
    val devices by viewModel.devices.collectAsState()
    val logs by viewModel.logs.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(NeonCyan, MaterialTheme.colorScheme.secondary)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Wifi,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                "Control Hub",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                "Universal Remote Controller",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }
                },
                actions = {
                    TextButton(
                        onClick = { viewModel.navigateTo("guide") },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.HelpOutline,
                            contentDescription = "Setup Guide",
                            tint = NeonCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Setup Guide", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openAddDeviceDialog() },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape,
                modifier = Modifier
                    .testTag("add_device_fab")
                    .padding(bottom = 16.dp, end = 8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Device")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp)
        ) {
            // Visual Banner Header
            item {
                DeviceHeaderBanner()
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Saved Devices Title Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "PAIRED DEVICES",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = "${devices.size} total",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // List of Devices
            if (devices.isEmpty()) {
                item {
                    EmptyDevicesPlaceholder()
                }
            } else {
                items(devices) { device ->
                    DeviceCard(
                        device = device,
                        onClick = { viewModel.selectDevice(device) },
                        onEdit = { viewModel.openEditDeviceDialog(device) },
                        onDelete = { viewModel.deleteDevice(device.id) },
                        onWake = { viewModel.wakeDevice(device) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Action Logs History Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "COMMAND HISTORY",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted,
                            letterSpacing = 1.2.sp
                        )
                    }

                    if (logs.isNotEmpty()) {
                        TextButton(onClick = { viewModel.clearLogHistory() }) {
                            Text("Clear", color = PowerRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (logs.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "No recent commands sent. Start controlling to build a history.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
            } else {
                items(logs.take(12)) { log ->
                    LogItemRow(log)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp)) // Padding for FAB and bottom system bars
            }
        }
    }
}

@Composable
fun DeviceHeaderBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        CarbonSurfaceLight,
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
            .border(1.dp, CarbonSurfaceLight, RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Secure Local Wi-Fi Control",
                style = MaterialTheme.typography.bodySmall,
                color = NeonCyan,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Tap any configured device to start controlling. Open 'Setup Guide' to find how to launch companion server endpoints on your computer or TV.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun EmptyDevicesPlaceholder() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Computer,
                contentDescription = null,
                tint = SlateGray,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "No devices paired yet",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Tap the '+' button in the corner to register your Mac, Windows PC, or Android TV.",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
fun DeviceCard(
    device: DeviceEntity,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onWake: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("device_card_${device.id}")
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Device Type Icon Circle
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(CarbonSurfaceLight),
                contentAlignment = Alignment.Center
            ) {
                val icon = when (device.type) {
                    "mac" -> Icons.Default.Computer
                    "windows" -> Icons.Default.Computer
                    else -> Icons.Default.Tv
                }
                val tint = when (device.type) {
                    "mac" -> Color.White
                    "windows" -> MaterialTheme.colorScheme.secondary
                    else -> StatusGreen
                }
                Icon(
                    imageVector = icon,
                    contentDescription = device.type,
                    tint = tint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Middle Name & Connection Credentials
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = device.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${device.ipAddress}:${device.port}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    fontSize = 11.sp
                )
                if (device.macAddress.isNotBlank()) {
                    Text(
                        text = "MAC: ${device.macAddress.uppercase()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = SlateGray,
                        fontSize = 9.sp
                    )
                }
            }

            // Right Quick Actions Button Row
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Wake On Lan (WoL) Lightning Bolt if MAC address present
                if (device.macAddress.isNotBlank()) {
                    IconButton(
                        onClick = onWake,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(CarbonSurfaceLight)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ElectricBolt,
                            contentDescription = "Wake on LAN",
                            tint = GlowAmber,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Device",
                        tint = SlateGray,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Device",
                        tint = PowerRed.copy(alpha = 0.8f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun LogItemRow(log: ActionLogEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CarbonSurfaceLight),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                // Device Icon dot indicator
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(
                            when (log.deviceType) {
                                "mac" -> Color.White
                                "windows" -> MaterialTheme.colorScheme.secondary
                                else -> StatusGreen
                            }
                        )
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = log.action,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "to ${log.deviceName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }
            }

            // Timestamp and Status
            Column(horizontalAlignment = Alignment.End) {
                val formatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                val timeStr = formatter.format(Date(log.timestamp))

                Text(
                    text = timeStr,
                    style = MaterialTheme.typography.bodySmall,
                    color = SlateGray,
                    fontSize = 10.sp
                )

                val isSuccess = log.status.startsWith("Success") || log.status == "Sent"
                Text(
                    text = log.status,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isSuccess) StatusGreen else PowerRed,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
