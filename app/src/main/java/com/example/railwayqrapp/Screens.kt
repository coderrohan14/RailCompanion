package com.example.railwayqrapp

sealed class Screens(val route: String){
    object SignInScreen: Screens("sign_in_screen")
    object SignUpScreen: Screens("sign_up_screen")
    object HomeScreen: Screens("home_screen")
}
