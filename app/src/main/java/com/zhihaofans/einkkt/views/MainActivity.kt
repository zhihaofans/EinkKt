package com.zhihaofans.einkkt.views

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zhihaofans.einkkt.views.components.MyTopBar
import com.zhihaofans.einkkt.views.components.TextAlert
import com.zhihaofans.einkkt.views.ui.theme.EinkKtTheme
import io.zhihao.library.android.kotlinEx.startActivity
import io.zhihao.library.android.util.AppUtil
import kotlin.system.exitProcess

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            EinkKtTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        MyTopBar("Eink", false) {
                            (context as? Activity)?.finish()
                        }
                    },
                    /* floatingActionButton = {
                         ExtendedFloatingActionButton(
                             text = { Text("Show snackbar") },
                             icon = { Icon(Icons.Filled.Build, contentDescription = "") },
                             onClick = {
                                 scope.launch {
                                     snackbarHostState.showSnackbar("Snackbar")
                                 }
                             }
                         )
                     }*/
                ) { innerPadding ->
                    MainView(
                        name = "Eink",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
private fun MainView(name: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val openDialog = remember { mutableStateOf(false) }
    TextAlert(
        openDialog, "错误", "点击了退出",
        onConfirmClick = {
            (context as? Activity)?.finishAffinity()
            exitProcess(0)
        },
        onCancelClick = { }
    )
    val buttonList = listOf(
        "应用管理" to {
            context.startActivity(AppManagerActivity::class.java)
        },
        "设备信息" to {
            context.startActivity(DevicesActivity::class.java)
        },
        "打开系统原生设置" to {
            AppUtil.launchApp(context, "com.android.settings")
        },
        "打开无线调试" to {
            openWirelessDebug(context)
        },
        "二维码" to {
            context.startActivity(QrcodeActivity::class.java)
        },
        "功能三：退出应用" to {
            openDialog.value = true
        }
    )
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally, // 水平居中！
    ) {
        Text(text = "Hello $name!")

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally // 居中按钮
        ) {
            items(buttonList) { (title, onClickFunc) ->
                Button(
                    onClick = onClickFunc as () -> Unit,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth(0.6f) // 控制按钮宽度
                ) {
                    Text(text = title)
                }
            }
        }
    }

}

private fun openWirelessDebug(context: Context) {
    try {
        // Android 11+ 部分系统
        val intent = Intent().apply {
            setClassName(
                "com.android.settings",
                "com.android.settings.Settings\$WirelessDebuggingActivity"
            )
        }

        context.startActivity(intent)

    } catch (e: Exception) {
        try {
            // 某些 ROM
            context.startActivity(
                Intent("android.settings.WIRELESS_DEBUGGING_SETTINGS")
            )

        } catch (e: Exception) {
            try {
                // 某些 ROM
                context.startActivity(
                    Intent("com.android.settings.WIFI_ADB_SETTINGS")
                )

            } catch (e: Exception) {
                // 最后回退到开发者选项
                context.startActivity(
                    Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS)
                )

                Toast.makeText(
                    context,
                    "未找到无线调试页面，请手动进入开发者选项",
                    Toast.LENGTH_SHORT
                ).show()

            }

        }

    }
}

@Preview(showBackground = true)
@Composable
fun MainViewPreview() {
    EinkKtTheme {
        MainView("Android")
    }
}