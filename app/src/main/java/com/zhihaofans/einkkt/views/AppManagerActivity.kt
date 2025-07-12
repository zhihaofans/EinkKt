package com.zhihaofans.einkkt.views

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.zhihaofans.einkkt.views.components.MyTopBar
import com.zhihaofans.einkkt.views.ui.theme.EinkKtTheme
import io.zhihao.library.android.util.AppUtil

class AppManagerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            EinkKtTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        MyTopBar("应用管理", true) {
                            (context as? Activity)?.finish()
                        }
                    }) { innerPadding ->
                    AppManagerView(
                        Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

data class AppInfo(
    val appName: String,
    val packageName: String,
    val icon: Drawable
)

fun getInstalledApps(context: Context): List<AppInfo> {
    val pm = context.packageManager
    val intent = Intent(Intent.ACTION_MAIN, null).apply {
        addCategory(Intent.CATEGORY_LAUNCHER)
    }
    val resolveInfos = pm.queryIntentActivities(intent, 0)

    val appList = resolveInfos.map {
        val appName = it.loadLabel(pm).toString()
        val packageName = it.activityInfo.packageName
        val icon = it.loadIcon(pm)
        AppInfo(appName, packageName, icon)
    }
    return appList.sortedBy { it.appName }
}

@Composable
fun AppManagerView(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }
    var appList by remember { mutableStateOf(listOf<AppInfo>()) }

    LaunchedEffect(Unit) {
        // 模拟加载过程；你可以改成获取安装应用列表逻辑
//        delay(1500)
        /* appList = listOf(
             "com.android.chrome",
             "com.android.settings",
             "com.example.app"
         )*/
        appList = getInstalledApps(context)/*.map { it.appName }*/ // 获取安装的应用包名
        isLoading = false
    }

    if (isLoading) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(modifier = modifier) {
            items(appList) { appInfo ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            Log.e("AppManagerActivity", "Clicked on ${appInfo.appName}")
                            AppUtil.launchApp(context, appInfo.packageName)
                            Log.d("AppManagerActivity", "Launching ${appInfo.packageName}")
                        }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Image(
                        painter = rememberDrawablePainter(drawable = appInfo.icon),
                        contentDescription = appInfo.appName,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = appInfo.appName)
                        Text(
                            text = appInfo.packageName,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview3() {
    EinkKtTheme {
        //Greeting3("Android")
    }
}