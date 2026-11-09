package com.genymobile.scrcpy

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cast
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.genymobile.scrcpy.ui.CodecInfo
import com.genymobile.scrcpy.ui.DeviceInspector
import com.genymobile.scrcpy.ui.DiagnosticLog
import com.genymobile.scrcpy.ui.ScrcpyConfig
import com.genymobile.scrcpy.ui.theme.ScrcpyTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ScrcpyTheme {
                ScrcpyApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScrcpyApp() {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    var config by remember { mutableStateOf(ScrcpyConfig()) }
    var localIp by remember { mutableStateOf("127.0.0.1") }
    var displayMetrics by remember { mutableStateOf("Loading...") }
    var encoders by remember { mutableStateOf<List<CodecInfo>>(emptyList()) }
    var cameraCount by remember { mutableIntStateOf(0) }

    val logs = remember {
        mutableStateListOf(
            DiagnosticLog(currentTime(), "INFO", "Server", "Scrcpy v5.0.1 initialized"),
            DiagnosticLog(currentTime(), "INFO", "Device", "Model: ${Build.MANUFACTURER} ${Build.MODEL} (Android ${Build.VERSION.RELEASE}, API ${Build.VERSION.SDK_INT})"),
            DiagnosticLog(currentTime(), "INFO", "Network", "Listening interface configured")
        )
    }

    LaunchedEffect(Unit) {
        localIp = DeviceInspector.getLocalIpAddress()
        displayMetrics = DeviceInspector.getDisplayMetrics(context)
        encoders = DeviceInspector.getSupportedEncoders()
        cameraCount = DeviceInspector.getCameraCount(context)
        logs.add(DiagnosticLog(currentTime(), "SUCCESS", "Telemetry", "Detected IP: $localIp, Encoders: ${encoders.size}"))
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(36.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Cast,
                                    contentDescription = "Scrcpy Logo Icon",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "Scrcpy",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Server v5.0.1 • Companion",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .testTag("status_indicator")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF34DC83))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Ready",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Tv, contentDescription = "Overview Tab") },
                    label = { Text("Overview") },
                    modifier = Modifier.testTag("tab_overview")
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Videocam, contentDescription = "Video Tab") },
                    label = { Text("Video") },
                    modifier = Modifier.testTag("tab_video")
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.GraphicEq, contentDescription = "Audio Tab") },
                    label = { Text("Audio") },
                    modifier = Modifier.testTag("tab_audio")
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Default.Wifi, contentDescription = "Wireless Tab") },
                    label = { Text("Wireless") },
                    modifier = Modifier.testTag("tab_wireless")
                )
                NavigationBarItem(
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 },
                    icon = { Icon(Icons.Default.DeveloperMode, contentDescription = "Diagnostics Tab") },
                    label = { Text("Diagnostics") },
                    modifier = Modifier.testTag("tab_diagnostics")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> OverviewScreen(
                    config = config,
                    localIp = localIp,
                    onConfigChange = { config = it },
                    onCopyCommand = { cmd ->
                        copyToClipboard(context, cmd)
                        logs.add(DiagnosticLog(currentTime(), "INFO", "Client", "Command copied: $cmd"))
                    }
                )
                1 -> VideoConfigScreen(
                    config = config,
                    onConfigChange = { config = it }
                )
                2 -> AudioConfigScreen(
                    config = config,
                    onConfigChange = { config = it }
                )
                3 -> WirelessScreen(
                    localIp = localIp,
                    port = config.tcpPort,
                    onRefreshIp = {
                        localIp = DeviceInspector.getLocalIpAddress()
                        logs.add(DiagnosticLog(currentTime(), "INFO", "Network", "IP refreshed: $localIp"))
                    }
                )
                4 -> DiagnosticsScreen(
                    context = context,
                    displayMetrics = displayMetrics,
                    encoders = encoders,
                    cameraCount = cameraCount,
                    logs = logs,
                    onClearLogs = { logs.clear() }
                )
            }
        }
    }
}

