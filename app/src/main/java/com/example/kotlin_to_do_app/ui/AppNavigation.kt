package com.example.kotlin_to_do_app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kotlin_to_do_app.auth.AuthViewModel
import com.example.kotlin_to_do_app.ui.screens.LoginScreen
import com.example.kotlin_to_do_app.ui.screens.TaskListScreen
import com.example.kotlin_to_do_app.viewmodel.TaskViewModel
import com.example.kotlin_to_do_app.viewmodel.ThemeViewModel

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel,
    themeViewModel: ThemeViewModel
) {
    val taskViewModel: TaskViewModel = viewModel()
    val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()

    NavHost(
        navController = navController,
        startDestination = if (authViewModel.isUserLoggedIn()) "tasks" else "login"
    ) {
        composable("login") {
            LoginScreen(
                onNavigateToTasks = { navController.navigate("tasks") {
                    popUpTo("login") { inclusive = true }
                }}
            )
        }

        composable("tasks") {
            TaskListScreen(
                onLogout = {
                    authViewModel.logout()
                    navController.navigate("login") {
                        popUpTo("tasks") { inclusive = true }
                    }
                },
                taskViewModel = taskViewModel,
                isDarkTheme = isDarkTheme,
                onToggleTheme = { themeViewModel.toggleTheme() }
            )
        }
    }
}