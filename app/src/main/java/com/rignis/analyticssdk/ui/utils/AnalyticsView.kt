package com.rignis.analyticssdk.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
fun AnalyticsOnView(onView: () -> Unit) {
    LaunchedEffect(true) {
        onView.invoke()
    }
}
