package com.example.railwayqrapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.railwayqrapp.R
import com.example.railwayqrapp.ui.theme.lightRed
import com.example.railwayqrapp.viewModels.HomeViewModel

@Composable
fun DetailsScreen(
    navController: NavController,
    homeViewModel: HomeViewModel
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        DetailsTopBar(text = "Details") {
            navController.popBackStack()
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