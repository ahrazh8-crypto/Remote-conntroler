package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Brightness5
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mouse
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SettingsCell
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.DeviceEntity
import com.example.ui.theme.CarbonSurfaceLight
import com.example.ui.theme.GlowAmber
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.PowerRed
import com.example.ui.theme.SlateGray
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.TextLight
import com.example.ui.theme.TextMuted
import com.example.ui.viewmodel.DeviceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControllerScreen(viewModel: DeviceViewModel) {
    val device by viewModel.selectedDevice.collectAsState()
    val currentDevice = device ?: return

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = currentDevice.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(StatusGreen)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${currentDevice.ipAddress}:${currentDevice.port}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.selectDevice(null) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Disconnect")
                    }
                },
                actions = {
                    // Quick WoL trigger
                    if (currentDevice.macAddress.isNotBlank()) {
                        IconButton(onClick = { viewModel.wakeDevice(currentDevice) }) {
                            Icon(
                                imageVector = Icons.Default.ElectricBolt,
                                contentDescription = "Wake device via LAN",
                                tint = GlowAmber
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            // Adaptive View based on Device Type
            if (currentDevice.type == "android_tv") {
                AndroidTVController(viewModel, currentDevice)
            } else {
                ComputerController(viewModel, currentDevice)
            }
        }
    }
}

@Composable
fun ComputerController(viewModel: DeviceViewModel, device: DeviceEntity) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Trackpad", "Media & Sys", "Keyboard")

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = SlateGray,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = NeonCyan
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedTab == index) NeonCyan else SlateGray,
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            when (selectedTab) {
                0 -> TrackpadMode(viewModel)
                1 -> MediaAndSystemMode(viewModel)
                2 -> KeyboardAndHotkeysMode(viewModel)
            }
        }
    }
}

