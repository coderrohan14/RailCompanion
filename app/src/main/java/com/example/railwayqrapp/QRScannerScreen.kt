package com.example.railwayqrapp

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.example.railwayqrapp.authentication.ProgressState
import com.example.railwayqrapp.data.PassengerInfo
import com.example.railwayqrapp.screens.DetailsTopBar
import com.example.railwayqrapp.ui.theme.darkGray
import com.example.railwayqrapp.ui.theme.darkGreen
import com.example.railwayqrapp.ui.theme.fadedWhite
import com.example.railwayqrapp.ui.theme.lightRed
import com.example.railwayqrapp.ui.theme.listItemBackground
import com.example.railwayqrapp.ui.theme.verifiedBackground
import com.example.railwayqrapp.viewModels.HomeViewModel
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.CompoundBarcodeView

@Composable
fun QRScannerScreen(
    navController: NavController,
    backstack: NavBackStackEntry,
    homeViewModel: HomeViewModel
) {
    SystemColors(
        navigationBarColor = Color.Transparent,
        systemBarsColor = Color.Transparent,
        statusBarColor = lightRed
    )
    val passengersState = homeViewModel.passengersState.collectAsState()
    val passengerVerificationState = homeViewModel.passengerVerificationState.collectAsState()
    val progressBarState = remember { mutableStateOf(false) }
    val coachNumber = backstack.arguments?.getString("coachNumber")
    val seatNumber = backstack.arguments?.getString("seatNumber")
    val currPassenger = passengersState.value.data[coachNumber]?.get(seatNumber)

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
        Log.d(
            "PassengerQRInfo",
            "FName -> ${currPassenger.firstName}, verified -> ${currPassenger.verified}}"
        )
        val context = LocalContext.current
        var errorDialogState by remember {
            mutableStateOf(ErrorDialogState(dialogEnabled = false, alreadyExistingUser = false))
        }
        var matchedDialogState by remember {
            mutableStateOf(false)
        }
        var hasCamPermission by remember {
            mutableStateOf(
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            )
        }
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { granted ->
                hasCamPermission = granted
            }
        )
        LaunchedEffect(key1 = true) {
            launcher.launch(Manifest.permission.CAMERA)
        }
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            if (hasCamPermission) {
                var scanFlag by remember {
                    mutableStateOf(false)
                }
                if (errorDialogState.dialogEnabled) {
                    ErrorDialogBox(
                        alreadyExistingUser = errorDialogState.alreadyExistingUser,
                        onCancelClicked = {
                            // navigate back to seats screen
                            errorDialogState =
                                ErrorDialogState(dialogEnabled = false, alreadyExistingUser = false)
                            homeViewModel.resetProgressState()
                            navController.popBackStack()
                        },
                        onRetryClicked = {
                            errorDialogState =
                                ErrorDialogState(dialogEnabled = false, alreadyExistingUser = false)
                            scanFlag = false
                        }
                    )
                }
                if (matchedDialogState) {
                    MatchedDialogBox(
                        passengerInfo = currPassenger,
                        progressState = progressBarState.value,
                        onCancelClicked = {
                            matchedDialogState = false
                            scanFlag = false
                        },
                        onAcceptClicked = {
                            // navigate back to seats screen and set passenger as verified
                            homeViewModel.verifyPassengerOnDB(currPassenger)
                        }
                    )
                }
                passengerVerificationState.value?.let { state ->
                    when (state) {
                        is ProgressState.Success -> {
                            // Passenger verified successfully
                            progressBarState.value = false
                            matchedDialogState = false
                            Toast.makeText(
                                context,
                                "Passenger verified successfully!",
                                Toast.LENGTH_LONG
                            ).show()
                            homeViewModel.resetProgressState()
                            navController.popBackStack()
                        }

                        is ProgressState.Loading -> {
                            progressBarState.value = true
                        }

                        else -> {
                            // Error
                            progressBarState.value = false
                            matchedDialogState = false
                            Toast.makeText(context, state.toString(), Toast.LENGTH_LONG).show()
                            scanFlag = false
                        }
                    }
                }
                val compoundBarcodeView = remember {
                    CompoundBarcodeView(context).apply {
                        val capture = CaptureManager(context as Activity, this)
                        capture.initializeFromIntent(context.intent, null)
                        this.setStatusText("")
                        capture.decode()
                        this.resume()
                        this.decodeContinuous { result ->
                            if (scanFlag) {
                                return@decodeContinuous
                            }
                            scanFlag = true
                            result.text?.let { qrResult ->
                                Log.d("QRResult", "Result -> $qrResult ")
                                val passengerInfoList = qrResult.split(",")
                                if (passengerInfoList.size != 10) {
                                    // incorrect
                                    errorDialogState = ErrorDialogState(
                                        dialogEnabled = true,
                                        alreadyExistingUser = false
                                    )
                                } else {
                                    if (passengerInfoList[7] != "true" && passengerInfoList[7] != "false") {
                                        // incorrect
                                        errorDialogState = ErrorDialogState(
                                            dialogEnabled = true,
                                            alreadyExistingUser = false
                                        )
                                    }
                                    val scannedPassenger =
                                        getPassengerFromQrResult(passengerInfoList)
                                    if (scannedPassenger == currPassenger) {
                                        // matched
                                        Log.d("QRResult", "Result -> Passenger Verified!")
                                        matchedDialogState = true
                                    } else {
                                        // incorrect
                                        if (scannedPassenger.verified) {
                                            // seat already verified
                                            Log.d("QRResult", "Result -> Already Existing")
                                            errorDialogState = ErrorDialogState(
                                                dialogEnabled = true,
                                                alreadyExistingUser = true
                                            )
                                        } else {
                                            // incorrect
                                            errorDialogState = ErrorDialogState(
                                                dialogEnabled = true,
                                                alreadyExistingUser = false
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                DetailsTopBar(text = "Scan the QR code") {
                    navController.popBackStack()
                }

                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { compoundBarcodeView },
                )
            }else{
                Column(modifier = Modifier.fillMaxSize()) {
                    DetailsTopBar(text = "Scan the QR code") {
                        navController.popBackStack()
                    }
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ){
                        Text(
                            modifier = Modifier.padding(12.dp),
                            text = "Camera access is needed to scan the QR code!\n\nPlease go to settings and grant the Camera permission to continue.",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = darkGray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MatchedDialogBox(
    modifier: Modifier = Modifier,
    progressState: Boolean,
    passengerInfo: PassengerInfo,
    onCancelClicked: () -> Unit,
    onAcceptClicked: () -> Unit
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
                    text = "QR code matched!",
                    fontSize = 22.sp,
                    color = darkGray,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "First Name : ${passengerInfo.firstName}",
                    fontSize = 16.sp,
                    color = darkGray,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Last Name : ${passengerInfo.lastName}",
                    fontSize = 16.sp,
                    color = darkGray,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Pass. Id : ${passengerInfo.passengerId}",
                    fontSize = 16.sp,
                    color = darkGray,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "PNR : ${passengerInfo.PNR}",
                    fontSize = 16.sp,
                    color = darkGray,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Age : ${passengerInfo.age}",
                    fontSize = 16.sp,
                    color = darkGray,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Boarding Station : ${passengerInfo.from}",
                    fontSize = 16.sp,
                    color = darkGray,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Destination Station : ${passengerInfo.to}",
                    fontSize = 16.sp,
                    color = darkGray,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Coach : ${passengerInfo.coach}",
                    fontSize = 16.sp,
                    color = darkGray,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Seat Number : ${passengerInfo.seatNumber}",
                    fontSize = 16.sp,
                    color = darkGray,
                    fontWeight = FontWeight.SemiBold
                )
                if (progressState) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = lightRed
                        )
                    }
                }
            }
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
                    colors = ButtonDefaults.buttonColors(backgroundColor = lightRed),
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
                    onClick = { onAcceptClicked() },
                    colors = ButtonDefaults.buttonColors(backgroundColor = darkGreen),
                    shape = RoundedCornerShape(20)
                ) {
                    Text(
                        text = "Accept",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = darkGray
                    )
                }
            }
        },
        backgroundColor = verifiedBackground
    )
}

@Composable
fun ErrorDialogBox(
    modifier: Modifier = Modifier,
    alreadyExistingUser: Boolean,
    onCancelClicked: () -> Unit,
    onRetryClicked: () -> Unit
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = { },
        title = {
            Text(
                modifier = Modifier.padding(12.dp),
                text = if (alreadyExistingUser) "This scanned QR code is already verified!"
                else "QR code not matched!",
                fontSize = 20.sp,
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
                    colors = ButtonDefaults.buttonColors(backgroundColor = lightRed),
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
                    onClick = { onRetryClicked() },
                    colors = ButtonDefaults.buttonColors(backgroundColor = darkGreen),
                    shape = RoundedCornerShape(20)
                ) {
                    Text(
                        text = "Scan Again",
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

private fun getPassengerFromQrResult(passengersInfo: List<String>): PassengerInfo {
    val firstName = passengersInfo[0]
    val lastName = passengersInfo[1]
    val passengerId = passengersInfo[2]
    val PNR = passengersInfo[3]
    val age = passengersInfo[4]
    val from = passengersInfo[5]
    val to = passengersInfo[6]
    val verified = passengersInfo[7] == "true"
    val coach = passengersInfo[8]
    val seatNumber = passengersInfo[9]
    return PassengerInfo(
        firstName = firstName,
        lastName = lastName,
        passengerId = passengerId,
        PNR = PNR,
        age = age,
        from = from,
        to = to,
        verified = verified,
        coach = coach,
        seatNumber = seatNumber
    )
}