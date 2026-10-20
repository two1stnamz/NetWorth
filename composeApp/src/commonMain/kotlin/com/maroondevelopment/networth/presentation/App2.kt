package com.maroondevelopment.networth.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.maroondevelopment.networth.presentation.account.AccountInfoScreen
import com.maroondevelopment.networth.presentation.history.PortfolioHistoryScreen
import com.maroondevelopment.networth.presentation.portfolio.PortfolioScreen
import com.maroondevelopment.networth.presentation.route.Route
import com.maroondevelopment.networth.presentation.theme.AppTheme

@Composable
fun App2() {
    val navController = rememberNavController()

    AppTheme {

        NavHost(
            navController = navController,
            startDestination = Route.Summary,
        ) {

            composable<Route.Summary> {

                PortfolioScreen(
                    onAccountSelected = { accountId ->
                        navController.navigate(Route.Account(accountId))
                    },
                    onHistorySelected = { navController.navigate(Route.History) }
                )

            }

            composable<Route.History> {

                PortfolioHistoryScreen(
                    onBack = { navController.popBackStack() }
                )

            }

            composable<Route.Account> { backStackEntry ->

                AccountInfoScreen(
                    accountId = backStackEntry.toRoute<Route.Account>().id,
                    onBack = { navController.popBackStack() }
                )

            }

        }

    }
}
