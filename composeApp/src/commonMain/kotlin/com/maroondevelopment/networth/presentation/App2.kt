package com.maroondevelopment.networth.presentation

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.maroondevelopment.networth.presentation.route.Route
import com.maroondevelopment.networth.presentation.summary.SummaryScreen

@Composable
fun App2() {
    val navController = rememberNavController()

    MaterialTheme {

        NavHost(
            navController = navController,
            startDestination = Route.Summary,
        ) {

            composable<Route.Summary> {

                SummaryScreen()

            }

        }

    }
}