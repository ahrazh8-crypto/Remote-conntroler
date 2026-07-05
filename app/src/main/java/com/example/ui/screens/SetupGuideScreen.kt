package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CarbonSurfaceLight
import com.example.ui.theme.NeonCyan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupGuideScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Connection Setup Guide", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Configure Your Companion Servers",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "To control your Mac, Windows PC, or Android TV, this app sends HTTP GET requests to the IP Address and Port of your target devices.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            GuideSection(
                title = "1. Computer Control Setup (Mac / Windows)",
                icon = Icons.Default.Computer,
                description = "To receive remote control packets (clicks, typing, media), run a lightweight HTTP server on your Mac or Windows PC."
            ) {
                Text(
                    text = "A simple, lightweight Python server script can receive commands and simulate keyboard or mouse events using 'pyautogui':",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                CodeBlock(
                    code = """
# Install requirements: pip install pyautogui flask flask-cors
from flask import Flask, request
from flask_cors import CORS
import pyautogui

app = Flask(__name__)
CORS(app) # Allow local requests

@app.route('/api/control', methods=['GET'])
def control():
    action = request.args.get('action')
    # Mouse clicks
    if action == 'left_click':
        pyautogui.click()
    elif action == 'right_click':
        pyautogui.rightClick()
    # Mouse navigation
    elif action == 'mouse_move':
        dx = float(request.args.get('dx', 0)) * 2.5
        dy = float(request.args.get('dy', 0)) * 2.5
        pyautogui.moveRel(dx, dy)
    # Media controls
    elif action == 'volume_up':
        pyautogui.press('volumeup')
    elif action == 'volume_down':
        pyautogui.press('volumedown')
    elif action == 'mute':
        pyautogui.press('volumemute')
    elif action == 'play_pause':
        pyautogui.press('playpause')
    # Keypresses
    elif action == 'keypress':
        key = request.args.get('key')
        pyautogui.press(key)
    # Typing
    elif action == 'type_text':
        text = request.args.get('text', '')
        pyautogui.write(text)
        
    return {"status": "ok"}

if __name__ == '__main__':
    # Bind to all interfaces, port 8080
    app.run(host='0.0.0.0', port=8080)
                    """.trimIndent()
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            GuideSection(
                title = "2. Android TV Remote Control Setup",
                icon = Icons.Default.Tv,
                description = "For Android TV, you can use built-in ADB (Android Debug Bridge) or run a web server client companion app on the TV."
            ) {
                Text(
                    text = "Option A: ADB over Wi-Fi\n1. Go to Settings > Device Preferences > Developer Options on your TV.\n2. Enable Network Debugging.\n3. Take note of the TV's IP Address (e.g., 192.168.1.50) and Port 5555.\n4. Input these credentials in this app.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Option B: Web Server Companion App\nYou can install a generic HTTP remote control listener app on your TV (listening to port 8080) to receive remote navigation keys.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            GuideSection(
                title = "3. Wake on LAN (WoL)",
                icon = Icons.Default.Info,
                description = "Turn your computer on directly from this app!"
            ) {
                Text(
                    text = "1. Enable 'Wake on LAN' (WoL) in your PC's BIOS / UEFI settings.\n2. In Mac settings, check 'Wake for network access'.\n3. Copy the MAC address of your Ethernet/Wi-Fi adapter.\n4. Enter the MAC address (e.g., 3D:F2:A9:01:BC:4E) when adding your device.\n5. Click the lightning icon on the dashboard to wake it up whenever it's asleep.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun GuideSection(
    title: String,
    icon: ImageVector,
    description: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = NeonCyan,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Divider(
                color = CarbonSurfaceLight,
                modifier = Modifier.padding(vertical = 12.dp)
            )
            content()
        }
    }
}

@Composable
fun CodeBlock(code: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = CarbonSurfaceLight
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Code,
                contentDescription = "Code",
                tint = NeonCyan.copy(alpha = 0.8f),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = code,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
