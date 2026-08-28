package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.ScratchCard
import com.example.data.model.TaskItem
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
                showCelebration("Referral Bonus", 250, message)
            } else {
                _userMessage.value = message
            }
        }
    }

    fun simulateFriendJoined() {
        viewModelScope.launch {
            val bonus = repository.simulateFriendInvite()
            showCelebration("Friend Joined!", bonus, "A friend signed up using your link!")
        }
    }

    // --- WITHDRAWAL ---
    fun requestWithdrawal(method: String, coins: Long, amountFormatted: String, destination: String) {
        viewModelScope.launch {
            val (success, message) = repository.requestWithdrawal(method, coins, amountFormatted, destination)
            if (success) {
                showCelebration("Withdrawal Requested", 0L, message)
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
    fun approveWithdrawal(withdrawalId: Long) {
        viewModelScope.launch {
            val (success, message) = repository.approveWithdrawal(withdrawalId)
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
