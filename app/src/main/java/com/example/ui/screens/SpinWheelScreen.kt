package com.example.ui.screens

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldOrange
import com.example.ui.theme.GoldYellow
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SpinWheelScreen(
    dailySpinsLeft: Int,
    isSpinning: Boolean,
    spinAngle: Float,
    onSpinClick: () -> Unit,
    onRefillSpinsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedAngle by animateFloatAsState(
        targetValue = spinAngle,
        animationSpec = tween(durationMillis = 3500, easing = FastOutSlowInEasing),
        label = "wheel_spin_anim"
    )

    val prizes = listOf("25", "50", "10", "100", "20", "250", "500", "1000")
    val sliceColors = listOf(
        Color(0xFF7C3AED), // Violet
        Color(0xFFEA580C), // Orange
        Color(0xFF0D9488), // Teal
        Color(0xFFE11D48), // Rose
        Color(0xFF2563EB), // Blue
        Color(0xFFCA8A04), // Gold
        Color(0xFF059669), // Emerald
        Color(0xFFD946EF)  // Fuchsia
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Header Info
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF27134A)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(GoldYellow, GoldOrange))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Casino,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Wheel of Fortune",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = "Win up to 1,000 Coins per spin!",
                                fontSize = 12.sp,
                                color = Color(0xFFC4B5FD)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF431F7A)
                    ) {
                        Text(
                            text = "$dailySpinsLeft Spins",
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            color = GoldYellow,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Animated Lucky Wheel Container
        item {
            Box(
                modifier = Modifier
                    .size(310.dp)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                // Outer Glowing Border Ring
                Box(
                    modifier = Modifier
                        .size(300.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.sweepGradient(
                                listOf(GoldYellow, GoldOrange, Color(0xFF7C3AED), GoldYellow)
                            )
                        )
                        .padding(8.dp)
                ) {
                    // Canvas Wheel with Rotations
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .rotate(animatedAngle)
                    ) {
                        val canvasSize = size.minDimension
                        val radius = canvasSize / 2f
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val sweepAngle = 360f / prizes.size

                        prizes.forEachIndexed { i, prize ->
                            val startAngle = i * sweepAngle
                            drawArc(
                                color = sliceColors[i % sliceColors.size],
                                startAngle = startAngle,
                                sweepAngle = sweepAngle,
                                useCenter = true,
                                topLeft = Offset(center.x - radius, center.y - radius),
                                size = Size(radius * 2, radius * 2)
                            )
                        }

                        // Draw Prize Labels on Wheel Slices
                        drawIntoCanvas { canvas ->
                            val paint = Paint().apply {
                                color = android.graphics.Color.WHITE
                                textSize = 42f
                                textAlign = Paint.Align.CENTER
                                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                                isAntiAlias = true
                            }

                            prizes.forEachIndexed { i, prize ->
                                val midAngleDeg = i * sweepAngle + sweepAngle / 2f
                                val midAngleRad = Math.toRadians(midAngleDeg.toDouble())
                                val textDist = radius * 0.65f
                                val textX = (center.x + textDist * cos(midAngleRad)).toFloat()
                                val textY = (center.y + textDist * sin(midAngleRad) + 14).toFloat()

                                canvas.nativeCanvas.save()
                                canvas.nativeCanvas.rotate(midAngleDeg + 90f, textX, textY - 14)
                                canvas.nativeCanvas.drawText(prize, textX, textY, paint)
                                canvas.nativeCanvas.restore()
                            }
                        }
                    }
                }

                // Center Gold Star Hub
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Brush.radialGradient(listOf(GoldYellow, GoldOrange, Color(0xFFB45309))))
                        .border(3.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Star",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Top Arrow Pointer Indicator (Points down at top of wheel)
                Canvas(
                    modifier = Modifier
                        .size(36.dp)
                        .align(Alignment.TopCenter)
                ) {
                    val path = androidx.compose.ui.graphics.Path().apply {
                        moveTo(size.width / 2f, size.height)
                        lineTo(0f, 0f)
                        lineTo(size.width, 0f)
                        close()
                    }
                    drawPath(path, color = Color(0xFFFFD600))
                    drawPath(
                        path,
                        color = Color.White,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f)
                    )
                }
            }
        }

        // Spin Action CTA Button
        item {
            Button(
                onClick = onSpinClick,
                enabled = !isSpinning && dailySpinsLeft > 0,
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldAccent,
                    disabledContainerColor = Color(0xFF4A3A20)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("spin_the_wheel_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Casino,
                    contentDescription = null,
                    tint = if (dailySpinsLeft > 0) Color(0xFF2C1B00) else Color(0xFF9E9E9E),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = if (isSpinning) "Spinning Lucky Wheel..." else if (dailySpinsLeft > 0) "SPIN TO WIN" else "NO SPINS LEFT",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    color = if (dailySpinsLeft > 0) Color(0xFF2C1B00) else Color(0xFF9E9E9E)
                )
            }
        }

        // Video Refill Button
        item {
            OutlinedButton(
                onClick = onRefillSpinsClick,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("refill_spins_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.OndemandVideo,
                    contentDescription = null,
                    tint = EmeraldGreen,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Watch Ad for +5 Extra Spins & +50 Coins",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldGreen
                )
            }
        }

        // Prize Probability Table
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B0E33)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Wheel Reward Table",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• Jackpot 1,000 Coins\n• Big Win 500 & 250 Coins\n• Standard 100, 50, 25, 20, 10 Coins\n• Spins reset every 24 hours automatically",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8),
                        lineHeight = 18.sp
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
