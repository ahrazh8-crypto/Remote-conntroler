package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.AddDeviceDialog
import com.example.ui.screens.ControllerScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.SetupGuideScreen
import com.example.ui.theme.CarbonSurfaceLight
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.PowerRed
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.TextLight
import com.example.ui.viewmodel.DeviceViewModel
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppScreen()
            }
        }
    }
}

@Composable
fun MainAppScreen() {
    val viewModel: DeviceViewModel = viewModel()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Main Router Screen flow
            when (viewModel.activeScreen) {
                "dashboard" -> DashboardScreen(viewModel = viewModel)
                "controller" -> ControllerScreen(viewModel = viewModel)
                "guide" -> SetupGuideScreen(onBack = { viewModel.navigateTo("dashboard") })
            }

            // Dialog Modals
            AddDeviceDialog(viewModel = viewModel)

            // Tactile feedback transient status toast notifications
            TransientToastOverlay(
                statusMessage = viewModel.statusMessage,
                onDismiss = { viewModel.dismissToast() }
            )
        }
    }
}

@Composable
fun BoxScope.TransientToastOverlay(
    statusMessage: Pair<Boolean, String>?,
    onDismiss: () -> Unit
) {
    AnimatedVisibility(
        visible = statusMessage != null,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
        modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter)
            .padding(16.dp)
            .testTag("status_toast")
    ) {
        if (statusMessage != null) {
            val (isSuccess, text) = statusMessage

            // Dismiss automatically after 2.5 seconds
            LaunchedEffect(statusMessage) {
                delay(2500)
                onDismiss()
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CarbonSurfaceLight)
                    .border(
                        1.dp,
                        if (isSuccess) StatusGreen.copy(alpha = 0.5f) else PowerRed.copy(alpha = 0.5f),
                        RoundedCornerShape(12.dp)
                    )
                    .clickable { onDismiss() }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = if (isSuccess) Icons.Default.CheckCircle else Icons.Default.Error,
                        contentDescription = if (isSuccess) "Success" else "Error",
                        tint = if (isSuccess) StatusGreen else PowerRed,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = text,
                        color = TextLight,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss",
                        tint = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}