@Composable
fun OverviewScreen(
    config: ScrcpyConfig,
    localIp: String,
    onConfigChange: (ScrcpyConfig) -> Unit,
    onCopyCommand: (String) -> Unit
) {
    val cliCommand = remember(config, localIp) {
        config.generateCliCommand(localIp)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("screen_overview"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Device Connection",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${Build.MANUFACTURER} ${Build.MODEL}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "IP: $localIp",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Scrcpy forwards your screen, audio, and keyboard/mouse control over USB or TCP/IP. Use the toggles below or copy the customized CLI command.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Generated Scrcpy Command",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = cliCommand,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp
                            ),
                            modifier = Modifier
                                .padding(12.dp)
                                .testTag("cli_command_text")
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = { onCopyCommand(cliCommand) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("copy_command_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy command icon",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Copy Command to Clipboard")
                    }
                }
            }
        }

        item {
            Text(
                text = "Quick Control Toggles",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    ToggleRow(
                        title = "Stay Awake",
                        subtitle = "Prevent device from sleeping while scrcpy is running (-w)",
                        checked = config.stayAwake,
                        onCheckedChange = { onConfigChange(config.copy(stayAwake = it)) },
                        tag = "toggle_stay_awake"
                    )
                    ToggleRow(
                        title = "Turn Screen Off",
                        subtitle = "Turn physical screen off while mirroring on PC (-S)",
                        checked = config.turnScreenOff,
                        onCheckedChange = { onConfigChange(config.copy(turnScreenOff = it)) },
                        tag = "toggle_turn_screen_off"
                    )
                    ToggleRow(
                        title = "Show Physical Touches",
                        subtitle = "Display visual circles for touch inputs (-t)",
                        checked = config.showTouches,
                        onCheckedChange = { onConfigChange(config.copy(showTouches = it)) },
                        tag = "toggle_show_touches"
                    )
                    ToggleRow(
                        title = "Read-Only Mode",
                        subtitle = "Disable keyboard, mouse, and touch injection (--no-control)",
                        checked = config.readOnly,
                        onCheckedChange = { onConfigChange(config.copy(readOnly = it)) },
                        tag = "toggle_read_only"
                    )
                    ToggleRow(
                        title = "Audio Forwarding",
                        subtitle = "Stream Android audio to host system speakers",
                        checked = config.audioEnabled,
                        onCheckedChange = { onConfigChange(config.copy(audioEnabled = it)) },
                        tag = "toggle_audio_enabled"
                    )
                }
            }
        }
    }
}

