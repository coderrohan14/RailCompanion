package com.example.railwayqrapp.screens

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.example.railwayqrapp.R
import com.example.railwayqrapp.SystemColors
import com.example.railwayqrapp.data.PassengerInfo
import com.example.railwayqrapp.ui.theme.darkGray
import com.example.railwayqrapp.ui.theme.fadedWhite
import com.example.railwayqrapp.ui.theme.lightRed
import com.example.railwayqrapp.viewModels.HomeViewModel

@Composable
fun DetailsScreen(
    navController: NavController,
    backstack: NavBackStackEntry,
    homeViewModel: HomeViewModel
) {
    SystemColors(
        navigationBarColor = fadedWhite,
        systemBarsColor = fadedWhite,
        statusBarColor = lightRed
    )
    val passengersDetails = homeViewModel.passengersState.collectAsState()
    val coachNumber = backstack.arguments?.getString("coachNumber")
    val seatNumber = backstack.arguments?.getString("seatNumber")
    val currPassenger = passengersDetails.value.data[coachNumber]?.get(seatNumber)
    if (coachNumber == null || seatNumber == null || currPassenger == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(fadedWhite),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Some error occurred....please try again later.",
                fontSize = 20.sp,
                color = lightRed
            )
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            DetailsTopBar(text = "Details") {
                navController.popBackStack()
            }
            Spacer(modifier = Modifier.height(20.dp))
            DetailsSection(
                modifier = Modifier.padding(12.dp),
                passengerInfo = currPassenger
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DetailsTopBar(
    modifier: Modifier = Modifier,
    text: String,
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
                text = text,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun DetailsSection(
    modifier: Modifier = Modifier,
    passengerInfo: PassengerInfo
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "First Name : ${passengerInfo.firstName}",
            fontSize = 20.sp,
            color = darkGray,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Last Name : ${passengerInfo.lastName}",
            fontSize = 20.sp,
            color = darkGray,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Pass. Id : ${passengerInfo.passengerId}",
            fontSize = 20.sp,
            color = darkGray,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "PNR : ${passengerInfo.PNR}",
            fontSize = 20.sp,
            color = darkGray,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Age : ${passengerInfo.age}",
            fontSize = 20.sp,
            color = darkGray,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Boarding Station : ${passengerInfo.from}",
            fontSize = 20.sp,
            color = darkGray,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Destination Station : ${passengerInfo.to}",
            fontSize = 20.sp,
            color = darkGray,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Coach : ${passengerInfo.coach}",
            fontSize = 20.sp,
            color = darkGray,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Seat Number : ${passengerInfo.seatNumber}",
            fontSize = 20.sp,
            color = darkGray,
            fontWeight = FontWeight.SemiBold
        )
    }
}