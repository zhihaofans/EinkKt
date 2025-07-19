package com.zhihaofans.einkkt.views

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zhihaofans.einkkt.views.components.MyTopBar
import com.zhihaofans.einkkt.views.ui.theme.EinkKtTheme
import io.zhihao.library.android.kotlinEx.isNotNullAndEmpty
import io.zhihao.library.android.kotlinEx.string

class DevicesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            EinkKtTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        MyTopBar("设备信息", true) {
                            (context as? Activity)?.finish()
                        }
                    }
                ) { innerPadding ->
                    Greeting2(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting2(name: String, modifier: Modifier = Modifier) {
    /*Text(
        text = "设备信息页面更新中",
        modifier = modifier
    )*/
    DeviceInfoView(modifier)
}

@Composable
fun DeviceInfoView(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val info = remember { DeviceInfoUtil.getDeviceInfo(context) }
    LazyColumn(
        modifier = modifier
    ) {
        items(info.entries.toList()) { entry ->
            Text("${entry.key}: ${entry.value}", modifier = Modifier.padding(vertical = 4.dp))
        }
    }
}

object DeviceInfoUtil {
    fun getDeviceInfo(context: Context): Map<String, String> {
        val pm = context.packageManager
        val packageInfo = pm.getPackageInfo(context.packageName, 0)
        val metrics = context.resources.displayMetrics
        val cameraManager =
            context.getSystemService(Context.CAMERA_SERVICE) as? android.hardware.camera2.CameraManager

        val info = linkedMapOf<String, String>()

        // 系统信息
        info["品牌"] = Build.BRAND
        info["设备名"] = Build.DEVICE
        info["型号"] = Build.MODEL
        info["制造商"] = Build.MANUFACTURER
        info["Android版本"] = Build.VERSION.RELEASE
        info["API Level"] = Build.VERSION.SDK_INT.toString()

        // 应用信息
        info["包名"] = context.packageName
        info["版本名"] = packageInfo.versionName ?: "Unknown"
        info["版本号"] = packageInfo.longVersionCode.toString()

        // 屏幕信息
        info["屏幕宽度(px)"] = metrics.widthPixels.toString()
        info["屏幕高度(px)"] = metrics.heightPixels.toString()
        info["屏幕密度"] = metrics.density.toString()

        // 摄像头信息
        cameraManager?.cameraIdList?.forEachIndexed { index, cameraId ->
            info["摄像头 $index ID"] = cameraId
        }
        info["虚拟机"] = this.isEmulator().string("可能是", "应该不是")
        info["系统定制名称"] = getRomName()
        return info
    }

    fun isEmulator(): Boolean {
        val buildProperties = listOf(
            Build.FINGERPRINT,
            Build.MODEL,
            Build.MANUFACTURER,
            Build.BRAND,
            Build.DEVICE,
            Build.PRODUCT,
            Build.HARDWARE
        )

        val indicators = listOf(
            "generic",
            "unknown",
            "emulator",
            "sdk",
            "x86",
            "goldfish",
            "ranchu",
            "vbox",
            "test-keys"
        )

        return buildProperties.any { prop ->
            indicators.any { indicator ->
                prop.contains(indicator, ignoreCase = true)
            }
        } || Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
    }

    @SuppressLint("PrivateApi")
    fun getSystemProperty(propName: String): String? {
        return try {
            val clazz = Class.forName("android.os.SystemProperties")
            val method = clazz.getMethod("get", String::class.java)
            val resu = method.invoke(null, propName) as? String
            Log.e("DeviceInfoUtil", "getSystemProperty: $propName = $resu")
            resu
        } catch (e: Exception) {
            null
        }
    }

    fun getRomName(): String {
        return when {
            isMiui() -> "MIUI"
            isEmui() -> "EMUI"
            isFlyme() -> "Flyme"
            isOneUI() -> "One UI"
            else -> "AOSP 或未知"
        }
    }

    fun isMiui(): Boolean {
        return getSystemProperty("ro.miui.ui.version.name").isNotNullAndEmpty() && !isEmulator()
    }

    fun isEmui(): Boolean {
        return getSystemProperty("ro.build.version.emui").isNotNullAndEmpty()
    }

    fun isFlyme(): Boolean {
        val displayId = Build.DISPLAY ?: return false
        return displayId.lowercase().contains("flyme")
    }

    fun isOneUI(): Boolean {
        return getSystemProperty("ro.build.version.oneui").isNotNullAndEmpty()
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    EinkKtTheme {
        Greeting2("Android")
    }
}