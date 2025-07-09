package com.zhihaofans.einkkt.views.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextAlert(
    openDialog: MutableState<Boolean>,
    title: String,
    message: String,
    onConfirmClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    if (openDialog.value) {
        BasicAlertDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            content = {
                Box(
                    modifier = Modifier
                        .padding(24.dp)
                        .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp)) // ✅ 灰色边框
                        .background(Color.White, shape = RoundedCornerShape(8.dp)) // ✅ 白色背景
                        .padding(24.dp)
                ) {
                    Column {
                        Text(
                            text = title,
                            fontWeight = FontWeight.W700,
                            style = MaterialTheme.typography.headlineSmall
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = message,
                            fontSize = 16.sp
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(onClick = {
                                onCancelClick()
                                openDialog.value = false
                            }) {
                                Text("取消")
                            }

                            Button(onClick = {
                                onConfirmClick()
                                openDialog.value = false
                            }) {
                                Text("好的！")
                            }
                        }
                    }
                }
            }
        )
    }
}