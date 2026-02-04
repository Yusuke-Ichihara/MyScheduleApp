package com.example.myscheduleapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myscheduleapp.data.Schedule

@Composable
fun ScheduleCardItem(
    schedule: Schedule,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val colorPalette = listOf(
        Color(0xFFE3F2FD), // Light Blue
        Color(0xFFF1F8E9), // Light Green
        Color(0xFFFFFDE7), // Light Yellow
        Color(0xFFFFF3E0), // Light Orange
        Color(0xFFFCE4EC), // Light Pink
        Color(0xFFF3E5F5)  // Light Purple
    )

    val colorIndex = (schedule.id % colorPalette.size).toInt()
    val cardBackground = colorPalette[colorIndex]

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.elevatedCardColors(containerColor = cardBackground),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp)
    ){
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = schedule.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "削除",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}