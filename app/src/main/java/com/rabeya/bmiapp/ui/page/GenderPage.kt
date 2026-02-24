package com.rabeya.bmiapp.ui.page

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Female
import androidx.compose.material.icons.outlined.Male
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rabeya.bmiapp.Gender
import com.rabeya.bmiapp.ui.PrevNext


@Composable
fun GenderScreen(
    onNext: (selected: Gender) -> Unit
){
    var selected by remember { mutableStateOf(Gender.None) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            "Select Gender",
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 20.sp
        )
        Spacer(
            modifier = Modifier.height(30.dp)
        )
        GenderBtn(
            icon = Icons.Outlined.Male,
            text = "Male",
            isSelected = selected == Gender.Male,
            onClick = {
                selected = Gender.Male
            }
        )
        Spacer(
            modifier = Modifier.height(20.dp)
        )
        GenderBtn(
            icon = Icons.Outlined.Female,
            text = "Female",
            isSelected = selected == Gender.Female,
            onClick = {
                selected = Gender.Female
            }
        )
        Spacer(
            modifier = Modifier.height(30.dp)
        )
        PrevNext(
            canNext = selected != Gender.None,
            onNext = {
                onNext(selected)
            },
            onPrev = {}
        )
    }
}


@Composable
fun GenderBtn(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    isSelected: Boolean = false
){
    val bgColor = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.surfaceDim
    val textColor = if (isSelected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurface
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape.copy(CornerSize(50.dp)))
                .background(bgColor)
                .clickable{onClick()},
        ) {
            Icon(
                icon,
                text,
                tint = textColor,
                modifier = Modifier.size(40.dp)
            )
            Spacer(
                modifier = Modifier.height(10.dp)
            )
            Text(text, color = textColor)
        }

}
