package com.example.railwayqrapp.authentication

import android.graphics.Color
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.railwayqrapp.Screens
import com.example.railwayqrapp.SystemColors
import com.example.railwayqrapp.data.User
import com.example.railwayqrapp.ui.theme.buttonBackgroundColor
import com.example.railwayqrapp.ui.theme.fadedWhite
import org.w3c.dom.Text

@Composable
fun SignUpScreen(
    navController: NavController,
    viewModel: AuthViewModel
) {
    val context = LocalContext.current
    SystemColors(
        navigationBarColor = Color.Red,
        systemBarsColor = Color.Red,
        statusBarColor = fadedWhite
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.White,
                        Color.Red
                    )
                )
            )
            .padding(8.dp)
            .padding(top = 40.dp)
    ) {
        var firstNameState by remember {
            mutableStateOf("")
        }
        var lastNameState by remember {
            mutableStateOf("")
        }
        var emailState by remember {
            mutableStateOf("")
        }
        var passwordState by remember {
            mutableStateOf("")
        }
        var progressBarState by remember {
            mutableStateOf(false)
        }

        val errorToastState = remember{ mutableStateOf(false)}
        val errorSaveUserState = remember{ mutableStateOf(false)}

        val signUpState = viewModel.signUpState.collectAsState()
        val saveUserState = viewModel.saveUserState.collectAsState()

        signUpState.value?.let { state ->
            when (state) {
                ProgressState.Success -> {
                    Log.d("DBTesting", "SignUpScreen: HERE")
                    val currUser = User(
                        viewModel.getCurrentUser()!!.uid,
                        firstNameState,
                        lastNameState
                    )
                    viewModel.saveUserToDB(currUser)
                }

                ProgressState.Loading -> {
                    progressBarState = true
                }

                else -> {
                    progressBarState = false
                    LaunchedEffect(key1 = errorToastState.value){
                        Toast.makeText(context,"Some error occurred!", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        saveUserState.value?.let { state ->
            when (state) {
                ProgressState.Success -> {
                    progressBarState = false
                    navController.navigate(Screens.HomeScreen.route){
                        popUpTo(Screens.SignInScreen.route){
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }

                ProgressState.Loading -> {
                    progressBarState = true
                }

                else -> {
                    progressBarState = false
                    LaunchedEffect(key1 = errorSaveUserState){
                        Toast.makeText(context, "Some error occurred!", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        Text(
            modifier = Modifier
                .weight(.15f)
                .fillMaxWidth()
                .padding(
                    start = 20.dp,
                    end = 20.dp
                ),
            text = "Get Started",
            fontWeight = FontWeight.SemiBold,
            fontSize = 30.sp,
        )

        // Text Fields Section
        Column(
            modifier = Modifier
                .weight(.5f)
                .fillMaxSize()
                .padding(
                    start = 20.dp,
                    end = 20.dp
                )
        ) {
            CustomTextField(
                modifier = Modifier.fillMaxWidth(),
                label = "First Name",
                placeholder = "Enter your first name",
                value = firstNameState,
                keyboardType = KeyboardType.Text,
                textVisible = true,
                onValueChange = { currValue ->
                    firstNameState = currValue
                }
            )
            Spacer(modifier = Modifier.height(20.dp))
            CustomTextField(
                modifier = Modifier.fillMaxWidth(),
                label = "Last Name",
                placeholder = "Enter your last name",
                value = lastNameState,
                keyboardType = KeyboardType.Text,
                textVisible = true,
                onValueChange = { currValue ->
                    lastNameState = currValue
                }
            )
            Spacer(modifier = Modifier.height(20.dp))
            CustomTextField(
                modifier = Modifier.fillMaxWidth(),
                label = "E-mail",
                placeholder = "Enter your E-mail Id",
                value = emailState,
                keyboardType = KeyboardType.Email,
                textVisible = true,
                onValueChange = { currValue ->
                    emailState = currValue
                }
            )
            Spacer(modifier = Modifier.height(20.dp))
            CustomTextField(
                modifier = Modifier.fillMaxWidth(),
                label = "Password",
                placeholder = "Enter your password",
                value = passwordState,
                keyboardType = KeyboardType.Password,
                textVisible = false,
                onValueChange = { currValue ->
                    passwordState = currValue
                }
            )
        }

        Box(
            modifier = Modifier
                .weight(.35f)
                .fillMaxSize()
                .padding(
                    start = 20.dp,
                    end = 20.dp
                ),
        ) {
            CustomButton(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .padding(
                        start = 45.dp,
                        end = 45.dp
                    ),
                text = "Sign Up",
                backgroundColor = buttonBackgroundColor,
                disabledBackgroundColor = Color.Transparent,
                enabled = emailState.isEmailValid()
                    && passwordState.isNotEmpty()
                    && firstNameState.isNotEmpty()
                    && lastNameState.isNotEmpty()
            ) {
                // Perform Sign Up
                errorToastState.value = !errorToastState.value
                errorSaveUserState.value = !errorSaveUserState.value
                val email = emailState
                val password = passwordState
                viewModel.signUpUser(email, password)
            }

            if (progressBarState) CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center),
                color = buttonBackgroundColor
            )

            Text(
                modifier = Modifier
                    .padding(
                        start = 16.dp,
                        bottom = 20.dp
                    )
                    .align(Alignment.BottomCenter)
                    .clickable {
                        // Handle sign in
                        navController.popBackStack()
                    }
                    .alpha(.6f),
                text = "Already a member? Sign in here...",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}