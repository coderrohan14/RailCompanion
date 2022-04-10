package com.example.railwayqrapp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.railwayqrapp.authentication.AuthViewModel
import com.example.railwayqrapp.data.User
import com.example.railwayqrapp.ui.theme.fadedWhite
import com.example.railwayqrapp.ui.theme.lightRed

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: AuthViewModel
) {
    val user = viewModel.getCurrentUser()
    viewModel.getUserFromFirebase()
    SystemColors(
        navigationBarColor = fadedWhite,
        systemBarsColor = fadedWhite,
        statusBarColor = lightRed
    )
    val userDataState = viewModel.userData.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(fadedWhite)
    ) {
        val currUser: MutableState<User> = remember { mutableStateOf(User()) }
        if(userDataState.value==null){
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    color = lightRed
                )
            }
        }
        userDataState.value?.let { user ->
            currUser.value = user
        }
        TopBar(
            firstName = currUser.value.firstName,
            onDetailsClicked = {
                // Navigate to train details screen...
            }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    firstName: String,
    onDetailsClicked: () -> Unit
) {
    Surface(
        onClick = { },
        elevation = 8.dp,
        enabled = false
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .background(lightRed)
                .padding(8.dp)
                .padding(start = 4.dp)
        ) {
            Column {
                Text(
                    "Hello, $firstName",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = fadedWhite
                )
                Spacer(modifier = Modifier.height(4.dp))
                UnderlinedText(
                    modifier = Modifier.clickable { onDetailsClicked() },
                    text = "View Train Details",
                    textColor = fadedWhite
                )
            }
        }
    }
}

@Composable
fun UnderlinedText(
    modifier: Modifier = Modifier,
    text: String,
    textColor: Color = Color.Black
) {
    var layout by remember { mutableStateOf<TextLayoutResult?>(null) }

    Text(
        text = text,
        color = textColor,
        onTextLayout = {
            layout = it
        },
        modifier = modifier.drawBehind {

            layout?.let {
                val thickness = 5f
                val dashPath = 15f
                val spacingExtra = 4f
                val offsetY = 6f

                for (i in 0 until it.lineCount) {
                    drawPath(
                        path = Path().apply {
                            moveTo(it.getLineLeft(i), it.getLineBottom(i) - spacingExtra + offsetY)
                            lineTo(it.getLineRight(i), it.getLineBottom(i) - spacingExtra + offsetY)
                        },
                        textColor,
                        style = Stroke(
                            width = thickness,
                            pathEffect = PathEffect.dashPathEffect(
                                floatArrayOf(dashPath, dashPath),
                                0f
                            )
                        )
                    )
                }
            }
        }
    )
}