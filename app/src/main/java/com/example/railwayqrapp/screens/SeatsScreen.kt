package com.example.railwayqrapp.screens

import android.util.Log
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.example.railwayqrapp.R
import com.example.railwayqrapp.Screens
import com.example.railwayqrapp.SystemColors
import com.example.railwayqrapp.data.PassengerInfo
import com.example.railwayqrapp.navigate
import com.example.railwayqrapp.navigateClearFullBackStack
import com.example.railwayqrapp.ui.theme.aqua
import com.example.railwayqrapp.ui.theme.darkGray
import com.example.railwayqrapp.ui.theme.darkGreen
import com.example.railwayqrapp.ui.theme.fadedWhite
import com.example.railwayqrapp.ui.theme.lightRed
import com.example.railwayqrapp.ui.theme.listItemBackground
import com.example.railwayqrapp.ui.theme.verifiedBackground
import com.example.railwayqrapp.viewModels.HomeViewModel

@Composable
fun SeatsScreen(
    navController: NavController,
    backstack: NavBackStackEntry,
    homeViewModel: HomeViewModel
) {
    SystemColors(
        navigationBarColor = fadedWhite,
        systemBarsColor = fadedWhite,
        statusBarColor = lightRed
    )
    val coachNumber = backstack.arguments?.getString("coachNumber")
    val passengerInfo = homeViewModel.passengersState.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(fadedWhite)
    ) {
        coachNumber?.let {
            SeatsTopBar(coachNumber = coachNumber) {
                navigateClearFullBackStack(
                    navController = navController,
                    destination = Screens.HomeScreen.route
                )
            }
            SeatListSection(
                modifier = Modifier.fillMaxSize(),
                coach = coachNumber,
                passengersData = passengerInfo.value.data
            ) { currPassenger ->
                // direct to qr scanner or details screen
                if (!currPassenger.verified) {
                    Log.d(
                        "PassengerInfoTag",
                        "FName -> ${currPassenger.firstName}, verified -> ${currPassenger.verified}}"
                    )
                    navigate(
                        navController = navController,
                        destination = Screens.QRCodeScannerScreen.route + "/${currPassenger.coach}/${currPassenger.seatNumber}"
                    )
                } else {
                    // navigate to details screen
                    navigate(
                        navController = navController,
                        destination = Screens.DetailsScreen.route + "/${currPassenger.coach}/${currPassenger.seatNumber}"
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SeatsTopBar(
    modifier: Modifier = Modifier,
    coachNumber: String,
    onBackClicked: () -> Unit
) {
    Surface(
        onClick = {},
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
            Icon(
                modifier = Modifier
                    .size(32.dp)
                    .clickable {
                        onBackClicked()
                    },
                painter = painterResource(id = R.drawable.ic_back_arrow),
                contentDescription = "Back Arrow"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Coach - $coachNumber",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SeatListSection(
    modifier: Modifier = Modifier,
    coach: String,
    passengersData: HashMap<String, HashMap<String, PassengerInfo>>,
    onSeatClicked: (PassengerInfo) -> Unit
) {
    val passengersList = passengersData[coach]?.keys
    val seatsList = passengersList?.sortedBy { it.toInt() }
    LazyColumn(
        modifier = modifier.padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (seatsList != null) {
            for (currSeat in seatsList) {
                val passenger = passengersData[coach]?.get(currSeat)
                passenger?.let { currPassenger ->
                    item {
                        SeatListItem(
                            seatNo = currPassenger.seatNumber,
                            verified = currPassenger.verified
                        ) {
                            onSeatClicked(passenger)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SeatListItem(
    modifier: Modifier = Modifier,
    seatNo: String,
    verified: Boolean,
    onButtonClicked: () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        onClick = { },
        color = if (verified) verifiedBackground else listItemBackground,
        border = BorderStroke(2.dp, darkGray),
        elevation = 4.dp,
        shape = RoundedCornerShape(20)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .padding(start = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier
                    .weight(.3f)
                    .padding(6.dp),
                text = "Seat - $seatNo",
                fontSize = 22.sp,
                color = darkGray,
                fontWeight = FontWeight.Bold
            )
            Column(
                modifier = Modifier
                    .weight(.3f)
                    .padding(6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    modifier = Modifier.size(25.dp),
                    painter = painterResource(
                        id = if (verified) R.drawable.verified_logo else R.drawable.unverified_logo
                    ),
                    contentDescription = "Status Logo",
                    tint = if (verified) darkGreen else lightRed
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (verified) "Verified" else "Unverified",
                    fontSize = 16.sp,
                    color = if (verified) darkGreen else lightRed,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Button(
                modifier = Modifier
                    .weight(.4f)
                    .padding(6.dp),
                onClick = { onButtonClicked() },
                shape = RoundedCornerShape(20),
                elevation = ButtonDefaults.elevation(defaultElevation = 6.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = aqua),
            ) {
                if (!verified) {
                    Icon(
                        modifier = Modifier.size(22.dp),
                        painter = painterResource(id = R.drawable.ic_qr_scanner),
                        contentDescription = "scanner logo",
                        tint = darkGray
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = if (verified) "Details" else "Scan",
                    fontSize = if (verified) 20.sp else 16.sp,
                    color = darkGray,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ItemPreview() {
    SeatListItem(seatNo = "12", verified = true) {

    }
}