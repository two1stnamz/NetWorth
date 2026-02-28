package com.maroondevelopment.networth.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object HttpFactory {

    val httpClient = HttpClient {
        // Ktor automatically picks OkHttp on Android and Darwin on iOS
        // if you don't specify an engine in the constructor.

        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                }
            )
        }
    }
}