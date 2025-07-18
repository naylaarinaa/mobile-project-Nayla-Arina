package com.example.suitmediaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.suitmediaapp.ui.theme.SuitmediaAppTheme
import com.example.suitmediaapp.ui.components.MyButton
import com.example.suitmediaapp.ui.components.NameEmptyAlert
import com.example.suitmediaapp.ui.components.PalindromeEmptyAlert
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.text.style.TextAlign

import android.content.Intent
import androidx.compose.ui.platform.LocalContext

class FirstPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SuitmediaAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    FirstPageContent(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun FirstPageContent(modifier: Modifier = Modifier) {
    val poppins = FontFamily(
        Font(R.font.poppins_regular, FontWeight.Normal),
        Font(R.font.poppins_medium, FontWeight.Medium)
    )

    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }
    var showNameAlert by remember { mutableStateOf(false) }
    var showPalindromeAlert by remember { mutableStateOf(false) }

    val context = LocalContext.current

    fun isPalindrome(text: String): Boolean {
        val cleanedText = text.replace("\\s+".toRegex(), "").lowercase()
        return cleanedText == cleanedText.reversed()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Show alerts for empty name or palindrome near the top of the Column
            NameEmptyAlert(
                showDialog = showNameAlert,
                onDismiss = { showNameAlert = false }
            )
            PalindromeEmptyAlert(
                showDialog = showPalindromeAlert,
                onDismiss = { showPalindromeAlert = false }
            )
            Spacer(modifier = Modifier.height(170.dp))

            Image(
                painter = painterResource(id = R.drawable.ic_photo),
                contentDescription = "Photo",
                modifier = Modifier.size(116.dp)
            )
            Spacer(modifier = Modifier.height(53.dp))

            var name by remember { mutableStateOf("") }
            Box(
                modifier = Modifier
                    .width(310.dp)
                    .height(47.dp)
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .border(1.dp, Color(0xFFE2E3E4), RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (name.isEmpty()) {
                    Text(
                        "Name",
                        fontFamily = poppins,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        color = Color(0xFF686777).copy(alpha = 0.36f)
                    )
                }
                BasicTextField(
                    value = name,
                    onValueChange = { name = it },
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontFamily = poppins,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        color = Color.Black
                    ),
                    cursorBrush = SolidColor(Color.Black),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.height(18.dp))

            var palindrome by remember { mutableStateOf("") }
            Box(
                modifier = Modifier
                    .width(310.dp)
                    .height(47.dp)
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .border(1.dp, Color(0xFFE2E3E4), RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (palindrome.isEmpty()) {
                    Text(
                        "Palindrome",
                        fontFamily = poppins,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        color = Color(0xFF686777).copy(alpha = 0.36f)
                    )
                }
                BasicTextField(
                    value = palindrome,
                    onValueChange = { palindrome = it },
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontFamily = poppins,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        color = Color.Black
                    ),
                    cursorBrush = SolidColor(Color.Black),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.height(38.dp))

            MyButton(
                text = "Check",
                fontSize = 14.sp,
                onClick = {
                    if (palindrome.isEmpty()) {
                        showPalindromeAlert = true
                    } else {
                        val result = isPalindrome(palindrome)
                        dialogMessage = if (result) "isPalindrome" else "not palindrome"
                        showDialog = true
                    }
                }
            )
            Spacer(modifier = Modifier.height(12.dp))

            MyButton(
                text = "Next",
                fontSize = 14.sp,
                onClick = {
                    if (name.isEmpty()) {
                        showNameAlert = true
                    } else {
                        val intent = Intent(context, SecondPage::class.java)
                        intent.putExtra("name", name)
                        context.startActivity(intent)
                    }
                }
            )
        }
    }

    if (showDialog) {
        Dialog(
            onDismissRequest = { showDialog = false },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(
                                Color(0xFF2B637B).copy(alpha = 0.1f),
                                RoundedCornerShape(30.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (dialogMessage == "isPalindrome") "✓" else "✗",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (dialogMessage == "isPalindrome") Color(0xFF4CAF50) else Color(0xFFE57373)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Palindrome Check",
                        fontFamily = poppins,
                        fontWeight = FontWeight.Medium,
                        fontSize = 18.sp,
                        color = Color(0xFF2B637B),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = dialogMessage,
                        fontFamily = poppins,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        color = Color(0xFF666666),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    MyButton(
                        text = "OK",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        onClick = { showDialog = false }
                    )
                }
            }
        }
    }

}
