package com.example.railwayqrapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.railwayqrapp.authentication.AuthViewModel
import com.example.railwayqrapp.authentication.SignInScreen
import com.example.railwayqrapp.authentication.SignUpScreen
import com.example.railwayqrapp.ui.theme.RailwayQRAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RailwayQRAppTheme {
                // A surface container using the 'background' color from the theme
                val viewModel = viewModel<AuthViewModel>()
                val navController = rememberNavController()
                val startDestination = if (viewModel.isUserSignedIn()) Screens.HomeScreen.route
                else Screens.SignInScreen.route
                NavHost(
                    navController = navController,
                    startDestination = startDestination
                ) {

                    composable(Screens.SignInScreen.route) {
                        SignInScreen(navController, viewModel)
                    }

                    composable(Screens.SignUpScreen.route) {
                        SignUpScreen(navController, viewModel)
                    }

                    composable(Screens.HomeScreen.route) {
                        HomeScreen(navController,viewModel)
                    }
                }
            }
        }
    }
}

