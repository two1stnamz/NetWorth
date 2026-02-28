package com.maroondevelopment.networth.persistence

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.maroondevelopment.App
import com.maroondevelopment.networth.Database

actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(Database.Schema, App.instance, "persistence.db")
    }
}