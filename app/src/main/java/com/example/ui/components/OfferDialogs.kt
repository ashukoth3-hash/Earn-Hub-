package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.OfferQuestion
import com.example.data.model.OfferwallPartner
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GemCyan
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldOrange
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SkyBlue

@Composable
fun MegaOfferDialog(
    isVisible: Boolean,
    isSuperOffer: Boolean,
    progress: Int, // 0 to 25
    question: OfferQuestion,
    onAnswerSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    if (!isVisible) return

    var selectedOption by remember(question) { mutableStateOf<Int?>(null) }
    var isSubmitted by remember(question) { mutableStateOf(false) }

    val offerTitle = if (isSuperOffer) "🔥 Super Offer (5,000 Coins Grand Pool)" else "💎 Mega Offer (2,500 Coins Grand Pool)"
    val milestoneGoal = 25
    val currentStep = progress + 1

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false)
    ) {
        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF14092A)),
            border = BorderStroke(
                1.5.dp,
                if (isSuperOffer) Color(0xFFFF5722) else GemCyan
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("mega_offer_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSuperOffer) Color(0xFFFF5722).copy(alpha = 0.2f) else GemCyan.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, if (isSuperOffer) Color(0xFFFF5722) else GemCyan)
                    ) {
                        Text(
                            text = if (isSuperOffer) "SUPER OFFER" else "MEGA 25 GEMS CHALLENGE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isSuperOffer) Color(0xFFFFAB91) else Color(0xFF67E8F9),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp).testTag("close_mega_offer_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Progress Step Badge (e.g. 5/25 Gems)
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                if (isSuperOffer) listOf(Color(0xFFFF5722), Color(0xFFFF9100))
                                else listOf(Color(0xFF00E5FF), Color(0xFF2979FF))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "💎",
                        fontSize = 28.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Collect 25 Gems Challenge",
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = Color.White
                )

                Text(
                    text = "Answer question + watch sponsor clip to collect 1 Gem & Coins!",
                    fontSize = 12.sp,
                    color = Color(0xFFCBD5E1),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp).padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Progress Bar: Steps completed (e.g. 12/25)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Gem Milestone: $progress / 25",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF67E8F9)
                    )
                    Text(
                        text = if (progress >= 24) "🎁 Grand Prize Unlocks Now!" else "${25 - progress} steps remaining",
                        fontSize = 11.sp,
                        color = GoldYellow,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                LinearProgressIndicator(
                    progress = { (progress.toFloat() / milestoneGoal).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (isSuperOffer) Color(0xFFFF5722) else GemCyan,
                    trackColor = Color(0xFF23143F)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Question Box
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF1E103B),
                    border = BorderStroke(1.dp, Color(0xFF381F66)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Stars,
                                contentDescription = "Q",
                                tint = GoldYellow,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Question #$currentStep",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldYellow
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = question.question,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White,
                            lineHeight = 20.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 4 Options
                question.options.forEachIndexed { index, optionText ->
                    val isChosen = selectedOption == index
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isChosen) Color(0xFF3A1F70) else Color(0xFF190D32),
                        border = BorderStroke(
                            1.dp,
                            if (isChosen) (if (isSuperOffer) Color(0xFFFF5722) else GemCyan) else Color(0xFF2B164E)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { selectedOption = index }
                            .testTag("option_${index}_btn")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(if (isChosen) (if (isSuperOffer) Color(0xFFFF5722) else GemCyan) else Color(0xFF2E1752)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${('A' + index)}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isChosen) Color.Black else Color.White
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Text(
                                text = optionText,
                                fontSize = 13.sp,
                                fontWeight = if (isChosen) FontWeight.Bold else FontWeight.Normal,
                                color = if (isChosen) Color.White else Color(0xFFCBD5E1)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Submit & Watch Ad Button
                Button(
                    onClick = {
                        val chosen = selectedOption ?: 0
                        onAnswerSelected(chosen)
                    },
                    enabled = selectedOption != null,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSuperOffer) Color(0xFFFF5722) else GemCyan,
                        disabledContainerColor = Color(0xFF2D164D)
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_mega_offer_btn")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PlayCircle,
                            contentDescription = "Watch Ad & Collect",
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Submit & Claim +1 💎 Gem",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OfferwallDetailDialog(
    partner: OfferwallPartner?,
    onCompleteTask: (partnerName: String, coins: Long) -> Unit,
    onDismiss: () -> Unit
) {
    if (partner == null) return

    val sampleOffers = listOf(
        Pair("🔥 Hero Clash: Reach City Hall Lv.5", 1200L),
        Pair("⚡ Crypto Wallet: Install & Verify ID", 2500L),
        Pair("📱 Quick Quiz: 10 General Knowledge Qs", 450L),
        Pair("💎 Slot Mania: Spin 50 Times", 1800L),
        Pair("🛍️ Shopping App: Register Free Account", 850L)
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {
        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF140A28)),
            border = BorderStroke(1.dp, Color(0xFF381D69)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("offerwall_detail_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = NeonPurple.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = partner.badge,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonPurple,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = partner.name,
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            color = Color.White
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Text(
                    text = partner.description,
                    fontSize = 12.sp,
                    color = Color(0xFFCBD5E1),
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Active High-Yield Tasks (${partner.availableOffers} Available):",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = GoldYellow
                )

                Spacer(modifier = Modifier.height(8.dp))

                sampleOffers.forEach { (taskTitle, reward) ->
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF1D0E38),
                        border = BorderStroke(1.dp, Color(0xFF2F1759)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = taskTitle,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = "Estimated completion: 2-3 mins",
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }

                            Button(
                                onClick = { onCompleteTask(partner.name, reward) },
                                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "+$reward",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Close Offerwall", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun GemExchangeDialog(
    gems: Long,
    onExchange: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF130A26)),
            border = BorderStroke(1.5.dp, GemCyan),
            modifier = Modifier.padding(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "💎", fontSize = 40.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Gem Exchange Vault",
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = Color.White
                )
                Text(
                    text = "Exchange rate: 1 Gem = 100 Coins\n(25 Gems = 2,500 Coins!)",
                    fontSize = 12.sp,
                    color = Color(0xFFCBD5E1),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF1E0E3B),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Your Gems Balance:", fontSize = 13.sp, color = Color.White)
                        Text(text = "$gems 💎", fontSize = 15.sp, fontWeight = FontWeight.Black, color = GemCyan)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onExchange(5) },
                        enabled = gems >= 5,
                        colors = ButtonDefaults.buttonColors(containerColor = GemCyan),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("5 💎 -> 500", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }

                    Button(
                        onClick = { onExchange(25) },
                        enabled = gems >= 25,
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("25 💎 -> 2.5K", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.Black)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Close", color = Color.White)
                }
            }
        }
    }
}