@Composable
fun TrackpadMode(viewModel: DeviceViewModel) {
    var scrollDelta by remember { mutableStateOf(0f) }

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Trackpad area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CarbonSurfaceLight)
                .border(1.dp, NeonCyan.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                .testTag("trackpad_gestures_area")
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { viewModel.sendCommand("left_click") },
                        onDoubleTap = { viewModel.sendCommand("double_click") },
                        onLongPress = { viewModel.sendCommand("right_click") }
                    )
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDrag = { change, dragAmount ->
                            change.consume()
                            // Send mouse moves
                            viewModel.sendCommand(
                                "mouse_move",
                                mapOf("dx" to dragAmount.x.toString(), "dy" to dragAmount.y.toString())
                            )
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            // Textured background lines or instruction
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Mouse,
                    contentDescription = null,
                    tint = NeonCyan.copy(alpha = 0.4f),
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "GUESTURE TRACKPAD",
                    fontSize = 12.sp,
                    color = NeonCyan.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "Slide to Move • Tap to Left Click\nDouble Tap to Open • Long Press to Right Click",
                    fontSize = 10.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        // Mouse click triggers & Scrollbar
        Row(
            modifier = Modifier.fillMaxWidth().height(60.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { viewModel.sendCommand("left_click") },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("LEFT CLICK", fontWeight = FontWeight.Bold, color = TextLight, fontSize = 12.sp)
            }

            // Tactile vertical scroll wheel slider helper
            Box(
                modifier = Modifier
                    .width(60.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CarbonSurfaceLight)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDrag = { change, dragAmount ->
                                change.consume()
                                scrollDelta += dragAmount.y
                                if (scrollDelta > 30f) {
                                    viewModel.sendCommand("keypress", mapOf("key" to "down"))
                                    scrollDelta = 0f
                                } else if (scrollDelta < -30f) {
                                    viewModel.sendCommand("keypress", mapOf("key" to "up"))
                                    scrollDelta = 0f
                                }
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.ArrowDropUp, "Scroll Up", tint = NeonCyan, modifier = Modifier.size(16.dp))
                    Text("SCROLL", fontSize = 9.sp, color = NeonCyan, fontWeight = FontWeight.Bold)
                    Icon(Icons.Default.ArrowDropDown, "Scroll Down", tint = NeonCyan, modifier = Modifier.size(16.dp))
                }
            }

            Button(
                onClick = { viewModel.sendCommand("right_click") },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("RIGHT CLICK", fontWeight = FontWeight.Bold, color = TextLight, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun MediaAndSystemMode(viewModel: DeviceViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Media Section Card
        RemoteKeySectionCard(title = "MEDIA CONTROLS") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MediaCircleButton(Icons.Default.FastRewind, "Prev") {
                    viewModel.sendCommand("keypress", mapOf("key" to "prevtrack"))
                }
                MediaCircleButton(Icons.Default.PlayArrow, "Play/Pause", isPrimary = true) {
                    viewModel.sendCommand("play_pause")
                }
                MediaCircleButton(Icons.Default.FastForward, "Next") {
                    viewModel.sendCommand("keypress", mapOf("key" to "nexttrack"))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                RowButton(Icons.Default.VolumeDown, "Vol Down", modifier = Modifier.weight(1f)) {
                    viewModel.sendCommand("volume_down")
                }
                RowButton(Icons.Default.VolumeMute, "Mute", modifier = Modifier.weight(1f)) {
                    viewModel.sendCommand("mute")
                }
                RowButton(Icons.Default.VolumeUp, "Vol Up", modifier = Modifier.weight(1f)) {
                    viewModel.sendCommand("volume_up")
                }
            }
        }

        // Power & Lock Controls Section Card
        RemoteKeySectionCard(title = "SYSTEM ACTIONS") {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    RowButton(
                        icon = Icons.Default.Brightness5,
                        label = "Bright Up",
                        modifier = Modifier.weight(1f)
                    ) {
                        viewModel.sendCommand("keypress", mapOf("key" to "brightnessup"))
                    }
                    RowButton(
                        icon = Icons.Default.Brightness6,
                        label = "Bright Down",
                        modifier = Modifier.weight(1f)
                    ) {
                        viewModel.sendCommand("keypress", mapOf("key" to "brightnessdown"))
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    RowButton(
                        icon = Icons.Default.MusicNote,
                        label = "Launch Spotify",
                        modifier = Modifier.weight(1f)
                    ) {
                        viewModel.sendCommand("keypress", mapOf("key" to "mediaselect"))
                    }
                    RowButton(
                        icon = Icons.Default.SettingsCell,
                        label = "Lock Screen",
                        tint = GlowAmber,
                        modifier = Modifier.weight(1f)
                    ) {
                        viewModel.sendCommand("keypress", mapOf("key" to "sleep"))
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    RowButton(
                        icon = Icons.Default.PowerSettingsNew,
                        label = "Power Shutdown",
                        tint = PowerRed,
                        modifier = Modifier.weight(1f)
                    ) {
                        viewModel.sendCommand("keypress", mapOf("key" to "power"))
                    }
                }
            }
        }
    }
}

@Composable
fun KeyboardAndHotkeysMode(viewModel: DeviceViewModel) {
    var textInput by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Text string dispatch box
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "TRANSMIT TEXT STRING",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan,
                    letterSpacing = 1.sp
                )

                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    placeholder = { Text("Type words to send to PC...", fontSize = 13.sp) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (textInput.isNotBlank()) {
                                viewModel.sendCommand("type_text", mapOf("text" to textInput))
                                textInput = ""
                                focusManager.clearFocus()
                            }
                        }
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = CarbonSurfaceLight
                    ),
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                if (textInput.isNotBlank()) {
                                    viewModel.sendCommand("type_text", mapOf("text" to textInput))
                                    textInput = ""
                                    focusManager.clearFocus()
                                }
                            }
                        ) {
                            Icon(Icons.Default.Send, "Send text", tint = NeonCyan)
                        }
                    }
                )
            }
        }

        // Functional Keyboard Keys
        RemoteKeySectionCard(title = "FUNCTIONAL KEYS") {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    KeyboardKey("ESC", modifier = Modifier.weight(1f)) {
                        viewModel.sendCommand("keypress", mapOf("key" to "escape"))
                    }
                    KeyboardKey("BACKSPACE", icon = Icons.Default.Backspace, modifier = Modifier.weight(1.5f)) {
                        viewModel.sendCommand("keypress", mapOf("key" to "backspace"))
                    }
                    KeyboardKey("ENTER", modifier = Modifier.weight(1.5f)) {
                        viewModel.sendCommand("keypress", mapOf("key" to "enter"))
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    KeyboardKey("SPACEBAR", modifier = Modifier.weight(2f)) {
                        viewModel.sendCommand("keypress", mapOf("key" to "space"))
                    }
                    KeyboardKey("TAB", modifier = Modifier.weight(1f)) {
                        viewModel.sendCommand("keypress", mapOf("key" to "tab"))
                    }
                    KeyboardKey("WIN / CMD", modifier = Modifier.weight(1.2f)) {
                        viewModel.sendCommand("keypress", mapOf("key" to "win"))
                    }
                }
            }
        }

        // Common Multi-key Hotkeys
        RemoteKeySectionCard(title = "COMMON HOTKEY SHORTCUTS") {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    KeyboardKey("COPY (Ctrl+C)", modifier = Modifier.weight(1f)) {
                        viewModel.sendCommand("keypress", mapOf("key" to "ctrl+c"))
                    }
                    KeyboardKey("PASTE (Ctrl+V)", modifier = Modifier.weight(1f)) {
                        viewModel.sendCommand("keypress", mapOf("key" to "ctrl+v"))
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    KeyboardKey("CLOSE APP (Alt+F4)", tint = PowerRed.copy(alpha = 0.8f), modifier = Modifier.weight(1f)) {
                        viewModel.sendCommand("keypress", mapOf("key" to "alt+f4"))
                    }
                    KeyboardKey("SWITCH APP (Alt+Tab)", modifier = Modifier.weight(1f)) {
                        viewModel.sendCommand("keypress", mapOf("key" to "alt+tab"))
                    }
                }
            }
        }
    }
}

