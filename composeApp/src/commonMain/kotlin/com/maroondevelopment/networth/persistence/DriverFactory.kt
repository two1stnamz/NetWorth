package com.maroondevelopment.networth.persistence

import app.cash.sqldelight.db.SqlDriver
import com.maroondevelopment.networth.Database

expect class DriverFactory {
    fun createDriver(): SqlDriver
}

fun createDatabase(driverFactory: DriverFactory): Database {
    val driver = driverFactory.createDriver()
    val database = Database(driver)

    return database
}