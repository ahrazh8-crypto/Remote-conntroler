package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CarbonSurfaceLight
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.SlateGray
import com.example.ui.viewmodel.DeviceViewModel

@Composable
fun AddDeviceDialog(viewModel: DeviceViewModel) {
    if (!viewModel.showAddDialog) return

    val formState = viewModel.formState
    val isEditing = viewModel.editingDevice != null

    AlertDialog(
        onDismissRequest = { viewModel.closeAddDeviceDialog() },
        shape = RoundedCornerShape(16.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Text(
                text = if (isEditing) "Edit Remote Device" else "Add Remote Device",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Device Type Selector Segmented Row
                Text(
                    text = "Device Type",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(CarbonSurfaceLight),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val types = listOf(
                        Triple("mac", "Mac", Icons.Default.Computer),
                        Triple("windows", "Windows", Icons.Default.Computer),
                        Triple("android_tv", "Android TV", Icons.Default.Tv)
                    )

                    types.forEach { (typeKey, label, icon) ->
                        val isSelected = formState.type == typeKey
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.updateFormType(typeKey) }
                                .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else SlateGray,
                                    modifier = Modifier.padding(end = 4.dp)
                                )
                                Text(
                                    text = label,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Name Input
                OutlinedTextField(
                    value = formState.name,
                    onValueChange = { viewModel.updateFormName(it) },
                    label = { Text("Device Name") },
                    placeholder = { Text("e.g. Living Room TV") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("device_name_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = CarbonSurfaceLight
                    )
                )

                // IP Address Input
                OutlinedTextField(
                    value = formState.ipAddress,
                    onValueChange = { viewModel.updateFormIp(it) },
                    label = { Text("IP Address") },
                    placeholder = { Text("e.g. 192.168.1.50") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("device_ip_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = CarbonSurfaceLight
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Port Input
                    OutlinedTextField(
                        value = formState.port,
                        onValueChange = { viewModel.updateFormPort(it) },
                        label = { Text("Port") },
                        placeholder = { Text("8080") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("device_port_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = CarbonSurfaceLight
                        )
                    )

                    // MAC Address Input (for WoL)
                    OutlinedTextField(
                        value = formState.macAddress,
                        onValueChange = { viewModel.updateFormMac(it) },
                        label = { Text("MAC Address (Optional)") },
                        placeholder = { Text("e.g. 3D:F2:A9:01:BC:4E") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(2f)
                            .testTag("device_mac_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = CarbonSurfaceLight
                        )
                    )
                }

                Text(
                    text = "Note: MAC address is used to send Wake-on-LAN (WoL) broadcasts to wake up your computer/TV.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { viewModel.saveDevice() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.testTag("save_device_button")
            ) {
                Text(if (isEditing) "Save Changes" else "Add Device")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = { viewModel.closeAddDeviceDialog() },
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text("Cancel")
            }
        }
    )
}
