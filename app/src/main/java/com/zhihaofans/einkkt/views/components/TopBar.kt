package com.zhihaofans.einkkt.views.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopBar(
    title: String,
    hasBack: Boolean = false,
    onBackClick: () -> Unit
) {
    androidx.compose.foundation.layout.Column {
        TopAppBar(
            title = { Text(text = title) },
            navigationIcon = {
                if (hasBack) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                }
            }
        )
        Divider(
            color = Color.Black,
            thickness = 1.dp
        )
    }
}