@Composable
fun AndroidTVController(viewModel: DeviceViewModel, device: DeviceEntity) {
    var textInput by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // TV Header D-Pad and Volume Controller
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "D-PAD NAVIGATOR",
                fontWeight = FontWeight.Bold,
                color = StatusGreen,
                fontSize = 11.sp,
                letterSpacing = 1.2.sp
            )

            // Red glowing power button
            IconButton(
                onClick = { viewModel.sendCommand("keypress", mapOf("key" to "power")) },
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(PowerRed.copy(alpha = 0.15f))
                    .border(1.dp, PowerRed.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(Icons.Default.PowerSettingsNew, "Power Toggle", tint = PowerRed, modifier = Modifier.size(20.dp))
            }
        }

        // Beautiful Immersive Circular D-Pad Navigator
        Box(
            modifier = Modifier
                .size(240.dp)
                .clip(CircleShape)
                .background(CarbonSurfaceLight)
                .border(2.dp, CarbonSurfaceLight, CircleShape)
        ) {
            // Up Arrow Button
            TVNavigationButton(
                icon = Icons.Default.KeyboardArrowUp,
                description = "Navigate Up",
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 10.dp)
            ) {
                viewModel.sendCommand("keypress", mapOf("key" to "up"))
            }

            // Left Arrow Button
            TVNavigationButton(
                icon = Icons.Default.KeyboardArrowLeft,
                description = "Navigate Left",
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 10.dp)
            ) {
                viewModel.sendCommand("keypress", mapOf("key" to "left"))
            }

            // Central Select / OK Button
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.Center)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(StatusGreen, MaterialTheme.colorScheme.primary)
                        )
                    )
                    .clickable { viewModel.sendCommand("keypress", mapOf("key" to "select")) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "OK",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 18.sp
                )
            }

            // Right Arrow Button
            TVNavigationButton(
                icon = Icons.Default.KeyboardArrowRight,
                description = "Navigate Right",
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 10.dp)
            ) {
                viewModel.sendCommand("keypress", mapOf("key" to "right"))
            }

            // Down Arrow Button
            TVNavigationButton(
                icon = Icons.Default.KeyboardArrowDown,
                description = "Navigate Down",
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 10.dp)
            ) {
                viewModel.sendCommand("keypress", mapOf("key" to "down"))
            }
        }

        // TV Secondary Control keys (Back, Home, Menu)
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TVRoundButton(Icons.Default.ArrowBack, "Back") {
                viewModel.sendCommand("keypress", mapOf("key" to "back"))
            }
            TVRoundButton(Icons.Default.Home, "Home") {
                viewModel.sendCommand("keypress", mapOf("key" to "home"))
            }
            TVRoundButton(Icons.Default.Menu, "Menu") {
                viewModel.sendCommand("keypress", mapOf("key" to "menu"))
            }
        }

        // TV Volume sliders/Buttons Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.sendCommand("volume_down") },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(CarbonSurfaceLight)
                ) {
                    Icon(Icons.Default.VolumeDown, "Volume Down", tint = StatusGreen)
                }

                IconButton(
                    onClick = { viewModel.sendCommand("mute") },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(CarbonSurfaceLight)
                ) {
                    Icon(Icons.Default.VolumeMute, "Mute", tint = GlowAmber)
                }

                IconButton(
                    onClick = { viewModel.sendCommand("volume_up") },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(CarbonSurfaceLight)
                ) {
                    Icon(Icons.Default.VolumeUp, "Volume Up", tint = StatusGreen)
                }
            }
        }

        // Quick App Launcher Grid for TV
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "QUICK APP LAUNCHERS",
                    fontWeight = FontWeight.Bold,
                    color = StatusGreen,
                    fontSize = 10.sp,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TVAppButton("YouTube", Color(0xFFFF0000), modifier = Modifier.weight(1f)) {
                            viewModel.sendCommand("launch_app", mapOf("app" to "youtube"))
                        }
                        TVAppButton("Netflix", Color(0xFFE50914), modifier = Modifier.weight(1f)) {
                            viewModel.sendCommand("launch_app", mapOf("app" to "netflix"))
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TVAppButton("Prime Video", Color(0xFF00A8E1), modifier = Modifier.weight(1f)) {
                            viewModel.sendCommand("launch_app", mapOf("app" to "prime_video"))
                        }
                        TVAppButton("Spotify", Color(0xFF1DB954), modifier = Modifier.weight(1f)) {
                            viewModel.sendCommand("launch_app", mapOf("app" to "spotify"))
                        }
                    }
                }
            }
        }

        // TV Search Keyboard input
        OutlinedTextField(
            value = textInput,
            onValueChange = { textInput = it },
            placeholder = { Text("Search or type text on TV...", fontSize = 13.sp) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    if (textInput.isNotBlank()) {
                        viewModel.sendCommand("type_text", mapOf("text" to textInput))
                        textInput = ""
                        focusManager.clearFocus()
                    }
                }
            ),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = StatusGreen,
                unfocusedBorderColor = CarbonSurfaceLight
            ),
            trailingIcon = {
                IconButton(
                    onClick = {
                        if (textInput.isNotBlank()) {
                            viewModel.sendCommand("type_text", mapOf("text" to textInput))
                            textInput = ""
                            focusManager.clearFocus()
                        }
                    }
                ) {
                    Icon(Icons.Default.Keyboard, "Type", tint = StatusGreen)
                }
            }
        )

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun TVNavigationButton(
    icon: ImageVector,
    description: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(54.dp)
            .clip(CircleShape)
            .background(CarbonSurfaceLight)
    ) {
        Icon(icon, description, tint = TextLight, modifier = Modifier.size(32.dp))
    }
}

