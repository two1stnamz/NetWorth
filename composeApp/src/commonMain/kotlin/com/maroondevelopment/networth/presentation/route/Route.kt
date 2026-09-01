package com.maroondevelopment.networth.presentation.route

import kotlinx.serialization.Serializable

sealed interface Route {

    @Serializable
    data object Summary : Route

    @Serializable
    data class Account(val id: String) : Route
}