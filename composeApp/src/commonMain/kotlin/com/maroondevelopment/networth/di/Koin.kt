package com.maroondevelopment.networth.di

import com.maroondevelopment.networth.persistence.DriverFactory
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

/**
 * Starts Koin with the shared [appModule]. Call once, from each platform's entry point,
 * before any composable that injects a view model is shown.
 */
fun initKoin(
    driverFactory: DriverFactory,
    declaration: KoinAppDeclaration = {}
): KoinApplication = startKoin {
    declaration()
    modules(appModule(driverFactory))
}
