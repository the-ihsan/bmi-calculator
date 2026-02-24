package com.rabeya.bmiapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.rabeya.bmiapp.ui.page.GenderScreen
import com.rabeya.bmiapp.ui.page.HeightScreen
import com.rabeya.bmiapp.ui.page.ResultScreen
import com.rabeya.bmiapp.ui.page.WeightScreen
import com.rabeya.bmiapp.ui.theme.BMICalculatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        setContent {
            BMICalculatorTheme {
                BMIApp()
            }
        }
    }
}

@Composable
fun TopBar(onRestart: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.AutoGraph, "Stats", tint = MaterialTheme.colorScheme.onSurface)
        IconButton(onClick = onRestart) {
            Icon(Icons.Filled.Autorenew, "Restart", tint = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BMIApp() {
    val focusManager = LocalFocusManager.current
    val isKeyboardVisible = WindowInsets.isImeVisible

    LaunchedEffect(isKeyboardVisible) {
        if (!isKeyboardVisible) {
            focusManager.clearFocus()
        }
    }

    var screen by remember { mutableStateOf(Screen.Gender) }
    var userData by remember { mutableStateOf(UserData()) }

    val onRestart: () -> Unit = {
        screen = Screen.Gender
        userData = UserData()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        topBar = { TopBar(onRestart = onRestart) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (screen) {
                Screen.Gender -> GenderScreen(
                    onNext = { selectedGender ->
                        userData = userData.copy(gender = selectedGender)
                        screen = Screen.Height
                    }
                )

                Screen.Height -> HeightScreen(
                    onPrev = { screen = Screen.Gender },
                    onNext = { selectedHeight ->
                        userData = userData.copy(height = selectedHeight)
                        screen = Screen.Weight
                    }
                )

                Screen.Weight -> WeightScreen(
                    onPrev = { screen = Screen.Height },
                    onNext = { selectedWeight ->
                        userData = userData.copy(weight = selectedWeight)
                        screen = Screen.Result
                    }
                )

                Screen.Result -> ResultScreen(
                    heightCm = userData.height ?: 170,
                    weightKg = userData.weight ?: 70,
                    onPrev = { screen = Screen.Weight },
                    onRestart = onRestart
                )
            }
        }
    }
}
