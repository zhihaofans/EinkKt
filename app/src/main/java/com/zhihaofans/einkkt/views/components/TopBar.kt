package com.zhihaofans.einkkt.views.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopBar(
    title: String,
    hasBack: Boolean = false,
    actions: @Composable RowScope.() -> Unit = {},
    onBackClick: () -> Unit
) {
    androidx.compose.foundation.layout.Column {
        TopAppBar(
            title = { Text(text = title) },
            actions = actions,
            navigationIcon = {
                if (hasBack) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                }
            }
        )
        HorizontalDivider(
            Modifier, thickness = 1.dp,
            color = Color.Black
        )
    }
}
