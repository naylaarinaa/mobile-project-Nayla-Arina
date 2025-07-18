package com.example.suitmediaapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.suitmediaapp.ui.components.Header
import com.example.suitmediaapp.ui.components.MyButton
import com.example.suitmediaapp.ui.theme.SuitmediaAppTheme

class SecondPage : ComponentActivity() {
    private lateinit var chooseUserLauncher: ActivityResultLauncher<Intent>
    private var selectedUserName by mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val userName = intent.getStringExtra("name") ?: "John Doe"

        // Register activity result launcher
        chooseUserLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.getStringExtra("selected_user_name")?.let { name ->
                    selectedUserName = name
                }
            }
        }

        setContent {
            val context = LocalContext.current

            SuitmediaAppTheme {
                Scaffold { innerPadding ->
                    SecondPageContent(
                        userName = userName,
                        selectedUserName = selectedUserName,
                        onBack = { (context as ComponentActivity).finish() },
                        onChooseUser = {
                            val intent = Intent(context, ThirdPage::class.java)
                            chooseUserLauncher.launch(intent)
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun SecondPageContent(
    userName: String,
    selectedUserName: String,
    onBack: () -> Unit,
    onChooseUser: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Header(
                title = "Second Screen",
                onBack = onBack
            )

            HorizontalDivider(
                color = Color(0xFFE2E3E4),
                thickness = 1.dp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Static "Welcome" text
            Text(
                text = "Welcome",
                fontSize = 12.sp,
                color = Color(0xFF04021D),
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Dynamic user name from first screen
            Text(
                text = userName,
                fontSize = 18.sp,
                color = Color(0xFF04021D),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Selected User Name label (dynamic)
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = selectedUserName.ifEmpty { "Selected User Name" },
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF04021D)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Choose a User button
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                MyButton(
                    text = "Choose a User",
                    fontSize = 14.sp,
                    onClick = onChooseUser
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}