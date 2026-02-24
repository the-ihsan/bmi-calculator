package com.rabeya.bmiapp.ui.page

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rabeya.bmiapp.HeightUnit
import kotlin.math.roundToInt
import com.rabeya.bmiapp.ui.PrevNext
import com.rabeya.bmiapp.ui.UnitDropdownSelect
import com.rabeya.bmiapp.ui.components.VerticalRuler

private const val FEET_MIN = 1.0
private const val FEET_MAX = 8.0
private const val CM_MIN = 100.0
private const val CM_MAX = 250.0

@Composable
fun HeightScreen(
    onPrev: () -> Unit,
    onNext: (selected: Int) -> Unit
) {
    var heightValue by remember { mutableDoubleStateOf(5.5) }
    var inputText by remember { mutableStateOf("5.5") }
    var unit by remember { mutableStateOf(HeightUnit.Feet) }

    val (sliderMin, sliderMax) = when (unit) {
        HeightUnit.Centimeter -> CM_MIN to CM_MAX
        else -> FEET_MIN to FEET_MAX
    }

    val heightCm = when (unit) {
        HeightUnit.Centimeter -> heightValue.coerceIn(CM_MIN, CM_MAX)
        else -> (heightValue * 30.48).coerceIn(CM_MIN, CM_MAX)
    }

    val tickSpacing = when(unit) {
        HeightUnit.Centimeter -> 10.0
        else -> 4.0 / 12.0
    }

    val majorTickFrequency = when(unit) {
        HeightUnit.Centimeter -> 2
        else -> 3
    }

    fun clampValue(v: Double) = v.coerceIn(sliderMin, sliderMax)
    fun formatTextFieldValue(v: Double) = if (unit == HeightUnit.Centimeter) v.roundToInt().toString() else "%.1f".format(v)
    val rulerFormatter: (Double) -> String = { v ->
        if (unit == HeightUnit.Centimeter) {
            v.roundToInt().toString()
        } else {
            val totalInches = (v * 12).roundToInt()
            val feet = totalInches / 12
            val inches = totalInches % 12
            "$feet' $inches\""
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 32.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Select Height",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = inputText,
                    onValueChange = { s ->
                        val filtered = buildString {
                            var seenDot = false
                            for (c in s) {
                                when {
                                    c.isDigit() -> append(c)
                                    c == '.' && !seenDot -> { append(c); seenDot = true }
                                }
                            }
                        }
                        inputText = filtered
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.width(140.dp)
                        .border(0.dp, Color.Transparent, CircleShape)
                        .onFocusEvent{ it ->
                            Log.d("X", inputText)
                            if (!it.isFocused) {
                                Log.d("X", "Not Focused")
                                inputText.toDoubleOrNull()?.let { heightValue = clampValue(it) }
                            }
                        },
                    textStyle = TextStyle(
                        fontSize = 50.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                UnitDropdownSelect(
                    selected = unit,
                    options = listOf(
                        HeightUnit.Feet to "Feet",
                        HeightUnit.Centimeter to "Centimeter"
                    ),
                    onOptionSelected = { newUnit ->
                        // Prevent unnecessary work if selecting the same unit
                        if (unit == newUnit) return@UnitDropdownSelect

                        // 1. Convert current value to a base unit (cm)
                        val cm = when (unit) {
                            HeightUnit.Centimeter -> heightValue
                            else -> heightValue * 30.48
                        }

                        // 2. Calculate the new value and clamp it against the *new* bounds explicitly
                        val newValue = when (newUnit) {
                            HeightUnit.Centimeter -> cm.coerceIn(CM_MIN, CM_MAX)
                            else -> (cm / 30.48).coerceIn(FEET_MIN, FEET_MAX)
                        }

                        // 3. Update all states
                        unit = newUnit
                        heightValue = newValue

                        // Use inline formatting so it doesn't rely on the old 'unit' state
                        inputText = if (newUnit == HeightUnit.Centimeter) {
                            newValue.roundToInt().toString()
                        } else {
                            "%.1f".format(newValue)
                        }
                    }
                )
            }
            VerticalRuler(
                value = heightValue,
                onValueChange = {
                    heightValue = clampValue(it)
                    inputText = formatTextFieldValue(heightValue)
                },
                tickSpacing = tickSpacing,
                majorTickFrequency = majorTickFrequency,
                labelFormatter = rulerFormatter,
                min = sliderMin,
                max = sliderMax
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        PrevNext(
            onPrev = onPrev,
            onNext = { onNext(heightCm.roundToInt()) },
            canNext = heightCm in CM_MIN..CM_MAX
        )
    }
}
