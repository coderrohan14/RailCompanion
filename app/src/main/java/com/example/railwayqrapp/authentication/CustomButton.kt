package com.example.railwayqrapp.authentication

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomButton(
    modifier: Modifier = Modifier,
    text: String,
    disabledBackgroundColor: Color = Color.Transparent,
    backgroundColor: Color,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    OutlinedButton(
        modifier = modifier,
        onClick = { onClick() },
        colors = ButtonDefaults.buttonColors(
            backgroundColor = backgroundColor,
            disabledBackgroundColor = disabledBackgroundColor
        ),
        shape = CircleShape,
        contentPadding = PaddingValues(16.dp),
        border = if(enabled) BorderStroke(0.dp, Color.Transparent) else BorderStroke(1.5.dp, Color.DarkGray),
        enabled = enabled,
        elevation = ButtonDefaults.elevation(4.dp)
    ) {
        Text(text = text, color = Color.DarkGray, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
    }
}