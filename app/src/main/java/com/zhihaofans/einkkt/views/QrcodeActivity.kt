package com.zhihaofans.einkkt.views

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.zhihaofans.einkkt.views.components.MD3Button
import com.zhihaofans.einkkt.views.components.MyTopBar
import com.zhihaofans.einkkt.views.ui.theme.EinkKtTheme
import io.zhihao.library.android.util.ShareUtil
import java.io.File
import java.io.FileOutputStream

class QrcodeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            EinkKtTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        MyTopBar(
                            "二维码", true,
                            actions = {
                                IconButton(
                                    onClick = {
                                        // TODO: 扫描二维码
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.QrCodeScanner,
                                        contentDescription = "扫描二维码"
                                    )
                                }
                            }) {
                            (context as? Activity)?.finish()
                        }
                    },
                ) { innerPadding ->
                    var qrContent by remember {
                        mutableStateOf("https://github.com/zhihaofans")
                    }
                    val qrBitmap = remember(qrContent) {
                        if (qrContent.isBlank()) {
                            null
                        } else {
                            generateQrCode(qrContent)
                        }
                    }
                    val scrollState = rememberScrollState()

                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .imePadding()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "二维码工具",
                            style = MaterialTheme.typography.headlineSmall
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            tonalElevation = 2.dp
                        ) {
                            Box(
                                modifier = Modifier.padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                qrBitmap?.let {
                                    Image(
                                        bitmap = it.asImageBitmap(),
                                        contentDescription = "二维码",
                                        modifier = Modifier.size(200.dp)
                                    )
                                } ?: Text(
                                    text = "请输入内容生成二维码",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = qrContent,
                            onValueChange = {
                                qrContent = it
                            },
                            modifier = Modifier.fillMaxWidth(),
                            label = {
                                Text("二维码内容")
                            },
                            supportingText = {
                                Text("输入内容后二维码会自动更新")
                            },
                            singleLine = false
                        )

                        Spacer(modifier = Modifier.height(24.dp))
                        @OptIn(ExperimentalLayoutApi::class)
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MD3Button("粘贴") {
                                //TODO:copyToClipboard()
                            }
                            MD3Button("复制") {
                                //TODO:copyToClipboard()
                            }
                            MD3Button("保存二维码") {
                                //TODO:saveQrImage()
                                if (qrBitmap != null) {
                                    shareBitmap(context, qrBitmap)
                                }
                            }
                            MD3Button("分享") {
                                try {
                                    val share = ShareUtil(context)
                                    share.shareText(qrContent)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun generateQrCode(
    content: String,
    size: Int = 512
): Bitmap {
    val bitMatrix = MultiFormatWriter().encode(
        content,
        BarcodeFormat.QR_CODE,
        size,
        size
    )
    val pixels = IntArray(size * size)
    for (y in 0 until size) {
        for (x in 0 until size) {
            pixels[y * size + x] =
                if (bitMatrix[x, y]) {
                    Color.BLACK
                } else {
                    Color.WHITE
                }
        }
    }

    return Bitmap.createBitmap(
        pixels,
        size,
        size,
        Bitmap.Config.ARGB_8888
    )
}

fun shareBitmap(
    context: Context,
    bitmap: Bitmap,
    fileName: String = "share.png"
) {
    try {
        // 保存到应用缓存目录
        val file = File(context.cacheDir, fileName)

        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        // 获取 content:// Uri
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        // 分享
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"

            putExtra(Intent.EXTRA_STREAM, uri)

            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(
            Intent.createChooser(intent, "分享二维码")
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )

    } catch (e: Exception) {
        e.printStackTrace()

    }

}