package com.example.railwayqrapp

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.railwayqrapp.authentication.AuthViewModel
import com.example.railwayqrapp.authentication.SignInScreen
import com.example.railwayqrapp.authentication.SignUpScreen
import com.example.railwayqrapp.screens.DetailsScreen
import com.example.railwayqrapp.screens.HomeScreen
import com.example.railwayqrapp.screens.SeatsScreen
import com.example.railwayqrapp.viewModels.HomeViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String,
    authViewModel: AuthViewModel,
    homeViewModel: HomeViewModel
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        composable(Screens.SignInScreen.route) {
            SignInScreen(navController, authViewModel)
        }

        composable(Screens.SignUpScreen.route) {
            SignUpScreen(navController, authViewModel)
        }

        composable(Screens.HomeScreen.route) {
            HomeScreen(navController, authViewModel, homeViewModel)
        }

        composable(
            route = Screens.SeatsScreen.route + "/{coachNumber}",
            arguments = listOf(
                navArgument("coachNumber"){
                    type = NavType.StringType
                }
            )
        ){ backstack->
            SeatsScreen(navController, backstack, homeViewModel)
        }

        composable(
            Screens.QRCodeScannerScreen.route + "/{coachNumber}/{seatNumber}",
            arguments = listOf(
                navArgument("coachNumber"){
                    type = NavType.StringType
                },
                navArgument("seatNumber"){
                    type = NavType.StringType
                }
            )
        ){ backstack->
            QRScannerScreen(navController, backstack, homeViewModel)
        }

        composable(
            Screens.DetailsScreen.route + "/{coachNumber}/{seatNumber}",
            arguments = listOf(
                navArgument("coachNumber"){
                    type = NavType.StringType
                },
                navArgument("seatNumber"){
                    type = NavType.StringType
                }
            )
        ){ backstack->
            DetailsScreen(navController, backstack, homeViewModel)
        }
    }
}

// fun navigateClearBackStack(
//     navController: NavController,
//     source: String,
//     destination: String
// ){
//     navController.navigate(destination) {
//         popUpTo(source) {
//             inclusive = true
//         }
//         launchSingleTop = true
//     }
// }

fun navigateClearFullBackStack(
    navController: NavController,
    destination: String
){
    navController.navigate(destination) {
        popUpTo(0)
        launchSingleTop = true
    }
}

fun navigate(
    navController: NavController,
    destination: String
){
    navController.navigate(destination) {
        launchSingleTop = true
    }
}


