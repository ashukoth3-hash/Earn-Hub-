package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CoralRed
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldOrange
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.SkyBlue
import com.example.ui.viewmodel.ActiveGame
import com.example.ui.viewmodel.LuckyNumberState
import com.example.ui.viewmodel.MathSprintState
import com.example.ui.viewmodel.MemoryGameState
import com.example.ui.viewmodel.TapRushState

@Composable
fun GamesScreen(
    activeGame: ActiveGame,
    mathState: MathSprintState,
    luckyNumberState: LuckyNumberState,
    memoryState: MemoryGameState,
    tapRushState: TapRushState,
    onOpenGame: (ActiveGame) -> Unit,
    onCloseGame: () -> Unit,
    // Math Sprint callbacks
    onAnswerMath: (Int) -> Unit,
    // Lucky Number callbacks
    onGuessChange: (String) -> Unit,
    onSubmitGuess: () -> Unit,
    onRestartLuckyNumber: () -> Unit,
    // Memory Card callbacks
    onFlipCard: (Int) -> Unit,
    onRestartMemory: () -> Unit,
    // Tap Rush callbacks
    onCoinTapped: (Long) -> Unit,
    onRestartTapRush: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (activeGame != ActiveGame.NONE) {
        // Active Game Screen Overlay
        when (activeGame) {
            ActiveGame.MATH_SPRINT -> {
                MathSprintGameView(
                    state = mathState,
                    onAnswer = onAnswerMath,
                    onExit = onCloseGame
                )
            }
            ActiveGame.LUCKY_NUMBER -> {
                LuckyNumberGameView(
                    state = luckyNumberState,
                    onGuessChange = onGuessChange,
                    onSubmitGuess = onSubmitGuess,
                    onRestart = onRestartLuckyNumber,
                    onExit = onCloseGame
                )
            }
            ActiveGame.MEMORY_CARDS -> {
                MemoryCardsGameView(
                    state = memoryState,
                    onFlipCard = onFlipCard,
                    onRestart = onRestartMemory,
                    onExit = onCloseGame
                )
            }
            ActiveGame.TAP_RUSH -> {
                TapRushGameView(
                    state = tapRushState,
                    onCoinTapped = onCoinTapped,
                    onRestart = onRestartTapRush,
                    onExit = onCloseGame
                )
            }
            ActiveGame.NONE -> {}
        }
        return
    }

    // Games Hub List
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2C1354)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(GoldYellow, GoldOrange))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SportsEsports,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "Play & Win Coins",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = "Skill-based mini games with unlimited rewards",
                            fontSize = 12.sp,
                            color = Color(0xFFD1C4E9)
                        )
                    }
                }
            }
        }

        item {
            GameListItem(
                title = "Math Sprint Challenge",
                description = "Solve rapid calculations in 15 seconds to build streak combos!",
                rewardText = "Earn up to 150 Coins",
                icon = Icons.Default.Calculate,
                gradient = listOf(Color(0xFF6B21A8), Color(0xFF4C1D95)),
                testTag = "game_math_sprint",
                onClick = { onOpenGame(ActiveGame.MATH_SPRINT) }
            )
        }

        item {
            GameListItem(
                title = "Lucky Number Guesser (1-50)",
                description = "Guess the mystery number in 5 attempts using high/low clues!",
                rewardText = "Earn up to 200 Coins",
                icon = Icons.Default.HelpOutline,
                gradient = listOf(Color(0xFFC2410C), Color(0xFF9A3412)),
                testTag = "game_lucky_number",
                onClick = { onOpenGame(ActiveGame.LUCKY_NUMBER) }
            )
        }

        item {
            GameListItem(
                title = "Emoji Memory Flip Match",
                description = "Flip and match 6 pairs of lucky reward symbols in fewer moves!",
                rewardText = "Earn up to 180 Coins",
                icon = Icons.Default.Psychology,
                gradient = listOf(Color(0xFF047857), Color(0xFF065F46)),
                testTag = "game_memory_cards",
                onClick = { onOpenGame(ActiveGame.MEMORY_CARDS) }
            )
        }

        item {
            GameListItem(
                title = "Tap Rush Coin Frenzy",
                description = "Fast finger tap spree! Collect popping gold coins within 15 seconds!",
                rewardText = "Earn up to 250 Coins",
                icon = Icons.Default.TouchApp,
                gradient = listOf(Color(0xFFBE185D), Color(0xFF831843)),
                testTag = "game_tap_rush",
                onClick = { onOpenGame(ActiveGame.TAP_RUSH) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun GameListItem(
    title: String,
    description: String,
    rewardText: String,
    icon: ImageVector,
    gradient: List<Color>,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(gradient))
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0x33FFFFFF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = description,
                        fontSize = 12.sp,
                        color = Color(0xFFE2E8F0).copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0x33000000)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = null,
                                tint = GoldYellow,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = rewardText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldYellow
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
                ) {
                    Text(
                        text = "Play",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2C1B00),
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

// ----------------------------------------------------
// MINI GAME 1: MATH SPRINT
// ----------------------------------------------------
@Composable
fun MathSprintGameView(
    state: MathSprintState,
    onAnswer: (Int) -> Unit,
    onExit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF130A24))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Game Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onExit, modifier = Modifier.testTag("math_exit_btn")) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text(
                text = "Math Sprint Challenge",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF331454)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = "Timer",
                        tint = CoralRed,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${state.timeLeftSeconds}s",
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        color = CoralRed
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Timer Progress Bar
        LinearProgressIndicator(
            progress = { state.timeLeftSeconds / 15f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = if (state.timeLeftSeconds > 5) EmeraldGreen else CoralRed,
            trackColor = Color(0xFF281845)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Score & Streak Badges
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ScoreBadge(label = "Score", value = "${state.currentScore}", color = GoldYellow)
            ScoreBadge(label = "Streak", value = "${state.streak}x 🔥", color = Color(0xFFFF5722))
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Math Equation Box
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF231442)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${state.num1} ${state.operator} ${state.num2} = ?",
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 4 Answer Options Grid
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                state.options.take(2).forEach { opt ->
                    Button(
                        onClick = { onAnswer(opt) },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF381D6C)),
                        modifier = Modifier
                            .weight(1f)
                            .height(64.dp)
                    ) {
                        Text(
                            text = "$opt",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                state.options.drop(2).take(2).forEach { opt ->
                    Button(
                        onClick = { onAnswer(opt) },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF381D6C)),
                        modifier = Modifier
                            .weight(1f)
                            .height(64.dp)
                    ) {
                        Text(
                            text = "$opt",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// MINI GAME 2: LUCKY NUMBER
// ----------------------------------------------------
@Composable
fun LuckyNumberGameView(
    state: LuckyNumberState,
    onGuessChange: (String) -> Unit,
    onSubmitGuess: () -> Unit,
    onRestart: () -> Unit,
    onExit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF160924))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onExit) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text(
                text = "Lucky Number 1 - 50",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF4A1A2C)
            ) {
                Text(
                    text = "${state.attemptsLeft} Tries Left",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFFFF8A80),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Hints History Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF22113A)),
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.hints) { hint ->
                    Text(
                        text = hint,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (hint.contains("BINGO")) EmeraldGreen else if (hint.contains("Game Over")) CoralRed else Color(0xFFE2E8F0)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (!state.isGameOver && !state.isWon) {
            // Input Box
            OutlinedTextField(
                value = state.currentGuess,
                onValueChange = onGuessChange,
                label = { Text("Enter your guess (1-50)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = GoldAccent,
                    unfocusedBorderColor = Color(0xFF4C2A75)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("lucky_guess_input")
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onSubmitGuess,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("lucky_submit_guess_btn")
            ) {
                Icon(imageVector = Icons.Default.Send, contentDescription = null, tint = Color(0xFF2C1B00))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Submit Guess", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2C1B00))
            }
        } else {
            Button(
                onClick = onRestart,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Play Again", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ----------------------------------------------------
// MINI GAME 3: MEMORY CARDS
// ----------------------------------------------------
@Composable
fun MemoryCardsGameView(
    state: MemoryGameState,
    onFlipCard: (Int) -> Unit,
    onRestart: () -> Unit,
    onExit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onExit) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text(
                text = "Memory Match (6 Pairs)",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            IconButton(onClick = onRestart) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = "Restart", tint = GoldYellow)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ScoreBadge(label = "Matches", value = "${state.matchesFound}/6", color = EmeraldGreen)
            ScoreBadge(label = "Moves", value = "${state.moves}", color = GoldYellow)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 4x3 Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsIndexed(state.cards) { index, card ->
                val isRevealed = card.isFlipped || card.isMatched
                Box(
                    modifier = Modifier
                        .height(90.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (card.isMatched) Color(0xFF065F46)
                            else if (card.isFlipped) Color(0xFF1E293B)
                            else Color(0xFF334155)
                        )
                        .border(
                            1.5.dp,
                            if (card.isMatched) EmeraldGreen else Color.Transparent,
                            RoundedCornerShape(16.dp)
                        )
                        .clickable { onFlipCard(index) },
                    contentAlignment = Alignment.Center
                ) {
                    if (isRevealed) {
                        Text(
                            text = card.emoji,
                            fontSize = 32.sp
                        )
                    } else {
                        Text(
                            text = "❓",
                            fontSize = 24.sp
                        )
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// MINI GAME 4: TAP RUSH COIN FRENZY
// ----------------------------------------------------
@Composable
fun TapRushGameView(
    state: TapRushState,
    onCoinTapped: (Long) -> Unit,
    onRestart: () -> Unit,
    onExit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A0A26))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onExit) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text(
                text = "Tap Rush Frenzy",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF3B1520)
            ) {
                Text(
                    text = "${state.timeLeftSeconds}s",
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    color = CoralRed,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ScoreBadge(label = "Tapped", value = "${state.tappedCount}", color = GoldYellow)
            ScoreBadge(label = "Coins Won", value = "+${state.totalCoinsEarned}", color = EmeraldGreen)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Interactive Tap Playfield Canvas/Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF28143F))
                .border(2.dp, Color(0xFF4C2777), RoundedCornerShape(24.dp))
        ) {
            if (state.isPlaying && !state.isGameOver) {
                state.activeCoins.forEach { coin ->
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .align(Alignment.TopStart)
                            .padding(
                                start = (coin.xPercent * 260).dp,
                                top = (coin.yPercent * 340).dp
                            )
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    if (coin.isGolden) listOf(GoldYellow, GoldOrange)
                                    else listOf(Color(0xFF00E676), Color(0xFF00B0FF))
                                )
                            )
                            .clickable { onCoinTapped(coin.id) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (coin.isGolden) "+10" else "+5",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF1B0B2E)
                        )
                    }
                }
            } else if (state.isGameOver) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "TIME'S UP!",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        color = GoldYellow
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "You collected +${state.totalCoinsEarned} Coins!",
                        fontSize = 16.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onRestart,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                    ) {
                        Text("Play Again", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun ScoreBadge(label: String, value: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF251642)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Text(text = label, fontSize = 11.sp, color = Color(0xFF94A3B8))
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Black, color = color)
        }
    }
}
