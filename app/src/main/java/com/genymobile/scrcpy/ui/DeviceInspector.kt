package com.genymobile.scrcpy.ui

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.MediaCodecList
import android.os.Build
import android.view.WindowManager
import java.net.Inet4Address
import java.net.NetworkInterface

object DeviceInspector {

    fun getLocalIpAddress(): String {
        return try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val networkInterface = interfaces.nextElement()
                if (networkInterface.isLoopback || !networkInterface.isUp) continue
                val addresses = networkInterface.inetAddresses
                while (addresses.hasMoreElements()) {
                    val address = addresses.nextElement()
                    if (address is Inet4Address && !address.isLoopbackAddress) {
                        return address.hostAddress ?: "127.0.0.1"
                    }
                }
            }
            "127.0.0.1"
        } catch (_: Exception) {
            "127.0.0.1"
        }
    }

    fun getDisplayMetrics(context: Context): String {
        return try {
            val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val metrics = context.resources.displayMetrics
            val defaultDisplay = wm.defaultDisplay
            val refreshRate = defaultDisplay?.refreshRate?.toInt() ?: 60
            "${metrics.widthPixels} x ${metrics.heightPixels} (${metrics.densityDpi} DPI, ${refreshRate}Hz)"
        } catch (_: Exception) {
            "Default Display"
        }
    }

    fun getSupportedEncoders(): List<CodecInfo> {
        val result = mutableListOf<CodecInfo>()
        try {
            val codecList = MediaCodecList(MediaCodecList.REGULAR_CODECS)
            for (info in codecList.codecInfos) {
                if (!info.isEncoder) continue
                for (type in info.supportedTypes) {
                    if (type.startsWith("video/") || type.startsWith("audio/")) {
                        val isHw = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            info.isHardwareAccelerated
                        } else {
                            !info.name.startsWith("OMX.google.") && !info.name.startsWith("c2.android.")
                        }
                        result.add(CodecInfo(info.name, type, isHw))
                    }
                }
            }
        } catch (_: Exception) {
            // fallback
        }
        return result.distinctBy { it.mimeType + it.name }
    }

    fun getCameraCount(context: Context): Int {
        return try {
            val cm = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            cm.cameraIdList.size
        } catch (_: Exception) {
            0
        }
    }
}
