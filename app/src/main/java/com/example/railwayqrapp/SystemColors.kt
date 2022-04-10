package com.example.railwayqrapp

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun SystemColors(
    navigationBarColor: Color,
    systemBarsColor: Color,
    statusBarColor: Color
) {
    val systemUiController = rememberSystemUiController()
    systemUiController.setNavigationBarColor(navigationBarColor)
    systemUiController.setSystemBarsColor(systemBarsColor)
    systemUiController.setStatusBarColor(statusBarColor)
}