@Composable
fun VideoConfigScreen(
    config: ScrcpyConfig,
    onConfigChange: (ScrcpyConfig) -> Unit
) {
    val codecs = listOf("h264", "h265", "av1")
    val resolutions = listOf(0 to "Original", 720 to "720p", 1080 to "1080p", 1440 to "1440p", 1920 to "1920p")
    val frameRates = listOf(30, 60, 90, 120)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("screen_video"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Video Codec", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Select encoder protocol for screen streaming", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        codecs.forEach { codec ->
                            FilterChip(
                                selected = config.videoCodec == codec,
                                onClick = { onConfigChange(config.copy(videoCodec = codec)) },
                                label = { Text(codec.uppercase()) },
                                leadingIcon = if (config.videoCodec == codec) {
                                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                } else null,
                                modifier = Modifier.testTag("chip_codec_$codec")
                            )
                        }
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Video Bit Rate", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("${config.videoBitRateMbps} Mbps", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                    Text("Higher bitrate increases visual fidelity; lower reduces Wi-Fi bandwidth", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Slider(
                        value = config.videoBitRateMbps.toFloat(),
                        onValueChange = { onConfigChange(config.copy(videoBitRateMbps = it.toInt())) },
                        valueRange = 2f..32f,
                        steps = 14,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("slider_bitrate")
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Max Resolution Downscaling (-m)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        resolutions.forEach { (res, label) ->
                            FilterChip(
                                selected = config.maxSize == res,
                                onClick = { onConfigChange(config.copy(maxSize = res)) },
                                label = { Text(label) },
                                modifier = Modifier.testTag("chip_res_$res")
                            )
                        }
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Max Framerate (--max-fps)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        frameRates.forEach { fps ->
                            FilterChip(
                                selected = config.maxFps == fps,
                                onClick = { onConfigChange(config.copy(maxFps = fps)) },
                                label = { Text("$fps FPS") },
                                modifier = Modifier.testTag("chip_fps_$fps")
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AudioConfigScreen(
    config: ScrcpyConfig,
    onConfigChange: (ScrcpyConfig) -> Unit
) {
    val audioCodecs = listOf("opus", "aac", "flac", "raw")
    val sources = listOf("playback" to "Internal Playback", "mic" to "Microphone")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("screen_audio"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ToggleRow(
                        title = "Enable Audio Forwarding",
                        subtitle = "Streams audio to your computer in real-time",
                        checked = config.audioEnabled,
                        onCheckedChange = { onConfigChange(config.copy(audioEnabled = it)) },
                        tag = "toggle_audio"
                    )
                }
            }
        }

        if (config.audioEnabled) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Audio Codec (--audio-codec)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            audioCodecs.forEach { codec ->
                                FilterChip(
                                    selected = config.audioCodec == codec,
                                    onClick = { onConfigChange(config.copy(audioCodec = codec)) },
                                    label = { Text(codec.uppercase()) },
                                    modifier = Modifier.testTag("chip_audio_codec_$codec")
                                )
                            }
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Audio Capture Source", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            sources.forEach { (src, label) ->
                                FilterChip(
                                    selected = config.audioSource == src,
                                    onClick = { onConfigChange(config.copy(audioSource = src)) },
                                    label = { Text(label) },
                                    modifier = Modifier.testTag("chip_audio_src_$src")
                                )
                            }
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Audio Buffer Delay", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("${config.audioBufferMs} ms", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                        Text("Adjust buffer latency. Lower means less delay, higher prevents stuttering", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Slider(
                            value = config.audioBufferMs.toFloat(),
                            onValueChange = { onConfigChange(config.copy(audioBufferMs = it.toInt())) },
                            valueRange = 20f..200f,
                            steps = 17,
                            modifier = Modifier.testTag("slider_audio_buffer")
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WirelessScreen(
    localIp: String,
    port: Int,
    onRefreshIp: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("screen_wireless"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Wireless ADB Connection", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("Connect without USB cable over Wi-Fi", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(
                            onClick = onRefreshIp,
                            modifier = Modifier.testTag("refresh_ip_button")
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh IP")
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Target Address:", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = "$localIp:$port",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }

        item {
            Text("Setup Instructions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        item {
            SetupStepCard(
                step = "1",
                title = "Initial Connection",
                description = "Connect your Android device to your computer via USB once with USB Debugging enabled in Developer Options."
            )
        }

        item {
            SetupStepCard(
                step = "2",
                title = "Enable TCP/IP Mode",
                description = "In your computer's terminal, run:\nadb tcpip $port"
            )
        }

        item {
            SetupStepCard(
                step = "3",
                title = "Disconnect USB & Launch Scrcpy",
                description = "Unplug your USB cable, then start scrcpy over Wi-Fi:\nscrcpy --tcpip=$localIp:$port"
            )
        }
    }
}

@Composable
fun SetupStepCard(step: String, title: String, description: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Surface(
                modifier = Modifier.size(32.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(step, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontFamily = if (description.contains("adb") || description.contains("scrcpy")) FontFamily.Monospace else FontFamily.Default
                )
            }
        }
    }
}

@Composable
fun DiagnosticsScreen(
    context: Context,
    displayMetrics: String,
    encoders: List<CodecInfo>,
    cameraCount: Int,
    logs: List<DiagnosticLog>,
    onClearLogs: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("screen_diagnostics"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Hardware Diagnostics", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))
                    DiagnosticInfoRow("Display Metrics", displayMetrics)
                    DiagnosticInfoRow("Android Version", "Android ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})")
                    DiagnosticInfoRow("Device Model", "${Build.MANUFACTURER} ${Build.MODEL} (${Build.DEVICE})")
                    DiagnosticInfoRow("Hardware Encoders", "${encoders.count { it.isHardwareAccelerated }} HW / ${encoders.size} Total")
                    DiagnosticInfoRow("Cameras Detected", "$cameraCount Camera(s)")
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Available Encoders", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    encoders.take(6).forEach { codec ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(codec.mimeType, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (codec.isHardwareAccelerated) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = if (codec.isHardwareAccelerated) "HW Accel" else "Software",
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Server & Client Log", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                OutlinedButton(
                    onClick = onClearLogs,
                    modifier = Modifier.testTag("clear_logs_button")
                ) {
                    Text("Clear")
                }
            }
        }

        items(logs) { log ->
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = log.timestamp,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = when (log.level) {
                            "SUCCESS" -> Color(0xFF1B5E20)
                            "WARN" -> Color(0xFFE65100)
                            else -> MaterialTheme.colorScheme.primaryContainer
                        }
                    ) {
                        Text(
                            text = log.tag,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = log.message,
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

@Composable
fun DiagnosticInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun ToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    tag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.testTag(tag)
        )
    }
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("scrcpy command", text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "Command copied to clipboard!", Toast.LENGTH_SHORT).show()
}

private fun currentTime(): String {
    val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return sdf.format(Date())
}
