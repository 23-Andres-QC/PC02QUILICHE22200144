package com.example.pc02quiliche22200144.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pc02quiliche22200144.data.remote.FirebaseAuthManager
import com.example.pc02quiliche22200144.presentation.auth.LoginScreen
import com.example.pc02quiliche22200144.presentation.auth.RegisterScreen
import com.example.pc02quiliche22200144.presentation.converter.ConverterScreen
import com.example.pc02quiliche22200144.presentation.history.HistoryScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val startDestination = if (FirebaseAuthManager.currentUser != null) "converter" else "login"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("converter") { ConverterScreen(navController) }
        composable("history") { HistoryScreen(navController) }
    }
}
