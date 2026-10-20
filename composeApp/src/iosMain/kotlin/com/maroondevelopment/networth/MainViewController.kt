package com.maroondevelopment.networth

import androidx.compose.ui.window.ComposeUIViewController
import com.maroondevelopment.networth.di.initKoin
import com.maroondevelopment.networth.persistence.DriverFactory
import com.maroondevelopment.networth.presentation.App
import com.maroondevelopment.networth.presentation.App2

fun MainViewController() = ComposeUIViewController { App2() }

/** Call once from `iosApp` before presenting [MainViewController]. */
fun initialiseKoin() {
    initKoin(DriverFactory())
}
