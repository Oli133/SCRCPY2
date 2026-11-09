package com.genymobile.scrcpy.ui

data class ScrcpyConfig(
    val videoCodec: String = "h264", // h264, h265, av1
    val maxSize: Int = 0, // 0 = original, 1080, 1440, 1920, 2560
    val videoBitRateMbps: Int = 8, // 2 to 32 Mbps
    val maxFps: Int = 60, // 30, 60, 90, 120
    val displayId: Int = 0,
    val orientationLock: Int = -1, // -1 = auto, 0, 90, 180, 270
    val audioEnabled: Boolean = true,
    val audioCodec: String = "opus", // opus, aac, flac, raw
    val audioBitRateKbps: Int = 128,
    val audioSource: String = "playback", // playback, mic
    val audioBufferMs: Int = 50,
    val stayAwake: Boolean = true,
    val turnScreenOff: Boolean = false,
    val showTouches: Boolean = false,
    val readOnly: Boolean = false,
    val controlMode: String = "uhid", // uhid, aoa, sdk
    val tcpPort: Int = 5555
) {
    fun generateCliCommand(deviceIp: String): String {
        val sb = StringBuilder("scrcpy")
        if (deviceIp.isNotBlank() && deviceIp != "Unavailable") {
            sb.append(" --tcpip=").append(deviceIp).append(":").append(tcpPort)
        }
        if (videoCodec != "h264") {
            sb.append(" --video-codec=").append(videoCodec)
        }
        if (maxSize > 0) {
            sb.append(" -m ").append(maxSize)
        }
        if (videoBitRateMbps != 8) {
            sb.append(" -b ").append(videoBitRateMbps).append("M")
        }
        if (maxFps != 60) {
            sb.append(" --max-fps=").append(maxFps)
        }
        if (displayId != 0) {
            sb.append(" --display-id=").append(displayId)
        }
        if (orientationLock >= 0) {
            sb.append(" --lock-video-orientation=").append(orientationLock)
        }
        if (!audioEnabled) {
            sb.append(" --no-audio")
        } else {
            if (audioCodec != "opus") {
                sb.append(" --audio-codec=").append(audioCodec)
            }
            if (audioSource == "mic") {
                sb.append(" --audio-source=mic")
            }
            if (audioBufferMs != 50) {
                sb.append(" --audio-buffer=").append(audioBufferMs)
            }
        }
        if (stayAwake) {
            sb.append(" -w")
        }
        if (turnScreenOff) {
            sb.append(" -S")
        }
        if (showTouches) {
            sb.append(" -t")
        }
        if (readOnly) {
            sb.append(" --no-control")
        }
        if (controlMode != "uhid") {
            sb.append(" --keyboard=").append(controlMode)
        }
        return sb.toString()
    }
}

data class DiagnosticLog(
    val timestamp: String,
    val level: String, // INFO, WARN, SUCCESS
    val tag: String,
    val message: String
)

data class CodecInfo(
    val name: String,
    val mimeType: String,
    val isHardwareAccelerated: Boolean
)
