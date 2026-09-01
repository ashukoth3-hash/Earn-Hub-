package com.example.ui.screens

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TournamentItem
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldOrange
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SkyBlue

@Composable
fun TournamentsScreen(
    tournaments: List<TournamentItem>,
    userCoins: Long,
    onJoinTournament: (TournamentItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("ALL") }
    val clipboardManager = LocalClipboardManager.current
    var copiedRoomId by remember { mutableStateOf<String?>(null) }

    val filteredList = remember(tournaments, selectedFilter) {
        when (selectedFilter) {
            "BOARD" -> tournaments.filter { it.gameType == "LUDO" || it.gameType == "SNAKES_LADDERS" }
            "ESPORTS" -> tournaments.filter { it.gameType == "FREE_FIRE" || it.gameType == "BGMI" }
            else -> tournaments
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("tournaments_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header Banner
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF4A0E4E), Color(0xFF1E0C38), Color(0xFF0D061F))
                        )
                    )
                    .testTag("tournaments_hero_card")
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFFF5722).copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, Color(0xFFFF5722))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Whatshot,
                                    contentDescription = "Live",
                                    tint = Color(0xFFFF7043),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "ESPORTS & BOARD HUB",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFFFFAB91)
                                )
                            }
                        }

                        // Prize Pool Highlights
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = "Trophy",
                                tint = GoldAccent,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "₹50,000+ Daily Pool",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldYellow
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Battle Arenas & Custom Rooms",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        ),
                        color = Color.White
                    )

                    Text(
                        text = "Play Ludo, Saamp Seedhi, or get Custom Room IDs for Free Fire MAX & BGMI to win cash!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFE2D9F3),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        // Category Filter Chips
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == "ALL",
                    onClick = { selectedFilter = "ALL" },
                    label = { Text("⚡ All Tournaments") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NeonPurple,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.testTag("filter_all_tournaments")
                )
                FilterChip(
                    selected = selectedFilter == "BOARD",
                    onClick = { selectedFilter = "BOARD" },
                    label = { Text("🎲 Ludo & Saamp Seedhi") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SkyBlue,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.testTag("filter_board_games")
                )
                FilterChip(
                    selected = selectedFilter == "ESPORTS",
                    onClick = { selectedFilter = "ESPORTS" },
                    label = { Text("🔥 FF & BGMI") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFFF5722),
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.testTag("filter_esports_games")
                )
            }
        }

        // Tournaments List
        items(filteredList, key = { it.id }) { item ->
            TournamentCard(
                item = item,
                userCoins = userCoins,
                onJoin = { onJoinTournament(item) },
                onCopyRoom = {
                    val code = "Room ID: ${item.roomId ?: "N/A"}\nPassword: ${item.roomPassword ?: "N/A"}"
                    clipboardManager.setText(AnnotatedString(code))
                    copiedRoomId = item.id
                },
                isCopied = copiedRoomId == item.id
            )
        }

        // Bottom FAQ / Information Box
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF160D2C)),
                border = BorderStroke(1.dp, Color(0xFF2E1A59)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info",
                        tint = SkyBlue,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "How Room Matches Work?",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "1. Click 'Join Tournament' using your coins balance.\n" +
                                    "2. Room ID & Password will unlock instantly on this card.\n" +
                                    "3. Open game app, enter custom room, and compete.\n" +
                                    "4. Winner prizes & kill rewards are credited to your Earn Hub wallet automatically!",
                            fontSize = 12.sp,
                            color = Color(0xFFCBD5E1),
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TournamentCard(
    item: TournamentItem,
    userCoins: Long,
    onJoin: () -> Unit,
    onCopyRoom: () -> Unit,
    isCopied: Boolean
) {
    val isComingSoon = item.status == "COMING_SOON"
    val isLive = item.status == "LIVE_NOW"

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF170E2D)),
        border = BorderStroke(
            1.dp,
            if (item.isJoined) EmeraldGreen else if (isLive) GoldAccent.copy(alpha = 0.6f) else Color(0xFF2E1B56)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("tournament_card_${item.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Game Type Badge + Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Game Category Pill
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = when (item.gameType) {
                        "LUDO" -> Color(0xFF2E7D32).copy(alpha = 0.2f)
                        "SNAKES_LADDERS" -> Color(0xFF0288D1).copy(alpha = 0.2f)
                        "FREE_FIRE" -> Color(0xFFFF5722).copy(alpha = 0.2f)
                        else -> Color(0xFF7B1FA2).copy(alpha = 0.2f)
                    },
                    border = BorderStroke(
                        1.dp,
                        when (item.gameType) {
                            "LUDO" -> Color(0xFF4CAF50)
                            "SNAKES_LADDERS" -> Color(0xFF29B6F6)
                            "FREE_FIRE" -> Color(0xFFFF7043)
                            else -> Color(0xFFAB47BC)
                        }
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = when (item.gameType) {
                                "LUDO", "SNAKES_LADDERS" -> Icons.Default.Games
                                else -> Icons.Default.SportsEsports
                            },
                            contentDescription = item.gameType,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = item.gameType.replace("_", " "),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // Status Tag
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (item.isJoined) Color(0xFF10B981).copy(alpha = 0.2f)
                    else if (isLive) Color(0xFFFF9800).copy(alpha = 0.2f)
                    else Color(0xFF64748B).copy(alpha = 0.2f)
                ) {
                    Text(
                        text = if (item.isJoined) "✓ REGISTERED"
                        else if (isLive) "🔴 LIVE NOW"
                        else if (isComingSoon) "⏳ COMING SOON"
                        else "OPEN",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = if (item.isJoined) EmeraldGreen
                        else if (isLive) GoldYellow
                        else Color(0xFF94A3B8),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title & Subtitle
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp
                ),
                color = Color.White
            )

            Text(
                text = item.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF94A3B8),
                modifier = Modifier.padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Stats Row: Entry Fee, Prize Pool, Per Kill
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF0F0820))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Entry Fee
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Entry Fee", fontSize = 11.sp, color = Color(0xFF94A3B8))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = "Coins",
                            tint = GoldAccent,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${item.entryFeeCoins}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = GoldYellow
                        )
                    }
                }

                // Prize Pool
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Prize Pool", fontSize = 11.sp, color = Color(0xFF94A3B8))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Prize",
                            tint = EmeraldGreen,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${item.prizePoolCoins} Coins",
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            color = EmeraldGreen
                        )
                    }
                }

                // Schedule / Per Kill
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (item.perKillCoins > 0) "Per Kill" else "Format",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                    Text(
                        text = if (item.perKillCoins > 0) "+${item.perKillCoins} Coins" else item.matchType,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Slots Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Slots: ${item.filledSlots}/${item.totalSlots} joined",
                    fontSize = 11.sp,
                    color = Color(0xFFCBD5E1)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Time",
                        tint = SkyBlue,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = item.scheduleTime,
                        fontSize = 11.sp,
                        color = SkyBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { (item.filledSlots.toFloat() / item.totalSlots).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (item.filledSlots >= item.totalSlots) Color.Red else NeonPurple,
                trackColor = Color(0xFF2E1A59)
            )

            // Room ID / Password Section (Shown when joined)
            AnimatedVisibility(visible = item.isJoined) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF092C1D))
                        .border(BorderStroke(1.dp, EmeraldGreen), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "🔑 ROOM ACCESS UNLOCKED",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = EmeraldGreen
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Room ID: ${item.roomId ?: "TBD"}  |  Pass: ${item.roomPassword ?: "TBD"}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Button(
                            onClick = onCopyRoom,
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("copy_room_btn_${item.id}")
                        ) {
                            Icon(
                                imageVector = if (isCopied) Icons.Default.CheckCircle else Icons.Default.ContentCopy,
                                contentDescription = "Copy",
                                tint = Color.Black,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isCopied) "Copied!" else "Copy",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Button
            if (!item.isJoined) {
                Button(
                    onClick = onJoin,
                    enabled = !isComingSoon && userCoins >= item.entryFeeCoins,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isLive) GoldAccent else NeonPurple,
                        disabledContainerColor = Color(0xFF2B1C47)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("join_tournament_btn_${item.id}")
                ) {
                    if (isComingSoon) {
                        Text(
                            text = "Room Opening Soon...",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8)
                        )
                    } else if (userCoins < item.entryFeeCoins) {
                        Text(
                            text = "Need ${item.entryFeeCoins - userCoins} More Coins to Join",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFCA5A5)
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Join",
                                tint = if (isLive) Color.Black else Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Join Match (Entry: ${item.entryFeeCoins} Coins)",
                                fontWeight = FontWeight.Black,
                                color = if (isLive) Color.Black else Color.White,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
