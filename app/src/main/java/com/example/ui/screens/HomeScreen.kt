package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OfferwallPartner
import com.example.data.model.UserStats
import com.example.ui.theme.CoralRed
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GemCyan
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldOrange
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.PurpleDarkPrimary
import com.example.ui.theme.SkyBlue
import com.example.ui.viewmodel.ActiveGame
import com.example.ui.viewmodel.AppTab

@Composable
fun HomeScreen(
    userStats: UserStats?,
    offerwalls: List<OfferwallPartner> = emptyList(),
    onNavigateTab: (AppTab) -> Unit,
    onOpenGame: (ActiveGame) -> Unit,
    onClaimCheckIn: () -> Unit,
    onOpenMegaOffer: (isSuper: Boolean) -> Unit = {},
    onOpenOfferwallDetail: (OfferwallPartner) -> Unit = {},
    onOpenGemExchange: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val coins = userStats?.coins ?: 500L
    val gems = userStats?.gems ?: 5L
    val inrValue = "%.1f".format((coins / 1000.0) * 85.0)
    val megaProgress = userStats?.megaOfferProgress ?: 0
    val superProgress = userStats?.superOfferProgress ?: 0

    val infiniteTransition = rememberInfiniteTransition(label = "hero_glow")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.025f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hero_pulse"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))

            // 🌟 HERO BALANCE & GEMS CARD
            Card(
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E0E3B)),
                border = BorderStroke(1.5.dp, Color(0xFF6B21A8)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(pulseScale)
                    .clip(RoundedCornerShape(26.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF381268), Color(0xFF1B0B33), Color(0xFF0F0620))
                        )
                    )
                    .testTag("hero_balance_card")
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Header Row: User Level & Streak
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(NeonPurple),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "👑", fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = userStats?.userName ?: "Pro Earner",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = "VIP Tier 1 • Active",
                                    fontSize = 11.sp,
                                    color = GoldYellow
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFFF5722).copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, Color(0xFFFF5722))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Whatshot,
                                    contentDescription = "Streak",
                                    tint = Color(0xFFFF7043),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Day ${userStats?.streakDays ?: 1} Streak",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = Color(0xFFFFAB91)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Live Balances: Coins & Gems Side-by-Side
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Coins Box
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFF281408),
                            border = BorderStroke(1.dp, GoldAccent),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onNavigateTab(AppTab.WALLET) }
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.MonetizationOn,
                                        contentDescription = "Coins",
                                        tint = GoldAccent,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "COINS BALANCE",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldOrange
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "%,d".format(coins),
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Black,
                                    color = GoldYellow
                                )
                                Text(
                                    text = "≈ ₹$inrValue INR Value",
                                    fontSize = 11.sp,
                                    color = Color(0xFFE2E8F0)
                                )
                            }
                        }

                        // Gems Box
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFF072738),
                            border = BorderStroke(1.dp, GemCyan),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onOpenGemExchange() }
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "💎", fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "GEMS VAULT",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GemCyan
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "$gems 💎",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF67E8F9)
                                )
                                Text(
                                    text = "Tap to Exchange",
                                    fontSize = 11.sp,
                                    color = GoldYellow,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Buttons: Quick Withdraw & Daily Claim
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onNavigateTab(AppTab.WALLET) },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .testTag("home_redeem_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = "Redeem",
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Redeem Cash",
                                fontWeight = FontWeight.Black,
                                color = Color.Black,
                                fontSize = 13.sp
                            )
                        }

                        Button(
                            onClick = onClaimCheckIn,
                            colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .testTag("home_claim_streak_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Check-in",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Daily Bonus",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        // ⚔️ NEW: TOURNAMENT ARENA FEATURE CARD (Ludo, Saamp Seedhi, Free Fire, BGMI)
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF5B1061), Color(0xFF2A0D45), Color(0xFF110729))
                        )
                    )
                    .border(BorderStroke(1.dp, Color(0xFFB026FF)), RoundedCornerShape(22.dp))
                    .clickable { onNavigateTab(AppTab.TOURNAMENTS) }
                    .testTag("home_tournaments_card")
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFF5722).copy(alpha = 0.25f)
                        ) {
                            Text(
                                text = "🔥 NEW TOURNAMENT ARENA",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFFFAB91),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Ludo, Saamp Seedhi & Esports",
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Join 1v1 Ludo rooms or Free Fire / BGMI custom rooms to win coins!",
                            fontSize = 12.sp,
                            color = Color(0xFFE2D9F3)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(GoldAccent),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SportsEsports,
                            contentDescription = "Esports",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        // 💎 MEGA OFFER (25 GEMS CHALLENGE) & SUPER OFFER (5,000 COINS)
        item {
            Text(
                text = "💎 Special 25 Gems Offers",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp
                ),
                color = Color.White
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Mega Offer Card (25 Gems Challenge)
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF101935)),
                    border = BorderStroke(1.dp, GemCyan),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onOpenMegaOffer(false) }
                        .testTag("home_mega_offer_card")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "💎 MEGA OFFER", fontSize = 11.sp, fontWeight = FontWeight.Black, color = GemCyan)
                            Text(text = "$megaProgress/25", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldYellow)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "25 Gems Hunt",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Answer Qs + Ads = 2,500 Coins Grand Bonus!",
                            fontSize = 11.sp,
                            color = Color(0xFFCBD5E1)
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { (megaProgress.toFloat() / 25).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = GemCyan,
                            trackColor = Color(0xFF1E293B)
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { onOpenMegaOffer(false) },
                            colors = ButtonDefaults.buttonColors(containerColor = GemCyan),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Play & Collect 💎", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.Black)
                        }
                    }
                }

                // Super Offer Card (5,000 Coins)
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF260D1E)),
                    border = BorderStroke(1.dp, Color(0xFFFF5722)),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onOpenMegaOffer(true) }
                        .testTag("home_super_offer_card")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "🔥 SUPER OFFER", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color(0xFFFFAB91))
                            Text(text = "$superProgress/25", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldYellow)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Super 5K Pool",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                        Text(
                            text = "High rewards + 5,000 Coins Grand Bonus!",
                            fontSize = 11.sp,
                            color = Color(0xFFCBD5E1)
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { (superProgress.toFloat() / 25).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = Color(0xFFFF5722),
                            trackColor = Color(0xFF381B2B)
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { onOpenMegaOffer(true) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722)),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Join Super 🔥", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.White)
                        }
                    }
                }
            }
        }

        // 🚀 QUICK EARN SHORTCUTS GRID
        item {
            Text(
                text = "⚡ Quick Earn Hub",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp
                ),
                color = Color.White
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickEarnCard(
                        title = "Spin & Win",
                        subtitle = "Wheel of Fortune",
                        rewardText = "+1,000 Coins",
                        icon = Icons.Default.Casino,
                        gradientColors = listOf(0xFFFF8008, 0xFFFFC837),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateTab(AppTab.SPIN) }
                    )
                    QuickEarnCard(
                        title = "Arcade Games",
                        subtitle = "Math, Tap, Memory",
                        rewardText = "+500/Game",
                        icon = Icons.Default.Games,
                        gradientColors = listOf(0xFF8E2DE2, 0xFF4A00E0),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateTab(AppTab.GAMES) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickEarnCard(
                        title = "Watch & Earn",
                        subtitle = "Video Ads & Clips",
                        rewardText = "+200/Ad",
                        icon = Icons.Default.OndemandVideo,
                        gradientColors = listOf(0xFF11998E, 0xFF38EF7D),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateTab(AppTab.WATCH) }
                    )
                    QuickEarnCard(
                        title = "Refer & Earn",
                        subtitle = "Invite Friends",
                        rewardText = "+500/Friend",
                        icon = Icons.Default.People,
                        gradientColors = listOf(0xFFFF416C, 0xFFFF4B2B),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateTab(AppTab.REFERRAL) }
                    )
                }
            }
        }

        // 🌐 OFFERWALL HOT PICKS
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🌐 High-Yield Offerwalls",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp
                    ),
                    color = Color.White
                )
                Text(
                    text = "View All Tasks",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SkyBlue,
                    modifier = Modifier.clickable { onNavigateTab(AppTab.TASKS) }
                )
            }
        }

        items(offerwalls.take(3)) { partner ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF160D2C)),
                border = BorderStroke(1.dp, Color(0xFF2E1A56)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenOfferwallDetail(partner) }
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = partner.name,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = GoldAccent.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = partner.badge,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldYellow,
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
                        colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("Open Wall", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }

        // 🏆 LIVE WINNERS TICKER
        item {
            Text(
                text = "🎉 Recent Payouts & Winners",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp
                ),
                color = Color.White
            )
        }

        item {
            val winners = listOf(
                Pair("Rahul S. • UP", "₹500 Google Play Code"),
                Pair("Amit K. • Delhi", "₹200 UPI Transfer"),
                Pair("Priya M. • Mumbai", "₹500 Amazon Gift Card"),
                Pair("Vikas G. • Punjab", "₹100 Flipkart Voucher"),
                Pair("Sneha R. • Karnataka", "₹300 Bank Transfer")
            )

            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF140A26)),
                border = BorderStroke(1.dp, Color(0xFF2A154D)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    winners.forEach { (winner, prize) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "✅", fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = winner, fontSize = 12.sp, color = Color(0xFFE2E8F0))
                            }
                            Text(text = prize, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EmeraldGreen)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun QuickEarnCard(
    title: String,
    subtitle: String,
    rewardText: String,
    icon: ImageVector,
    gradientColors: List<Long>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF160D2C)),
        border = BorderStroke(1.dp, Color(0xFF2E1856)),
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(gradientColors[0]), Color(gradientColors[1]))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                fontWeight = FontWeight.Black,
                fontSize = 14.sp,
                color = Color.White
            )

            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = Color(0xFF94A3B8)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = GoldAccent.copy(alpha = 0.15f)
            ) {
                Text(
                    text = rewardText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldYellow,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}
