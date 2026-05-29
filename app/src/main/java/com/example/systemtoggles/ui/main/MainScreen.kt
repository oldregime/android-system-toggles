package com.example.systemtoggles.ui.main

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavKey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
  onItemClick: (NavKey) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  
  // States
  var peakRefreshRate by remember { mutableStateOf(getPeakRefreshRate(context)) }
  var networkMode by remember { mutableStateOf(getPreferredNetworkMode(context)) }
  
  val isHighRefresh = peakRefreshRate >= 90.0f
  val is5GEnabled = networkMode.contains("26")

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text("System Toggles", fontWeight = FontWeight.Bold)
            Text("Motorola moto g45 5G", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
      )
    }
  ) { innerPadding ->
    Column(
      modifier = modifier
        .fillMaxSize()
        .padding(innerPadding)
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      
      // Card 1: Refresh Rate
      ToggleCard(
        title = "Display Refresh Rate",
        statusText = if (isHighRefresh) "Smooth (120 Hz)" else "Standard (60 Hz)",
        description = "Higher refresh rate feels smoother but uses more battery.",
        iconEmoji = "🔄",
        isActive = isHighRefresh,
        activeColor = Color(0xFF8A2BE2), // Purple accent
        onClick = {
          val success = toggleRefreshRate(context, isHighRefresh)
          if (success) {
            peakRefreshRate = getPeakRefreshRate(context)
            Toast.makeText(context, "Refresh rate updated", Toast.LENGTH_SHORT).show()
          } else {
            Toast.makeText(context, "Error updating refresh rate. Secure Settings permission required.", Toast.LENGTH_LONG).show()
          }
        }
      )

      // Card 2: 5G Network Mode
      ToggleCard(
        title = "5G Mobile Network",
        statusText = if (is5GEnabled) "5G Enabled (Auto)" else "LTE Only (4G)",
        description = "Turn off 5G to significantly improve standby battery life.",
        iconEmoji = "📶",
        isActive = is5GEnabled,
        activeColor = Color(0xFF00C853), // Green accent
        onClick = {
          val success = toggleNetworkMode(context, is5GEnabled)
          if (success) {
            networkMode = getPreferredNetworkMode(context)
            Toast.makeText(context, "Network mode updated", Toast.LENGTH_SHORT).show()
          } else {
            Toast.makeText(context, "Error updating network mode. Secure Settings permission required.", Toast.LENGTH_LONG).show()
          }
        }
      )
    }
  }
}

@Composable
fun ToggleCard(
  title: String,
  statusText: String,
  description: String,
  iconEmoji: String,
  isActive: Boolean,
  activeColor: Color,
  onClick: () -> Unit
) {
  val backgroundBrush = if (isActive) {
    Brush.verticalGradient(colors = listOf(activeColor.copy(alpha = 0.15f), activeColor.copy(alpha = 0.05f)))
  } else {
    Brush.verticalGradient(colors = listOf(Color.Gray.copy(alpha = 0.08f), Color.Gray.copy(alpha = 0.03f)))
  }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() },
    shape = RoundedCornerShape(24.dp),
    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
  ) {
    Box(
      modifier = Modifier
        .background(backgroundBrush)
        .padding(20.dp)
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        Box(
          modifier = Modifier
            .size(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (isActive) activeColor.copy(alpha = 0.2f) else Color.Gray.copy(alpha = 0.1f)),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = iconEmoji,
            fontSize = 26.sp
          )
        }

        Column(modifier = Modifier.weight(1f)) {
          Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
          Text(statusText, fontWeight = FontWeight.SemiBold, color = if (isActive) activeColor else Color.Gray, fontSize = 14.sp)
          Spacer(modifier = Modifier.height(4.dp))
          Text(description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
      }
    }
  }
}

// Logic functions (using Secure table which maps to WRITE_SECURE_SETTINGS)
fun getPeakRefreshRate(context: Context): Float {
  return try {
    Settings.Secure.getFloat(context.contentResolver, "peak_refresh_rate", 120.0f)
  } catch (e: Exception) {
    120.0f
  }
}

fun toggleRefreshRate(context: Context, isCurrentlyHigh: Boolean): Boolean {
  return try {
    val target = if (isCurrentlyHigh) 60.0f else 120.0f
    Settings.Secure.putFloat(context.contentResolver, "peak_refresh_rate", target)
    Settings.Secure.putFloat(context.contentResolver, "min_refresh_rate", target)
    true
  } catch (e: Exception) {
    android.util.Log.e("SystemToggles", "RefreshRate Error", e)
    false
  }
}

fun getPreferredNetworkMode(context: Context): String {
  return try {
    Settings.Global.getString(context.contentResolver, "preferred_network_mode1")
      ?: Settings.Global.getString(context.contentResolver, "preferred_network_mode")
      ?: "26,26"
  } catch (e: Exception) {
    "26,26"
  }
}

fun toggleNetworkMode(context: Context, isCurrently5G: Boolean): Boolean {
  return try {
    val target = if (isCurrently5G) "22,22" else "26,26"
    val cr = context.contentResolver
    Settings.Global.putString(cr, "preferred_network_mode", target)
    Settings.Global.putString(cr, "preferred_network_mode1", target)
    Settings.Global.putString(cr, "preferred_network_mode2", target)
    true
  } catch (e: Exception) {
    android.util.Log.e("SystemToggles", "NetworkMode Error", e)
    false
  }
}
