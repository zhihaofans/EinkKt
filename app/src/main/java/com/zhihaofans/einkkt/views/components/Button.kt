package com.zhihaofans.einkkt.views.components

import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MD3Button(
    text: String,
    onBackClick: () -> Unit
) {
    Button(onClick = { onBackClick() }) { Text(text) }
}

@Composable
fun MD3Button(
    text: String,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit
) {
    Button(onClick = { onBackClick() }, modifier = modifier) { Text(text) }
}