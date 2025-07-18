package com.zhihaofans.einkkt.views

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
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

        val info = linkedMapOf<String, String>()

        info["品牌"] = Build.BRAND
        info["设备名"] = Build.DEVICE
        info["型号"] = Build.MODEL
        info["制造商"] = Build.MANUFACTURER
        info["Android版本"] = Build.VERSION.RELEASE
        info["API Level"] = Build.VERSION.SDK_INT.toString()

        info["包名"] = context.packageName
        info["版本名"] = packageInfo.versionName ?: "Unknown"
        info["版本号"] = packageInfo.longVersionCode.toString()

        info["屏幕宽度(px)"] = metrics.widthPixels.toString()
        info["屏幕高度(px)"] = metrics.heightPixels.toString()
        info["屏幕密度"] = metrics.density.toString()

        return info
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    EinkKtTheme {
        Greeting2("Android")
    }
}