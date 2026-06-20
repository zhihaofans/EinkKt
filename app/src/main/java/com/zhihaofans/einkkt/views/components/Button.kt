package com.zhihaofans.einkkt.views.components

import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MD3Button(
    text: String,
    onBackClick: () -> Unit
) {
    Button(onClick = { onBackClick() }) { Text(text) }
}