package com.rabeya.bmiapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun PrevNext(
    onPrev: (() -> Unit)? = null,
    onNext: () -> Unit,
    canNext: Boolean = true
){
    Row(
        modifier = Modifier.fillMaxWidth().padding(32.dp),
        horizontalArrangement = Arrangement.spacedBy(
            20.dp,
            alignment = Alignment.CenterHorizontally
        ),

        ) {
        if (onPrev != null) {
            Button(
                onClick = onPrev,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceDim),
                modifier = Modifier.height(56.dp).width(120.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("Prev", color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp)
            }
        }

        val btnColor = if (canNext) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.surfaceDim
        val textColor = if (canNext) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurface

        Button(
            onClick = {
                if (canNext) {
                    onNext()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = btnColor),
            modifier = Modifier.height(56.dp).width(120.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text("Next", color = textColor, fontSize = 16.sp)
        }
    }
}