@Composable
fun TVRoundButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(CarbonSurfaceLight)
                .border(1.dp, CarbonSurfaceLight, CircleShape)
        ) {
            Icon(icon, label, tint = TextLight, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun TVAppButton(
    name: String,
    brandColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(CarbonSurfaceLight)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(brandColor)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(name, fontWeight = FontWeight.Bold, color = TextLight, fontSize = 12.sp)
        }
    }
}

@Composable
fun RemoteKeySectionCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = NeonCyan,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            content()
        }
    }
}

@Composable
fun MediaCircleButton(
    icon: ImageVector,
    description: String,
    isPrimary: Boolean = false,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(if (isPrimary) 64.dp else 52.dp)
            .clip(CircleShape)
            .background(if (isPrimary) NeonCyan else CarbonSurfaceLight)
            .border(1.dp, if (isPrimary) NeonCyan else CarbonSurfaceLight, CircleShape)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = description,
            tint = if (isPrimary) MaterialTheme.colorScheme.onPrimary else TextLight,
            modifier = Modifier.size(if (isPrimary) 32.dp else 22.dp)
        )
    }
}

@Composable
fun RowButton(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    tint: Color = NeonCyan,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(46.dp),
        colors = ButtonDefaults.buttonColors(containerColor = CarbonSurfaceLight),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = label, tint = tint, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(label, color = TextLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun KeyboardKey(
    label: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    tint: Color = NeonCyan,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(44.dp),
        colors = ButtonDefaults.buttonColors(containerColor = CarbonSurfaceLight),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(label, color = TextLight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}
