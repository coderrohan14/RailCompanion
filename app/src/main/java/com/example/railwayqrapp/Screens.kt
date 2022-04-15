package com.example.railwayqrapp

sealed class Screens(val route: String){
    object SignInScreen: Screens("sign_in_screen")
    object SignUpScreen: Screens("sign_up_screen")
    object HomeScreen: Screens("home_screen")
    object SeatsScreen: Screens("seats_screen")
    object QRCodeScannerScreen: Screens("qr_code_scanner_screen")
    object DetailsScreen: Screens("details_screen")
}
