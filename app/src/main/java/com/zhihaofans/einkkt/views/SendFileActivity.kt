package com.zhihaofans.einkkt.views

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.zhihaofans.einkkt.views.ui.theme.EinkKtTheme
import io.ktor.http.ContentType
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import java.io.File
import java.net.Inet4Address
import java.net.NetworkInterface

class SendFileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EinkKtTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    FileReceiverScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun FileReceiverScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val ipAddress = remember { getLocalIpAddress() ?: "无法获取 IP" }
    val serverStarted = remember { mutableStateOf(false) }
    val qrBitmap = remember(ipAddress) { generateQrCodeBitmap("http://$ipAddress:8080") }

    LaunchedEffect(Unit) {
        if (!serverStarted.value) {
            startKtorServer(context)
            serverStarted.value = true
        }
    }

    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("📥 局域网文件接收服务已启动", style = MaterialTheme.typography.titleMedium)
        Text("🔗 访问地址: http://$ipAddress:8080")
        qrBitmap?.let {
            Image(bitmap = it.asImageBitmap(), contentDescription = "接收二维码")
        }
    }
}

fun getLocalIpAddress(): String? {
    return try {
        NetworkInterface.getNetworkInterfaces().toList().flatMap { it.inetAddresses.toList() }
            .firstOrNull { !it.isLoopbackAddress && it is Inet4Address }
            ?.hostAddress
    } catch (e: Exception) {
        null
    }
}

fun generateQrCodeBitmap(data: String): Bitmap? {
    return try {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, 512, 512)
        Bitmap.createBitmap(512, 512, Bitmap.Config.RGB_565).apply {
            for (x in 0 until width) {
                for (y in 0 until height) {
                    setPixel(
                        x,
                        y,
                        if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE
                    )
                }
            }
        }
    } catch (e: Exception) {
        null
    }
}

fun startKtorServer(context: android.content.Context) {
    embeddedServer(CIO, port = 8080, host = "0.0.0.0") {
        install(ContentNegotiation) {
            json()
        }
        routing {
            get("/") {
                call.respondText(
                    """
                    <html>
                    <body>
                        <h3>发送文件或文本到本机</h3>
                        <form method="post" enctype="multipart/form-data" action="/upload">
                            文本: <input type="text" name="message"/><br/>
                            文件: <input type="file" name="file"/><br/>
                            <input type="submit" value="发送"/>
                        </form>
                    </body>
                    </html>
                    """.trimIndent(),
                    ContentType.Text.Html
                )
            }

            post("/upload") {
                val multipart = call.receiveMultipart()
                var receivedText: String? = null
                var savedFile: File? = null

                multipart.forEachPart { part ->
                    when (part) {
                        is PartData.FormItem -> {
                            if (part.name == "message") {
                                receivedText = part.value
                            }
                        }

                        is PartData.FileItem -> {
                            val fileName = part.originalFileName ?: "uploaded.bin"
                            val fileBytes = part.streamProvider().readBytes()
                            val file = File(context.filesDir, fileName)
                            file.writeBytes(fileBytes)
                            savedFile = file
                        }

                        else -> {}
                    }
                    part.dispose()
                }

                val response = buildString {
                    appendLine("✅ 上传成功！")
                    receivedText?.let { appendLine("收到文本: $it") }
                    savedFile?.let { appendLine("文件已保存: ${it.absolutePath}") }
                }

                call.respondText(response, ContentType.Text.Plain)
            }
        }
    }.start(wait = false)
}

@Preview(showBackground = true)
@Composable
fun PreviewFileReceiverScreen() {
    EinkKtTheme {
        FileReceiverScreen()
    }
}