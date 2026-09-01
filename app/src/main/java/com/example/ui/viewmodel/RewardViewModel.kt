package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.OfferQuestion
import com.example.data.model.OfferwallPartner
import com.example.data.model.ScratchCard
import com.example.data.model.TaskItem
import com.example.data.model.TournamentItem
import com.example.data.model.TransactionRecord
import com.example.data.model.TransactionType
import com.example.data.model.UserStats
import com.example.data.model.WithdrawalRecord
import com.example.data.repository.RewardRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random


enum class AppTab {
    HOME,
    GAMES,
    SPIN,
    WATCH,
    TASKS,
    TOURNAMENTS,
    REFERRAL,
    WALLET,
    ADMIN
}

enum class ActiveGame {
    NONE,
    MATH_SPRINT,
    LUCKY_NUMBER,
    MEMORY_CARDS,
    TAP_RUSH
}

data class MathSprintState(
    val num1: Int = 0,
    val num2: Int = 0,
    val operator: String = "+",
    val correctAnswer: Int = 0,
    val options: List<Int> = emptyList(),
    val currentScore: Int = 0,
    val streak: Int = 0,
    val timeLeftSeconds: Int = 10,
    val isRunning: Boolean = false,
    val isGameOver: Boolean = false,
    val coinsEarned: Long = 0L
)

data class LuckyNumberState(
    val targetNumber: Int = 0,
    val attemptsLeft: Int = 5,
    val hints: List<String> = emptyList(),
    val currentGuess: String = "",
    val isWon: Boolean = false,
    val isGameOver: Boolean = false,
    val coinsEarned: Long = 0L
)

data class MemoryCardItem(
    val id: Int,
    val emoji: String,
    val isFlipped: Boolean = false,
    val isMatched: Boolean = false
)

data class MemoryGameState(
    val cards: List<MemoryCardItem> = emptyList(),
    val flippedCardIndices: List<Int> = emptyList(),
    val moves: Int = 0,
    val matchesFound: Int = 0,
    val isWon: Boolean = false,
    val isPlaying: Boolean = false,
    val coinsEarned: Long = 0L
)

data class TapRushCoin(
    val id: Long,
    val xPercent: Float,
    val yPercent: Float,
    val value: Int = 5,
    val isGolden: Boolean = false
)

data class TapRushState(
    val activeCoins: List<TapRushCoin> = emptyList(),
    val tappedCount: Int = 0,
    val totalCoinsEarned: Long = 0L,
    val timeLeftSeconds: Int = 15,
    val isPlaying: Boolean = false,
    val isGameOver: Boolean = false
)

data class WatchVideoItem(
    val id: String,
    val title: String,
    val channel: String,
    val category: String,
    val rewardCoins: Long,
    val durationSeconds: Int,
    val videoGradientStart: Long,
    val videoGradientEnd: Long,
    val isWatchedToday: Boolean = false
)

data class CelebrationReward(
    val title: String,
    val coins: Long,
    val subtitle: String = "Added to your wallet!",
    val isVisible: Boolean = false
)

class RewardViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RewardRepository

    val userStats: StateFlow<UserStats?>
    val transactions: StateFlow<List<TransactionRecord>>
    val tasks: StateFlow<List<TaskItem>>
    val scratchCards: StateFlow<List<ScratchCard>>
    val withdrawals: StateFlow<List<WithdrawalRecord>>

    private val _currentTab = MutableStateFlow(AppTab.HOME)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    private val _activeGame = MutableStateFlow(ActiveGame.NONE)
    val activeGame: StateFlow<ActiveGame> = _activeGame.asStateFlow()

    // Mini-game states
    private val _mathSprintState = MutableStateFlow(MathSprintState())
    val mathSprintState: StateFlow<MathSprintState> = _mathSprintState.asStateFlow()

    private val _luckyNumberState = MutableStateFlow(LuckyNumberState())
    val luckyNumberState: StateFlow<LuckyNumberState> = _luckyNumberState.asStateFlow()

    private val _memoryGameState = MutableStateFlow(MemoryGameState())
    val memoryGameState: StateFlow<MemoryGameState> = _memoryGameState.asStateFlow()

    private val _tapRushState = MutableStateFlow(TapRushState())
    val tapRushState: StateFlow<TapRushState> = _tapRushState.asStateFlow()

    // Spin Wheel State
    private val _spinAngle = MutableStateFlow(0f)
    val spinAngle: StateFlow<Float> = _spinAngle.asStateFlow()

    private val _isSpinning = MutableStateFlow(false)
    val isSpinning: StateFlow<Boolean> = _isSpinning.asStateFlow()

    // Video Watch List
    private val _videoList = MutableStateFlow(
        listOf(
            WatchVideoItem("v1", "🔥 Epic Mobile Esports Finals Top Plays", "GamerHQ", "Gaming", 100, 15, 0xFF4A148C, 0xFF311B92),
            WatchVideoItem("v2", "📱 Top 5 Game-Changing Tech Innovations 2026", "TechDaily", "Tech", 120, 18, 0xFF004D40, 0xFF00695C),
            WatchVideoItem("v3", "💎 Secret Treasure Island Gameplay Trailer", "ArcadeStudios", "Promos", 150, 20, 0xFFE65100, 0xFFBF360C),
            WatchVideoItem("v4", "⚡ Supercar Electric Turbo Showcase", "SpeedDrive", "Auto", 80, 12, 0xFF1A237E, 0xFF0D47A1),
            WatchVideoItem("v5", "🎁 Unlock Premium Reward Passes Tutorial", "RewardPro", "Tutorial", 90, 15, 0xFF880E4F, 0xFF4A148C)
        )
    )
    val videoList: StateFlow<List<WatchVideoItem>> = _videoList.asStateFlow()

    private val _activeVideo = MutableStateFlow<WatchVideoItem?>(null)
    val activeVideo: StateFlow<WatchVideoItem?> = _activeVideo.asStateFlow()

    private val _videoProgress = MutableStateFlow(0f)
    val videoProgress: StateFlow<Float> = _videoProgress.asStateFlow()

    private val _isVideoPlaying = MutableStateFlow(false)
    val isVideoPlaying: StateFlow<Boolean> = _isVideoPlaying.asStateFlow()

    private val _isVideoClaimable = MutableStateFlow(false)
    val isVideoClaimable: StateFlow<Boolean> = _isVideoClaimable.asStateFlow()

    // Offerwall Partners State
    private val _offerwalls = MutableStateFlow(
        listOf(
            OfferwallPartner(
                id = "adgate",
                name = "AdGate Media",
                badge = "🔥 3X COINS",
                payoutMultiplier = "Up to 5,000 Coins",
                description = "Download apps, reach in-game milestones & unlock high instant rewards.",
                availableOffers = 48,
                gradientColors = listOf(0xFF6A11CB, 0xFF2575FC)
            ),
            OfferwallPartner(
                id = "tapjoy",
                name = "Tapjoy Rewards",
                badge = "⚡ INSTANT PAY",
                payoutMultiplier = "Up to 10,000 Coins",
                description = "Play top trending games & complete arcade levels to earn big coins.",
                availableOffers = 62,
                gradientColors = listOf(0xFFFF416C, 0xFFFF4B2B)
            ),
            OfferwallPartner(
                id = "fyber",
                name = "Fyber OfferWall",
                badge = "⭐ POPULAR",
                payoutMultiplier = "Up to 3,500 Coins",
                description = "Interactive brand surveys, trial registrations and quick installs.",
                availableOffers = 35,
                gradientColors = listOf(0xFF11998E, 0xFF38EF7D)
            ),
            OfferwallPartner(
                id = "cpalead",
                name = "CPALead Express",
                badge = "🚀 FAST 1-MIN",
                payoutMultiplier = "Up to 1,800 Coins",
                description = "Quick 60-second quiz & email survey verification tasks.",
                availableOffers = 29,
                gradientColors = listOf(0xFFF7971E, 0xFFFFD200)
            ),
            OfferwallPartner(
                id = "offertoro",
                name = "OfferToro Premium",
                badge = "💎 HIGH VALUE",
                payoutMultiplier = "Up to 8,000 Coins",
                description = "Premium game passes, subscriptions, and high-yield video task bundles.",
                availableOffers = 41,
                gradientColors = listOf(0xFF8E2DE2, 0xFF4A00E0)
            )
        )
    )
    val offerwalls: StateFlow<List<OfferwallPartner>> = _offerwalls.asStateFlow()

    // Tournament Hub State
    private val _tournaments = MutableStateFlow(
        listOf(
            TournamentItem(
                id = "t_ludo_1",
                title = "Ludo King Championship 🎲",
                gameType = "LUDO",
                subtitle = "1v1 Quick Match • Winner Takes All",
                entryFeeCoins = 50,
                prizePoolCoins = 450,
                status = "LIVE_NOW",
                scheduleTime = "Every 15 Minutes",
                totalSlots = 100,
                filledSlots = 88,
                isJoined = false,
                roomId = "LUDO-ROOM-8821",
                roomPassword = "WIN77",
                matchType = "1v1 Classic"
            ),
            TournamentItem(
                id = "t_snakes_1",
                title = "Saamp Seedhi Pro (Snakes & Ladders) 🐍",
                gameType = "SNAKES_LADDERS",
                subtitle = "Fast 50-Step Race • Top 3 Win",
                entryFeeCoins = 30,
                prizePoolCoins = 300,
                status = "LIVE_NOW",
                scheduleTime = "Starting in 5 min",
                totalSlots = 50,
                filledSlots = 42,
                isJoined = false,
                roomId = "SNAKE-PASS-4920",
                roomPassword = "LUCK9",
                matchType = "4-Player Table"
            ),
            TournamentItem(
                id = "t_ff_1",
                title = "Free Fire MAX Diamond Room 💥",
                gameType = "FREE_FIRE",
                subtitle = "Bermuda Solo / Squad Battle Royale • Per Kill +200 Coins",
                entryFeeCoins = 100,
                prizePoolCoins = 2500,
                perKillCoins = 200,
                status = "REGISTRATION_OPEN",
                scheduleTime = "Tonight at 8:00 PM",
                totalSlots = 48,
                filledSlots = 39,
                isJoined = false,
                roomId = "FF-CUSTOM-ROOM-9182",
                roomPassword = "BOOYAH-PRO",
                matchType = "Solo Clash Squad"
            ),
            TournamentItem(
                id = "t_bgmi_1",
                title = "BGMI Erangel Squad Cup 🏆",
                gameType = "BGMI",
                subtitle = "Erangel Classic Custom Room • Chicken Dinner Grand Pool",
                entryFeeCoins = 150,
                prizePoolCoins = 5000,
                perKillCoins = 250,
                status = "COMING_SOON",
                scheduleTime = "Coming Soon (Next Room Opening)",
                totalSlots = 100,
                filledSlots = 94,
                isJoined = false,
                roomId = "BGMI-SQUAD-1004",
                roomPassword = "PUBG-WINNER",
                matchType = "Squad TPP"
            )
        )
    )
    val tournaments: StateFlow<List<TournamentItem>> = _tournaments.asStateFlow()

    // Mega Offer & Super Offer Questions
    private val megaQuestions = listOf(
        OfferQuestion(1, "What is the capital city of India?", listOf("Mumbai", "New Delhi", "Kolkata", "Bengaluru"), 1),
        OfferQuestion(2, "Which mobile game is famous for 'Winner Winner Chicken Dinner'?", listOf("Candy Crush", "Free Fire", "BGMI / PUBG", "Subway Surfers"), 2),
        OfferQuestion(3, "In Ludo, what roll on the dice lets you bring out a token?", listOf("1 only", "6 only", "Both 1 and 6", "4 only"), 1),
        OfferQuestion(4, "What does UPI stand for in digital payments?", listOf("Unified Payments Interface", "Universal Public Internet", "Unique Phone Index", "United Pay India"), 0),
        OfferQuestion(5, "How many players are in a standard cricket team on field?", listOf("9", "10", "11", "12"), 2),
        OfferQuestion(6, "Which company developed the Android Operating System?", listOf("Apple", "Microsoft", "Google", "Samsung"), 2),
        OfferQuestion(7, "What is the currency symbol '₹' used for?", listOf("Indian Rupee", "US Dollar", "British Pound", "Euro"), 0),
        OfferQuestion(8, "Which of these is used for online shopping deliveries?", listOf("Amazon & Flipkart", "Google Maps", "Calculator", "Camera"), 0),
        OfferQuestion(9, "What happens when you land on a ladder in Snakes and Ladders?", listOf("Go Down", "Climb Up Fast", "Lose Coins", "Wait 1 Turn"), 1),
        OfferQuestion(10, "What is the maximum number of gems in the 25 Gems Challenge?", listOf("10", "20", "25 Gems", "50"), 2)
    )

    private val _currentMegaQuestionIndex = MutableStateFlow(0)
    val currentMegaQuestionIndex: StateFlow<Int> = _currentMegaQuestionIndex.asStateFlow()

    private val _isMegaOfferDialogVisible = MutableStateFlow(false)
    val isMegaOfferDialogVisible: StateFlow<Boolean> = _isMegaOfferDialogVisible.asStateFlow()

    private val _isSuperOfferMode = MutableStateFlow(false)
    val isSuperOfferMode: StateFlow<Boolean> = _isSuperOfferMode.asStateFlow()

    private val _isOfferwallDetailVisible = MutableStateFlow<OfferwallPartner?>(null)
    val isOfferwallDetailVisible: StateFlow<OfferwallPartner?> = _isOfferwallDetailVisible.asStateFlow()

    // Celebration Dialog
    private val _celebration = MutableStateFlow(CelebrationReward("", 0L, isVisible = false))
    val celebration: StateFlow<CelebrationReward> = _celebration.asStateFlow()

    // Snackbar/Feedback message
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    private var mathTimerJob: Job? = null
    private var tapRushTimerJob: Job? = null
    private var videoTimerJob: Job? = null

    init {
        val database = AppDatabase.getDatabase(application)
        repository = RewardRepository(database)

        userStats = repository.userStats.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )
        transactions = repository.transactions.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
        tasks = repository.tasks.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
        scratchCards = repository.scratchCards.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
        withdrawals = repository.withdrawals.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        viewModelScope.launch {
            repository.initializeDefaultDataIfEmpty()
        }
    }

    fun selectTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }

    fun showCelebration(title: String, coins: Long, subtitle: String = "Added to your wallet!") {
        _celebration.value = CelebrationReward(title, coins, subtitle, isVisible = true)
    }

    fun dismissCelebration() {
        _celebration.value = _celebration.value.copy(isVisible = false)
    }

    // --- DAILY CHECK-IN ---
    fun claimDailyCheckIn() {
        viewModelScope.launch {
            val (success, reward) = repository.claimDailyCheckIn()
            if (success) {
                showCelebration("Daily Check-in Bonus", reward, "Streak reward claimed successfully!")
            } else {
                _userMessage.value = "You have already claimed today's check-in bonus! Come back tomorrow."
            }
        }
    }

    // --- SPIN WHEEL ---
    val spinSlices = listOf(
        25L, 50L, 10L, 100L, 20L, 250L, 500L, 1000L
    )

    fun spinTheWheel() {
        if (_isSpinning.value) return
        val currentSpins = userStats.value?.dailySpinsLeft ?: 0
        if (currentSpins <= 0) {
            _userMessage.value = "No free spins remaining! Watch a video to refill extra spins."
            return
        }

        _isSpinning.value = true
        val targetSliceIndex = Random.nextInt(spinSlices.size)
        val prizeCoins = spinSlices[targetSliceIndex]

        // 8 slices -> 360 / 8 = 45 degrees per slice
        // Segment 0 is at top (270 deg or pointer position at top 0/360)
        val sliceAngle = 360f / spinSlices.size
        val targetSliceCenter = targetSliceIndex * sliceAngle + (sliceAngle / 2)
        val totalRotations = 5 * 360f // 5 full loops
        // The pointer is at top (270 degrees in standard circle, or top = 0 offset)
        // Wheel rotates clockwise: targetAngle = currentAngle + fullTurns + offset
        val finalAngle = _spinAngle.value + totalRotations + (360f - (targetSliceCenter % 360f))

        _spinAngle.value = finalAngle

        viewModelScope.launch {
            delay(3500) // Animation duration
            _isSpinning.value = false
            repository.performSpin(prizeCoins)
            showCelebration("🎉 Lucky Wheel Win!", prizeCoins, "You won $prizeCoins coins from the wheel!")
        }
    }

    fun refillSpinsWithVideo() {
        viewModelScope.launch {
            repository.refillSpins(5)
            showCelebration("Extra Spins Refilled!", 50, "+5 Free Spins added to your account!")
        }
    }

    // --- MINI GAMES NAVIGATION ---
    fun openGame(game: ActiveGame) {
        _activeGame.value = game
        when (game) {
            ActiveGame.MATH_SPRINT -> startMathSprint()
            ActiveGame.LUCKY_NUMBER -> startLuckyNumber()
            ActiveGame.MEMORY_CARDS -> startMemoryCards()
            ActiveGame.TAP_RUSH -> startTapRush()
            ActiveGame.NONE -> {}
        }
    }

    fun closeGame() {
        mathTimerJob?.cancel()
        tapRushTimerJob?.cancel()
        _activeGame.value = ActiveGame.NONE
    }

    // --- GAME 1: MATH SPRINT ---
    private fun startMathSprint() {
        mathTimerJob?.cancel()
        _mathSprintState.value = MathSprintState(isRunning = true)
        generateNewMathQuestion()
        startMathTimer()
    }

    private fun generateNewMathQuestion() {
        val opType = Random.nextInt(3)
        val n1: Int
        val n2: Int
        val op: String
        val ans: Int

        when (opType) {
            0 -> {
                n1 = Random.nextInt(10, 80)
                n2 = Random.nextInt(5, 50)
                op = "+"
                ans = n1 + n2
            }
            1 -> {
                n1 = Random.nextInt(20, 99)
                n2 = Random.nextInt(5, n1)
                op = "-"
                ans = n1 - n2
            }
            else -> {
                n1 = Random.nextInt(3, 12)
                n2 = Random.nextInt(3, 12)
                op = "×"
                ans = n1 * n2
            }
        }

        val optionsSet = mutableSetOf(ans)
        while (optionsSet.size < 4) {
            val delta = Random.nextInt(-10, 11)
            if (delta != 0 && (ans + delta) >= 0) {
                optionsSet.add(ans + delta)
            }
        }
        val optionsList = optionsSet.shuffled()

        _mathSprintState.value = _mathSprintState.value.copy(
            num1 = n1,
            num2 = n2,
            operator = op,
            correctAnswer = ans,
            options = optionsList
        )
    }

    private fun startMathTimer() {
        mathTimerJob = viewModelScope.launch {
            for (i in 15 downTo 0) {
                _mathSprintState.value = _mathSprintState.value.copy(timeLeftSeconds = i)
                if (i == 0) {
                    endMathGame()
                    break
                }
                delay(1000)
            }
        }
    }

    fun answerMathQuestion(selectedAnswer: Int) {
        val current = _mathSprintState.value
        if (!current.isRunning || current.isGameOver) return

        if (selectedAnswer == current.correctAnswer) {
            val newScore = current.currentScore + 1
            val newStreak = current.streak + 1
            val streakBonus = if (newStreak % 3 == 0) 20L else 10L
            val totalEarned = current.coinsEarned + streakBonus

            _mathSprintState.value = current.copy(
                currentScore = newScore,
                streak = newStreak,
                coinsEarned = totalEarned
            )
            generateNewMathQuestion()
        } else {
            // Wrong answer ends combo
            _mathSprintState.value = current.copy(streak = 0)
            generateNewMathQuestion()
        }
    }

    private fun endMathGame() {
        mathTimerJob?.cancel()
        val finalState = _mathSprintState.value
        val totalCoins = (finalState.currentScore * 15L + (finalState.streak * 5L)).coerceAtLeast(20L)

        _mathSprintState.value = finalState.copy(
            isRunning = false,
            isGameOver = true,
            coinsEarned = totalCoins
        )

        viewModelScope.launch {
            repository.addCoins(
                totalCoins,
                "Math Sprint Game Reward",
                TransactionType.MATH_GAME,
                "Scored ${finalState.currentScore} correct answers in 15s"
            )
            showCelebration("Math Sprint Complete!", totalCoins, "You answered ${finalState.currentScore} math questions correctly!")
        }
    }

    // --- GAME 2: LUCKY NUMBER (1 - 50) ---
    private fun startLuckyNumber() {
        val target = Random.nextInt(1, 51)
        _luckyNumberState.value = LuckyNumberState(
            targetNumber = target,
            attemptsLeft = 5,
            hints = listOf("Guess the secret number between 1 and 50!"),
            currentGuess = "",
            isWon = false,
            isGameOver = false
        )
    }

    fun onGuessInputChanged(input: String) {
        if (input.length <= 2 && input.all { it.isDigit() }) {
            _luckyNumberState.value = _luckyNumberState.value.copy(currentGuess = input)
        }
    }

    fun submitGuess() {
        val state = _luckyNumberState.value
        if (state.isGameOver || state.isWon) return
        val guess = state.currentGuess.toIntOrNull() ?: return
        if (guess < 1 || guess > 50) return

        val newAttempts = state.attemptsLeft - 1
        val newHints = state.hints.toMutableList()

        if (guess == state.targetNumber) {
            val reward = (newAttempts + 1) * 35L + 50L
            newHints.add("🎯 BINGO! The secret number was ${state.targetNumber}!")
            _luckyNumberState.value = state.copy(
                isWon = true,
                isGameOver = true,
                hints = newHints,
                coinsEarned = reward,
                currentGuess = ""
            )
            viewModelScope.launch {
                repository.addCoins(
                    reward,
                    "Lucky Number Guess Win",
                    TransactionType.LUCKY_NUMBER_GAME,
                    "Guessed secret number ${state.targetNumber} with ${newAttempts} tries remaining"
                )
                showCelebration("🎯 Lucky Guesser Win!", reward, "Correct number ${state.targetNumber} guessed!")
            }
        } else {
            val hintText = if (guess < state.targetNumber) {
                "Try higher! $guess is too LOW ⬆️"
            } else {
                "Try lower! $guess is too HIGH ⬇️"
            }
            newHints.add(hintText)

            if (newAttempts <= 0) {
                newHints.add("Game Over! The secret number was ${state.targetNumber}.")
                _luckyNumberState.value = state.copy(
                    attemptsLeft = 0,
                    isGameOver = true,
                    hints = newHints,
                    currentGuess = ""
                )
            } else {
                _luckyNumberState.value = state.copy(
                    attemptsLeft = newAttempts,
                    hints = newHints,
                    currentGuess = ""
                )
            }
        }
    }

    fun restartLuckyNumber() {
        startLuckyNumber()
    }

    // --- GAME 3: EMOJI MEMORY CARD MATCH ---
    private fun startMemoryCards() {
        val emojis = listOf("💎", "💰", "👑", "🚀", "⚡", "🍀")
        val deck = (emojis + emojis).shuffled().mapIndexed { index, emoji ->
            MemoryCardItem(id = index, emoji = emoji)
        }
        _memoryGameState.value = MemoryGameState(
            cards = deck,
            flippedCardIndices = emptyList(),
            moves = 0,
            matchesFound = 0,
            isWon = false,
            isPlaying = true
        )
    }

    fun flipCard(index: Int) {
        val state = _memoryGameState.value
        if (!state.isPlaying || state.isWon) return
        val card = state.cards.getOrNull(index) ?: return
        if (card.isFlipped || card.isMatched) return
        if (state.flippedCardIndices.size >= 2) return

        val updatedCards = state.cards.toMutableList()
        updatedCards[index] = card.copy(isFlipped = true)
        val newFlipped = state.flippedCardIndices + index

        if (newFlipped.size == 2) {
            val idx1 = newFlipped[0]
            val idx2 = newFlipped[1]
            val card1 = updatedCards[idx1]
            val card2 = updatedCards[idx2]

            val newMoves = state.moves + 1

            if (card1.emoji == card2.emoji) {
                // Match found!
                updatedCards[idx1] = card1.copy(isMatched = true)
                updatedCards[idx2] = card2.copy(isMatched = true)
                val newMatches = state.matchesFound + 1
                val isGameWon = newMatches == 6

                _memoryGameState.value = state.copy(
                    cards = updatedCards,
                    flippedCardIndices = emptyList(),
                    moves = newMoves,
                    matchesFound = newMatches,
                    isWon = isGameWon
                )

                if (isGameWon) {
                    val reward = (180L - (newMoves * 5L)).coerceAtLeast(80L)
                    _memoryGameState.value = _memoryGameState.value.copy(coinsEarned = reward)
                    viewModelScope.launch {
                        repository.addCoins(
                            reward,
                            "Memory Cards Victory",
                            TransactionType.MEMORY_GAME,
                            "Matched all pairs in $newMoves moves!"
                        )
                        showCelebration("🧠 Memory Master!", reward, "You matched all 6 card pairs!")
                    }
                }
            } else {
                // Not a match, flip back after delay
                _memoryGameState.value = state.copy(
                    cards = updatedCards,
                    flippedCardIndices = newFlipped,
                    moves = newMoves
                )
                viewModelScope.launch {
                    delay(800)
                    val resetCards = _memoryGameState.value.cards.toMutableList()
                    resetCards[idx1] = resetCards[idx1].copy(isFlipped = false)
                    resetCards[idx2] = resetCards[idx2].copy(isFlipped = false)
                    _memoryGameState.value = _memoryGameState.value.copy(
                        cards = resetCards,
                        flippedCardIndices = emptyList()
                    )
                }
            }
        } else {
            _memoryGameState.value = state.copy(
                cards = updatedCards,
                flippedCardIndices = newFlipped
            )
        }
    }

    fun restartMemoryGame() {
        startMemoryCards()
    }

    // --- GAME 4: TAP RUSH COIN FRENZY ---
    private fun startTapRush() {
        tapRushTimerJob?.cancel()
        _tapRushState.value = TapRushState(
            isPlaying = true,
            timeLeftSeconds = 15,
            tappedCount = 0,
            totalCoinsEarned = 0L,
            isGameOver = false
        )
        spawnTapCoins()
        startTapRushTimer()
    }

    private fun spawnTapCoins() {
        val newCoins = (1..6).map {
            TapRushCoin(
                id = System.currentTimeMillis() + it,
                xPercent = Random.nextFloat() * 0.75f + 0.1f,
                yPercent = Random.nextFloat() * 0.65f + 0.15f,
                value = if (Random.nextFloat() < 0.25f) 10 else 5,
                isGolden = Random.nextFloat() < 0.25f
            )
        }
        _tapRushState.value = _tapRushState.value.copy(activeCoins = newCoins)
    }

    private fun startTapRushTimer() {
        tapRushTimerJob = viewModelScope.launch {
            for (sec in 15 downTo 0) {
                _tapRushState.value = _tapRushState.value.copy(timeLeftSeconds = sec)
                if (sec % 3 == 0 && sec > 0) {
                    spawnTapCoins()
                }
                if (sec == 0) {
                    endTapRushGame()
                    break
                }
                delay(1000)
            }
        }
    }

    fun onCoinTapped(coinId: Long) {
        val state = _tapRushState.value
        if (!state.isPlaying || state.isGameOver) return
        val coin = state.activeCoins.find { it.id == coinId } ?: return

        val remaining = state.activeCoins.filterNot { it.id == coinId }
        val newTapped = state.tappedCount + 1
        val newEarned = state.totalCoinsEarned + coin.value

        // Spawn a replacement coin dynamically
        val replacement = TapRushCoin(
            id = System.currentTimeMillis() + Random.nextInt(1000),
            xPercent = Random.nextFloat() * 0.75f + 0.1f,
            yPercent = Random.nextFloat() * 0.65f + 0.15f,
            value = if (Random.nextFloat() < 0.25f) 10 else 5,
            isGolden = Random.nextFloat() < 0.25f
        )

        _tapRushState.value = state.copy(
            activeCoins = remaining + replacement,
            tappedCount = newTapped,
            totalCoinsEarned = newEarned
        )
    }

    private fun endTapRushGame() {
        tapRushTimerJob?.cancel()
        val finalState = _tapRushState.value
        val totalWon = (finalState.totalCoinsEarned * 2L).coerceAtLeast(30L)

        _tapRushState.value = finalState.copy(
            isPlaying = false,
            isGameOver = true,
            totalCoinsEarned = totalWon
        )

        viewModelScope.launch {
            repository.addCoins(
                totalWon,
                "Tap Rush Coin Frenzy",
                TransactionType.TAP_RUSH_GAME,
                "Tapped ${finalState.tappedCount} coins in 15 seconds!"
            )
            showCelebration("⚡ Tap Frenzy Complete!", totalWon, "You collected ${finalState.tappedCount} coins!")
        }
    }

    fun restartTapRush() {
        startTapRush()
    }

    // --- WATCH & EARN VIDEO PLAYER ---
    fun startWatchingVideo(video: WatchVideoItem) {
        videoTimerJob?.cancel()
        _activeVideo.value = video
        _videoProgress.value = 0f
        _isVideoPlaying.value = true
        _isVideoClaimable.value = false

        videoTimerJob = viewModelScope.launch {
            val totalSteps = video.durationSeconds * 10
            for (step in 1..totalSteps) {
                if (!_isVideoPlaying.value) {
                    delay(100)
                    continue
                }
                delay(100)
                _videoProgress.value = step.toFloat() / totalSteps
            }
            _isVideoPlaying.value = false
            _isVideoClaimable.value = true
        }
    }

    fun toggleVideoPlayPause() {
        _isVideoPlaying.value = !_isVideoPlaying.value
    }

    fun claimVideoReward() {
        val video = _activeVideo.value ?: return
        if (!_isVideoClaimable.value) return

        viewModelScope.launch {
            repository.addCoins(
                video.rewardCoins,
                "Watched: ${video.title}",
                TransactionType.WATCH_VIDEO,
                "Streaming reward credited for sponsor video: ${video.channel}"
            )
            // Mark as watched
            val updated = _videoList.value.map {
                if (it.id == video.id) it.copy(isWatchedToday = true) else it
            }
            _videoList.value = updated

            _activeVideo.value = null
            _isVideoClaimable.value = false
            showCelebration("Video Reward Claimed!", video.rewardCoins, "Thanks for watching sponsor clip!")
        }
    }

    fun closeVideoPlayer() {
        videoTimerJob?.cancel()
        _activeVideo.value = null
        _isVideoPlaying.value = false
        _isVideoClaimable.value = false
    }

    // --- TASKS & SCRATCH CARDS ---
    fun completeTask(task: TaskItem) {
        if (task.isCompleted) return
        viewModelScope.launch {
            repository.completeTask(task.id, task.rewardCoins, task.title)
            showCelebration("Task Completed!", task.rewardCoins, "Reward for: ${task.title}")
        }
    }

    fun scratchCard(card: ScratchCard) {
        if (card.isScratched) return
        viewModelScope.launch {
            repository.scratchCard(card.id, card.rewardCoins, card.title)
            showCelebration("Scratch Win!", card.rewardCoins, "You scratched and revealed +${card.rewardCoins} coins!")
        }
    }

    // --- REFERRAL ---
    fun applyReferralCode(code: String) {
        viewModelScope.launch {
            val (success, message) = repository.applyReferralCode(code)
            if (success) {
                showCelebration("Referral Bonus", 500, message)
            } else {
                _userMessage.value = message
            }
        }
    }

    fun simulateFriendJoined() {
        viewModelScope.launch {
            val bonus = repository.simulateFriendInvite()
            showCelebration("Friend Joined! 👥", bonus, "A friend signed up using your code! +500 Coins")
        }
    }

    // --- MEGA / SUPER OFFER (25 GEMS CHALLENGE) ---
    fun openMegaOffer(isSuper: Boolean = false) {
        _isSuperOfferMode.value = isSuper
        _currentMegaQuestionIndex.value = Random.nextInt(megaQuestions.size)
        _isMegaOfferDialogVisible.value = true
    }

    fun closeMegaOffer() {
        _isMegaOfferDialogVisible.value = false
    }

    fun getActiveMegaQuestion(): OfferQuestion {
        return megaQuestions[_currentMegaQuestionIndex.value.coerceIn(0, megaQuestions.size - 1)]
    }

    fun answerMegaOfferQuestion(selectedOptionIndex: Int) {
        val isSuper = _isSuperOfferMode.value
        val q = getActiveMegaQuestion()
        val isCorrect = (selectedOptionIndex == q.correctIndex)

        viewModelScope.launch {
            val (newProg, coinsWon, gemsWon) = repository.advanceMegaOfferProgress(isSuper)
            _currentMegaQuestionIndex.value = Random.nextInt(megaQuestions.size)
            val offerName = if (isSuper) "Super Offer" else "Mega Offer"

            if (newProg >= 25) {
                showCelebration(
                    "🏆 Grand Milestone Achieved!",
                    coinsWon,
                    "Awesome! You completed all 25 Steps in $offerName! +$gemsWon Gem & Mega Bonus Coins credited!"
                )
                _isMegaOfferDialogVisible.value = false
            } else {
                showCelebration(
                    "Step $newProg/25 Completed! 💎",
                    coinsWon,
                    "Watched Ad & answered question! +$gemsWon Gem & +$coinsWon Coins added!"
                )
            }
        }
    }

    fun convertGemsToCoins(gemAmount: Long) {
        viewModelScope.launch {
            val (success, message) = repository.convertGemsToCoins(gemAmount)
            if (success) {
                showCelebration("Gems Exchanged! 💎", gemAmount * 100L, message)
            } else {
                _userMessage.value = message
            }
        }
    }

    // --- OFFERWALL DETAIL & SIMULATION ---
    fun openOfferwallDetail(partner: OfferwallPartner) {
        _isOfferwallDetailVisible.value = partner
    }

    fun closeOfferwallDetail() {
        _isOfferwallDetailVisible.value = null
    }

    fun completeOfferwallTask(partnerName: String, coins: Long) {
        viewModelScope.launch {
            repository.addCoins(
                coins,
                "$partnerName Task Payout 🚀",
                TransactionType.OFFERWALL_REWARD,
                "Offer verified and credited from $partnerName offerwall partner."
            )
            _isOfferwallDetailVisible.value = null
            showCelebration("Offerwall Reward! 💰", coins, "+$coins Coins credited from $partnerName!")
        }
    }

    // --- TOURNAMENTS HUB ---
    fun joinTournament(item: TournamentItem) {
        viewModelScope.launch {
            val (success, message) = repository.joinTournament(item.id, item.title, item.entryFeeCoins)
            if (success) {
                _tournaments.value = _tournaments.value.map {
                    if (it.id == item.id) it.copy(isJoined = true, filledSlots = it.filledSlots + 1) else it
                }
                showCelebration("Tournament Registered! ⚔️", 0L, message)
            } else {
                _userMessage.value = message
            }
        }
    }

    // --- WITHDRAWAL ---
    fun requestWithdrawal(
        method: String,
        coins: Long,
        amountFormatted: String,
        destination: String,
        accountHolderName: String = "",
        ifscCode: String = ""
    ) {
        viewModelScope.launch {
            val (success, message) = repository.requestWithdrawal(
                method = method,
                coins = coins,
                amountFormatted = amountFormatted,
                destination = destination,
                accountHolderName = accountHolderName,
                ifscCode = ifscCode
            )
            if (success) {
                showCelebration("Withdrawal Submitted! 🚀", 0L, message)
            } else {
                _userMessage.value = message
            }
        }
    }

    // --- ADMOB ADS INTEGRATION ---
    fun awardAdMobVideoReward(coins: Long = 100L) {
        viewModelScope.launch {
            repository.addCoins(
                coins,
                "AdMob Rewarded Video Ad",
                TransactionType.WATCH_VIDEO,
                "Earned from Google AdMob Rewarded Ad (ca-app-pub-1601992247643052/8378590953)"
            )
            showCelebration("AdMob Reward Earned! 🎁", coins, "+$coins Coins credited to your wallet!")
        }
    }

    // --- ADMIN PANEL ACTIONS ---
    fun approveWithdrawalWithVoucher(
        withdrawalId: Long,
        voucherCode: String? = null,
        utrRef: String? = null,
        note: String? = null
    ) {
        viewModelScope.launch {
            val (success, message) = repository.approveWithdrawalWithVoucher(
                withdrawalId = withdrawalId,
                voucherCode = voucherCode,
                utrReference = utrRef,
                adminNote = note
            )
            _userMessage.value = message
            if (success) {
                showCelebration("Withdrawal Approved! 💰", 0L, message)
            }
        }
    }

    fun rejectWithdrawal(withdrawalId: Long, reason: String) {
        viewModelScope.launch {
            val (success, message) = repository.rejectWithdrawal(withdrawalId, reason)
            _userMessage.value = message
        }
    }

    fun markWithdrawalUnderReview(withdrawalId: Long) {
        viewModelScope.launch {
            val (success, message) = repository.markWithdrawalUnderReview(withdrawalId)
            _userMessage.value = message
        }
    }

    fun adminAdjustCoins(amountDelta: Long, reason: String) {
        viewModelScope.launch {
            val (success, message) = repository.adminAdjustUserCoins(amountDelta, reason)
            _userMessage.value = message
        }
    }

    fun adminAdjustGems(deltaGems: Long, reason: String) {
        viewModelScope.launch {
            val (success, message) = repository.adminAdjustUserGems(deltaGems, reason)
            _userMessage.value = message
        }
    }

    fun adminResetLimits(newSpins: Int = 10, newCards: Int = 5) {
        viewModelScope.launch {
            val (success, message) = repository.adminResetDailyLimits(newSpins, newCards)
            _userMessage.value = message
        }
    }

    fun adminUpdateReferralCode(newCode: String) {
        viewModelScope.launch {
            val (success, message) = repository.adminUpdateReferralCode(newCode)
            _userMessage.value = message
        }
    }
}

