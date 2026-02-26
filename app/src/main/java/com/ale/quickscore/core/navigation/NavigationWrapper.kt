package com.ale.quickscore.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ale.quickscore.features.auth.presentation.screens.LoginScreen
import com.ale.quickscore.features.auth.presentation.screens.RegisterScreen

@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = LoginRoute
    ) {
        composable<LoginRoute> {
            LoginScreen(
                onLoginSuccess = {
                    // próximamente navegar a Home
                },
                onNavigateToRegister = {
                    navController.navigate(RegisterRoute)
                }
            )
        }
        composable<RegisterRoute> {
            RegisterScreen(
                onRegisterSuccess = {
                    // próximamente navegar a Home
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }
    }
}