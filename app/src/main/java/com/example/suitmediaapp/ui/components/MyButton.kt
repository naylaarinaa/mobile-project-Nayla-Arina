package com.example.suitmediaapp.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.*
import com.example.suitmediaapp.R
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight

@Composable
fun MyButton(
    text: String,
    modifier: Modifier = Modifier
        .width(310.dp)
        .height(41.dp),
    fontSize: TextUnit = 14.sp,
    onClick: () -> Unit
) {
    val poppins = FontFamily(
        Font(R.font.poppins_regular, FontWeight.Normal),
        Font(R.font.poppins_medium, FontWeight.Medium)
    )

    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2B637B))
    ) {
        Text(
            text = text,
            fontFamily = poppins,
            fontWeight = FontWeight.Medium,
            fontSize = fontSize,
            color = Color.White
        )
    }
}
