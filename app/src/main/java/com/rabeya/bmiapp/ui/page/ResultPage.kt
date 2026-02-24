package com.rabeya.bmiapp.ui.page

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.pow

private data class BmiCategory(
    val rating: String,
    val color: Color,
    val description: String,
)

@Composable
fun ResultScreen(
    heightCm: Int,
    weightKg: Int,
    onPrev: () -> Unit,
    onRestart: () -> Unit,
) {
    val bmi = remember(heightCm, weightKg) {
        weightKg / (heightCm / 100.0).pow(2)
    }

    val category = remember(bmi) {
        when {
            bmi < 18.5 -> BmiCategory(
                "Underweight",
                Color(0xFF42A5F5),
                "You are underweight. Consider consulting a healthcare provider about a balanced diet."
            )
            bmi < 25.0 -> BmiCategory(
                "Normal",
                Color(0xFF66BB6A),
                "You have a healthy weight. Keep maintaining your balanced lifestyle!"
            )
            bmi < 30.0 -> BmiCategory(
                "Overweight",
                Color(0xFFFFA726),
                "You are slightly overweight. A balanced diet and regular exercise can help."
            )
            else -> BmiCategory(
                "Obese",
                Color(0xFFEF5350),
                "You are in the obese range. Please consider consulting a healthcare provider."
            )
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                "Your BMI",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 20.sp,
            )

            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(category.color.copy(alpha = 0.1f))
                    .border(4.dp, category.color, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "%.1f".format(bmi),
                        fontSize = 52.sp,
                        fontWeight = FontWeight.Bold,
                        color = category.color
                    )
                    Text(
                        category.rating,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = category.color,
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                category.description,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                lineHeight = 24.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth().padding(32.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally)
        ) {
            Button(
                onClick = onPrev,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceDim
                ),
                modifier = Modifier.height(56.dp).width(120.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("Back", color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp)
            }
            Button(
                onClick = onRestart,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.height(56.dp).width(150.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("Start Over", color = MaterialTheme.colorScheme.surface, fontSize = 16.sp)
            }
        }
    }
}
