package com.example.suitmediaapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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

        chooseUserLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.getStringExtra("selected_user_name")?.let { selectedUserName = it }
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
                        onChooseUser = { chooseUserLauncher.launch(Intent(context, ThirdPage::class.java)) },
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
    Box(modifier = modifier.fillMaxSize().background(Color.White)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Header(title = "Second Screen", onBack = onBack)
            HorizontalDivider(color = Color(0xFFE2E3E4), thickness = 1.dp, modifier = Modifier.fillMaxWidth())
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Text(
                text = "Welcome",
                fontSize = 12.sp,
                color = Color(0xFF04021D),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = userName,
                fontSize = 18.sp,
                color = Color(0xFF04021D),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    text = selectedUserName.ifEmpty { "Selected User Name" },
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF04021D)
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                MyButton(text = "Choose a User", fontSize = 14.sp, onClick = onChooseUser)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}