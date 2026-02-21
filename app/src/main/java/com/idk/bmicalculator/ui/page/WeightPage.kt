package com.idk.bmicalculator.ui.page

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
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
import com.idk.bmicalculator.WeightUnit
import com.idk.bmicalculator.ui.PrevNext
import com.idk.bmicalculator.ui.UnitDropdownSelect
import com.idk.bmicalculator.ui.components.HorizontalRuler
import kotlin.math.roundToInt

private const val KG_MIN = 30.0
private const val KG_MAX = 200.0
private const val LB_MIN = 66.0
private const val LB_MAX = 440.0

@Composable
fun WeightScreen(
    onPrev: () -> Unit,
    onNext: (selected: Int) -> Unit
) {
    var weightValue by remember { mutableDoubleStateOf(70.0) }
    var inputText by remember { mutableStateOf("70") }
    var unit by remember { mutableStateOf(WeightUnit.Kilogram) }

    val (sliderMin, sliderMax) = when (unit) {
        WeightUnit.Pound -> LB_MIN to LB_MAX
        else -> KG_MIN to KG_MAX
    }

    val weightKg = when (unit) {
        WeightUnit.Pound -> (weightValue / 2.20462).coerceIn(KG_MIN, KG_MAX)
        else -> weightValue.coerceIn(KG_MIN, KG_MAX)
    }

    val tickSpacing = when (unit) {
        WeightUnit.Pound -> 10.0
        else -> 5.0
    }

    val majorTickFrequency = 2

    fun clampValue(v: Double) = v.coerceIn(sliderMin, sliderMax)
    fun formatTextFieldValue(v: Double) = v.roundToInt().toString()
    val rulerFormatter: (Double) -> String = { v -> v.roundToInt().toString() }

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
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
        ) {
            Text(
                "Select Weight",
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
                modifier = Modifier.width(180.dp)
                    .border(0.dp, Color.Transparent, CircleShape)
                    .onFocusEvent {
                        if (!it.isFocused) {
                            inputText.toDoubleOrNull()?.let { v -> weightValue = clampValue(v) }
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
                    WeightUnit.Kilogram to "Kilogram",
                    WeightUnit.Pound to "Pound"
                ),
                onOptionSelected = { newUnit ->
                    if (unit == newUnit) return@UnitDropdownSelect

                    val kg = when (unit) {
                        WeightUnit.Pound -> weightValue / 2.20462
                        else -> weightValue
                    }

                    val newValue = when (newUnit) {
                        WeightUnit.Pound -> (kg * 2.20462).coerceIn(LB_MIN, LB_MAX)
                        else -> kg.coerceIn(KG_MIN, KG_MAX)
                    }

                    unit = newUnit
                    weightValue = newValue
                    inputText = newValue.roundToInt().toString()
                }
            )
            Spacer(modifier = Modifier.height(32.dp))
            HorizontalRuler(
                value = weightValue,
                onValueChange = {
                    weightValue = clampValue(it)
                    inputText = formatTextFieldValue(weightValue)
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
            onNext = { onNext(weightKg.roundToInt()) },
            canNext = weightKg in KG_MIN..KG_MAX
        )
    }
}
