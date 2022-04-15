package com.example.railwayqrapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.railwayqrapp.authentication.AuthViewModel
import com.example.railwayqrapp.ui.theme.RailwayQRAppTheme
import com.example.railwayqrapp.viewModels.HomeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RailwayQRAppTheme {
                // A surface container using the 'background' color from the theme
                val authViewModel = viewModel<AuthViewModel>()
                val homeViewModel = viewModel<HomeViewModel>()
                LaunchedEffect(key1 = true){
                    homeViewModel.initializeDBSettings()
                    homeViewModel.addPassengerDataListener()
                }
                val navController = rememberNavController()
                val startDestination = if (authViewModel.isUserSignedIn()) Screens.HomeScreen.route
                else Screens.SignInScreen.route
                AppNavHost(
                    navController = navController,
                    startDestination = startDestination,
                    authViewModel = authViewModel,
                    homeViewModel = homeViewModel
                )
            }
        }
    }
}

