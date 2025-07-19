package com.zhihaofans.einkkt.views

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.zhihaofans.einkkt.views.ui.theme.EinkKtTheme
import io.ktor.http.ContentType
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import java.io.File

class SendFileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EinkKtTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting3(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting3(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

fun startKtorServer(context: Context) {
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
fun GreetingPreview4() {
    EinkKtTheme {
        Greeting3("Android")
    }
}