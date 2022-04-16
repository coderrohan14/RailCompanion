package com.example.railwayqrapp.screens

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.railwayqrapp.R
import com.example.railwayqrapp.Screens
import com.example.railwayqrapp.SystemColors
import com.example.railwayqrapp.authentication.AuthViewModel
import com.example.railwayqrapp.data.PassengerInfo
import com.example.railwayqrapp.data.TrainInfo
import com.example.railwayqrapp.data.User
import com.example.railwayqrapp.navigate
import com.example.railwayqrapp.navigateClearFullBackStack
import com.example.railwayqrapp.ui.theme.buttonBackgroundColor
import com.example.railwayqrapp.ui.theme.darkGray
import com.example.railwayqrapp.ui.theme.darkGreen
import com.example.railwayqrapp.ui.theme.fadedWhite
import com.example.railwayqrapp.ui.theme.lightRed
import com.example.railwayqrapp.ui.theme.listItemBackground
import com.example.railwayqrapp.viewModels.HomeViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    homeViewModel: HomeViewModel
) {
    val user = authViewModel.getCurrentUser()
    authViewModel.getUserFromFirebase()
    user?.let {
        homeViewModel.getTrainInfoForUser(it.uid)
    }
    SystemColors(
        navigationBarColor = fadedWhite,
        systemBarsColor = fadedWhite,
        statusBarColor = lightRed
    )
    val userDataState = authViewModel.userData.collectAsState()
    val detailsAlertState = remember {
        mutableStateOf(false)
    }
    val logOutDialogState = remember {
        mutableStateOf(false)
    }
    val trainInfoState = homeViewModel.trainInfoState.collectAsState()
    trainInfoState.value?.let { trainInfo ->
        if (detailsAlertState.value) {
            TrainDetailsAlertBox(trainInfo = trainInfo) {
                detailsAlertState.value = false
            }
        }
    }
    if (logOutDialogState.value) {
        LogOutAlertBox(
            onCancelClicked = {
                logOutDialogState.value = false
            },
            onLogOutClicked = {
                // Log Out the user
                authViewModel.logOut()
                navigateClearFullBackStack(
                    navController,
                    destination = Screens.SignInScreen.route
                )
            }
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(fadedWhite)
    ) {
        val currUser: MutableState<User> = remember { mutableStateOf(User()) }
        if (userDataState.value == null) {
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
        val passengerInfo = homeViewModel.passengersState.collectAsState()
        TopBar(
            firstName = currUser.value.firstName,
            onDetailsClicked = {
                detailsAlertState.value = true
            },
            onLogOutClicked = {
                logOutDialogState.value = true
            }
        )

        ListSection(
            modifier = Modifier.fillMaxSize(),
            passengersData = passengerInfo.value.data
        ) { coach ->
            navigate(
                navController = navController,
                destination = Screens.SeatsScreen.route + "/$coach"
            )
        }
    }
}

@Composable
fun TrainDetailsAlertBox(
    modifier: Modifier = Modifier,
    trainInfo: TrainInfo,
    onCloseClicked: () -> Unit
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = { },
        title = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier,
                    text = "Train Details",
                    fontSize = 24.sp,
                    color = darkGray,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Train Number : ${trainInfo.trainNo}",
                    fontSize = 16.sp,
                    color = darkGray,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Train Name : ${trainInfo.trainName}",
                    fontSize = 16.sp,
                    color = darkGray,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Departure : ${trainInfo.depTime}",
                    fontSize = 16.sp,
                    color = darkGray,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Arrival : ${trainInfo.arrTime}",
                    fontSize = 16.sp,
                    color = darkGray,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        buttons = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    modifier = Modifier.padding(12.dp),
                    onClick = { onCloseClicked() },
                    colors = ButtonDefaults.buttonColors(backgroundColor = lightRed),
                    shape = RoundedCornerShape(20)
                ) {
                    Text(
                        text = "Close",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = darkGray,
                    )
                }
            }
        },
        backgroundColor = listItemBackground
    )
}

@Composable
fun LogOutAlertBox(
    modifier: Modifier = Modifier,
    onCancelClicked: () -> Unit,
    onLogOutClicked: () -> Unit
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = { },
        title = {
            Text(
                modifier = Modifier,
                text = "Are you sure you want to log out?",
                fontSize = 24.sp,
                color = darkGray,
                fontWeight = FontWeight.Bold
            )
        },
        buttons = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { onCancelClicked() },
                    colors = ButtonDefaults.buttonColors(backgroundColor = buttonBackgroundColor),
                    shape = RoundedCornerShape(20)
                ) {
                    Text(
                        text = "Cancel",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = darkGray,
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { onLogOutClicked() },
                    colors = ButtonDefaults.buttonColors(backgroundColor = lightRed),
                    shape = RoundedCornerShape(20)
                ) {
                    Text(
                        text = "Log Out",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = darkGray
                    )
                }
            }
        },
        backgroundColor = listItemBackground
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    firstName: String,
    onDetailsClicked: () -> Unit,
    onLogOutClicked: () -> Unit
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
                .padding(start = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(.8f)
            ) {
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
            Icon(
                modifier = Modifier
                    .weight(.2f)
                    .size(40.dp)
                    .clickable {
                        onLogOutClicked()
                    },
                imageVector = Icons.Default.ExitToApp,
                contentDescription = "Exit"
            )
        }
    }
}

@Composable
fun ListSection(
    modifier: Modifier = Modifier,
    passengersData: HashMap<String, HashMap<String, PassengerInfo>>,
    onCoachClicked: (String) -> Unit
) {
    Log.d("ListItem", "List -> $passengersData")
    val coachList = passengersData.keys.sorted()
    LazyColumn(
        modifier = modifier.padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        for (coach in coachList) {
            val total = passengersData[coach]?.size
            var verified = 0
            passengersData[coach]?.values?.forEach { passenger ->
                if (passenger.verified) verified++
            }
            val unverified = total?.minus(verified)
            Log.d("ListItem", "Item -> $coach, v->$verified, uv->$unverified")
            item {
                ListItem(
                    coachNumber = coach,
                    total = total.toString(),
                    verified = verified.toString(),
                    unverified = unverified.toString(),
                    onListItemClicked = { coachNumber ->
                        onCoachClicked(coachNumber)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ListItem(
    modifier: Modifier = Modifier,
    coachNumber: String,
    total: String,
    verified: String,
    unverified: String,
    onListItemClicked: (String) -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        onClick = { onListItemClicked(coachNumber) },
        color = listItemBackground,
        border = BorderStroke(2.dp, darkGray),
        elevation = 4.dp,
        shape = RoundedCornerShape(20)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .padding(start = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(.4f),
                text = "Coach - $coachNumber",
                fontSize = 22.sp,
                color = darkGray,
                fontWeight = FontWeight.Bold
            )
            Column(
                modifier = Modifier.weight(.4f)
            ) {
                Text(
                    text = "Total Seats - $total",
                    fontSize = 16.sp,
                    color = darkGray,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Verified - $verified",
                    fontSize = 16.sp,
                    color = darkGreen,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Unverified - $unverified",
                    fontSize = 16.sp,
                    color = lightRed,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Icon(
                modifier = Modifier
                    .weight(.2f)
                    .size(50.dp)
                    .clickable {
                        onListItemClicked(coachNumber)
                    },
                painter = painterResource(
                    id = R.drawable.ic_arrow_forward
                ),
                contentDescription = "Right Arrow",
                tint = darkGray
            )
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