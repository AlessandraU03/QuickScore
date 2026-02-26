package com.ale.quickscore.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.ale.quickscore.features.auth.presentation.screens.LoginScreen
import com.ale.quickscore.features.auth.presentation.screens.RegisterScreen
import com.ale.quickscore.features.rooms.presentation.screens.HomeHostScreen
import com.ale.quickscore.features.rooms.presentation.screens.HomeParticipantScreen
import com.ale.quickscore.features.rooms.presentation.screens.RoomDetailScreen

@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = LoginRoute) {

        composable<LoginRoute> {
            LoginScreen(
                onLoginSuccess = { isHost ->
                    navController.navigate(HomeRoute(isHost)) {
                        popUpTo(LoginRoute) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(RegisterRoute) }
            )
        }

        composable<RegisterRoute> {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(LoginRoute) {
                        popUpTo(RegisterRoute) { inclusive = true }
                    }
                },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        composable<HomeRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<HomeRoute>()
            if (route.isHost) {
                HomeHostScreen(
                    onNavigateToRoom = { roomCode, isHost ->
                        navController.navigate(RoomDetailRoute(roomCode, isHost))
                    }
                )
            } else {
                HomeParticipantScreen(
                    onNavigateToRoom = { roomCode, isHost ->
                        navController.navigate(RoomDetailRoute(roomCode, isHost))
                    }
                )
            }
        }

        composable<RoomDetailRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<RoomDetailRoute>()
            RoomDetailScreen(
                roomCode = route.roomCode,
                onSessionEnded = { roomCode ->
                    navController.navigate(LeaderboardRoute(roomCode)) {
                        popUpTo(HomeRoute(route.isHost))
                    }
                }
            )
        }

    }
}
