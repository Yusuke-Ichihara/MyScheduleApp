package com.example.myscheduleapp.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.border
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myscheduleapp.data.Task
import java.util.Calendar

@Composable
fun TaskTimelineItem(
    task: Task,
    isFirst: Boolean,
    isLast: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val timelineColor = MaterialTheme.colorScheme.primary
    val cardColors = getCardColorsByTime(task.startTime)
    val isNow = isCurrentTask(task.startTime)
    // 時間を分に変換して差分を出す
    val startMin = timeToMinutes(task.startTime)
    val endMin = timeToMinutes(task.endTime).let {
        // 終了時間が未設定、もしくは開始時間より前の場合は60分として扱う
        if (it <= startMin) startMin + 60 else it
    }
    val duration = endMin - startMin

    // 縦幅：0.5dp/minute
    val scale = 0.5f
    // 縦幅：最低でも70dpは確保
    val calculatedHeight = (duration * scale).coerceAtLeast(90f).dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        //左側（線と点）
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .width(40.dp)
                .fillMaxHeight()
        ) {
            //上に向かう線
            if (!isFirst) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .weight(1f)
                        .background(timelineColor)
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
            Box(
                modifier = Modifier
                    .size(if (isNow) 16.dp else 12.dp)
                    .background(if (isNow) timelineColor else timelineColor, CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
            )
            //下に向かう線
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .weight(1f)
                        .background(timelineColor)
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
        //右側
        ElevatedCard(
            onClick = onEdit,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp, bottom = 16.dp)
                .height(calculatedHeight)
                .then(
                    if (isNow) {
                        Modifier.border(
                            width = 3.dp,
                            color = timelineColor,
                            shape = CardDefaults.shape
                        )
                    } else {
                        Modifier
                    }
                ),
            colors = cardColors,
            elevation = CardDefaults.elevatedCardElevation(
                defaultElevation = if (isNow) 12.dp else 0.dp
            )
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
//                    デバッグ用
//                    Text(
//                        text = "DEBUG: isNow=$isNow / Time=${Calendar.getInstance().get(Calendar.HOUR_OF_DAY)}:${Calendar.getInstance().get(Calendar.MINUTE)}",
//                        color = Color.Red,
//                        fontSize = 10.sp
//                    )

                    //時刻表示（少し控えめ）
                    Text(
                        text = task.startTime + " ~ " + task.endTime,
                        style = MaterialTheme.typography.headlineSmall,
                        color = cardColors.contentColor.copy(alpha = 1f),
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    //タスク名（大きくハッキリ）
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium,
                        maxLines = if (duration < 45) 1 else 3
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.DeleteOutline,
                        contentDescription = "削除",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
fun getCardColorsByTime(startTime: String): CardColors {
    //"HH:mm"の最初の2文字をIntに変換
    val hour = startTime.take(2).toIntOrNull() ?:0

    return when (hour) {
        in 6..10 -> {
            CardDefaults.elevatedCardColors(
                containerColor = Color(0xFFE8F5E9), // 優しい緑
                contentColor = Color(0xFF2E7D32)    // 濃い緑（文字用）
            )
        }
        in 11..17 -> {
            CardDefaults.elevatedCardColors(
                containerColor = Color(0xFFFFF3E0), // 優しいオレンジ
                contentColor = Color(0xFFE65100)    // 濃いオレンジ（文字用）
            )
        }
        in 18..22 -> {
            CardDefaults.elevatedCardColors(
                containerColor = Color(0xFFE3F2FD), // 優しい青
                contentColor = Color(0xFF1565C0)    // 濃い青（文字用）
            )
        }
        else -> {
            CardDefaults.elevatedCardColors(
                containerColor = Color(0xFFDACFFF), // 薄い紫
                contentColor = Color(0xFF1526C0) // 濃い紫（文字用）
            )
        }
    }
}

fun isCurrentTask(startTime: String): Boolean {
    val now = Calendar.getInstance()
    val currentHour = now.get(Calendar.HOUR_OF_DAY)
    val currentMinute = now.get(Calendar.MINUTE)

    val parts = startTime.split(":")
    if (parts.size != 2) return false

    val startHour = parts[0].toIntOrNull() ?: return false
    val startMin = parts[1].toIntOrNull() ?: return false

    val nowInMinutes = currentHour * 60 + currentMinute
    val startInMinutes = startHour * 60 + startMin
    val endInMinutes = startInMinutes + 60

    val result = nowInMinutes in startInMinutes..<endInMinutes
    Log.d("DEBUG_TIME", "Task: $startTime, Now: $currentHour:$currentMinute, Result: $result")

    return result
}

// "hh:mm" 形式の文字列を（分）に変換する関数
fun timeToMinutes(timeStr: String): Int {
    if (timeStr.isBlank()) return 0
    return try {
        val parts = timeStr.split(":")
        val hours = parts[0].toInt()
        val minutes = parts[1].toInt()
        hours * 60 + minutes
    } catch (e: Exception) {
        0
    }
}