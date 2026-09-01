package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Poll
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.OfferwallPartner
import com.example.data.model.ScratchCard
import com.example.data.model.TaskCategory
import com.example.data.model.TaskItem
import com.example.ui.components.InteractiveScratchCard
import com.example.ui.theme.CoralRed
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GemCyan
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldOrange
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SkyBlue

@Composable
fun TasksScreen(
    tasks: List<TaskItem>,
    scratchCards: List<ScratchCard>,
    offerwalls: List<OfferwallPartner> = emptyList(),
    onCompleteTask: (TaskItem) -> Unit,
    onScratchCard: (ScratchCard) -> Unit,
    onOpenOfferwallDetail: (OfferwallPartner) -> Unit = {},
    onOpenMegaOffer: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedSection by remember { mutableIntStateOf(0) } // 0: Offerwalls & Mega, 1: Daily Tasks, 2: Scratch Cards
    var activeSurveyTask by remember { mutableStateOf<TaskItem?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(4.dp))

        // Custom Tab Switcher
        TabRow(
            selectedTabIndex = selectedSection,
            containerColor = Color(0xFF1B0E35),
            contentColor = GoldAccent,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedSection]),
                    color = GoldAccent
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
        ) {
            Tab(
                selected = selectedSection == 0,
                onClick = { selectedSection = 0 },
                text = { Text("🌐 OfferWalls", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                selectedContentColor = GoldAccent,
                unselectedContentColor = Color(0xFF94A3B8)
            )
            Tab(
                selected = selectedSection == 1,
                onClick = { selectedSection = 1 },
                text = { Text("📋 Tasks", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                selectedContentColor = GoldAccent,
                unselectedContentColor = Color(0xFF94A3B8)
            )
            Tab(
                selected = selectedSection == 2,
                onClick = { selectedSection = 2 },
                text = { Text("🎁 Scratch", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                selectedContentColor = GoldAccent,
                unselectedContentColor = Color(0xFF94A3B8)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // SECTION 0: OFFERWALLS & MEGA OFFERS
        if (selectedSection == 0) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Mega & Super Offers Banners
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF111E38)),
                        border = BorderStroke(1.dp, GemCyan),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenMegaOffer(false) }
                            .testTag("tasks_mega_offer_banner")
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = GemCyan.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "💎 25 GEMS CHALLENGE",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = GemCyan,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Mega Offer: Collect 25 Gems",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = "Answer trivia + watch sponsor video = 2,500 Coins Bonus!",
                                    fontSize = 12.sp,
                                    color = Color(0xFFCBD5E1)
                                )
                            }

                            Button(
                                onClick = { onOpenMegaOffer(false) },
                                colors = ButtonDefaults.buttonColors(containerColor = GemCyan),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Play 💎", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }

                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2E0F23)),
                        border = BorderStroke(1.dp, Color(0xFFFF5722)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenMegaOffer(true) }
                            .testTag("tasks_super_offer_banner")
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFFF5722).copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "🔥 SUPER GRAND POOL",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFFFFAB91),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Super Offer: 5,000 Coins Vault",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = "Exclusive premium trivia challenge with 5,000 coin milestone!",
                                    fontSize = 12.sp,
                                    color = Color(0xFFCBD5E1)
                                )
                            }

                            Button(
                                onClick = { onOpenMegaOffer(true) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Join 🔥", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "🌐 Integrated Partner OfferWalls",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = Color.White,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }

                items(offerwalls) { partner ->
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF160D2C)),
                        border = BorderStroke(1.dp, Color(0xFF2E1A56)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenOfferwallDetail(partner) }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = partner.name,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 16.sp,
                                            color = Color.White
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = NeonPurple.copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = partner.badge,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = NeonPurple,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = partner.payoutMultiplier,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldGreen,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }

                                Button(
                                    onClick = { onOpenOfferwallDetail(partner) },
                                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Open Wall", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color.Black)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = partner.description,
                                fontSize = 12.sp,
                                color = Color(0xFFCBD5E1)
                            )
                        }
                    }
                }
            }
        }

        // SECTION 1: STANDARD TASKS
        if (selectedSection == 1) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(tasks) { task ->
                    TaskCard(
                        task = task,
                        onAction = {
                            if (task.category == TaskCategory.SURVEY) {
                                activeSurveyTask = task
                            } else {
                                onCompleteTask(task)
                            }
                        }
                    )
                }
            }
        }

        // SECTION 2: SCRATCH CARDS
        if (selectedSection == 2) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(scratchCards) { card ->
                    InteractiveScratchCard(
                        card = card,
                        onScratched = { onScratchCard(card) }
                    )
                }
            }
        }
    }

    // Survey Task Dialog
    activeSurveyTask?.let { survey ->
        SurveyDialog(
            task = survey,
            onComplete = {
                onCompleteTask(survey)
                activeSurveyTask = null
            },
            onDismiss = { activeSurveyTask = null }
        )
    }
}

@Composable
fun TaskCard(
    task: TaskItem,
    onAction: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF160D2C)),
        border = BorderStroke(1.dp, if (task.isCompleted) EmeraldGreen.copy(alpha = 0.4f) else Color(0xFF2E1754)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("task_item_${task.id}")
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (task.isCompleted) Color(0xFF0F4A2E) else Color(0xFF2E1754)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.Assignment,
                        contentDescription = null,
                        tint = if (task.isCompleted) EmeraldGreen else GoldAccent,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = task.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                    Text(
                        text = task.description,
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                    Text(
                        text = "+${task.rewardCoins} Coins",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = GoldYellow,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            Button(
                onClick = onAction,
                enabled = !task.isCompleted,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldAccent,
                    disabledContainerColor = Color(0xFF251342)
                ),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (task.isCompleted) "Claimed ✓" else "Start",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = if (task.isCompleted) Color(0xFF94A3B8) else Color.Black
                )
            }
        }
    }
}

@Composable
fun SurveyDialog(
    task: TaskItem,
    onComplete: () -> Unit,
    onDismiss: () -> Unit
) {
    var answerIndex by remember { mutableStateOf<Int?>(null) }
    val options = listOf("Very Satisfied", "Satisfied", "Neutral", "Needs More Rewards")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF140A28)),
            border = BorderStroke(1.dp, GoldAccent),
            modifier = Modifier.padding(8.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(text = "Quick 1-Minute Survey", fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color.White)
                Text(text = "How is your reward earning experience on Earn Hub today?", fontSize = 13.sp, color = Color(0xFFCBD5E1), modifier = Modifier.padding(vertical = 8.dp))

                options.forEachIndexed { idx, opt ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (answerIndex == idx) Color(0xFF381C6E) else Color(0xFF1E0E3B),
                        border = BorderStroke(1.dp, if (answerIndex == idx) GoldAccent else Color(0xFF2E1754)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { answerIndex = idx }
                    ) {
                        Text(text = opt, color = Color.White, fontSize = 13.sp, modifier = Modifier.padding(12.dp))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onComplete,
                    enabled = answerIndex != null,
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Submit & Earn +${task.rewardCoins} Coins", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
