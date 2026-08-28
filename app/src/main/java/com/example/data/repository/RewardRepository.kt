package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.model.ScratchCard
import com.example.data.model.TaskCategory
import com.example.data.model.TaskItem
import com.example.data.model.TransactionRecord
import com.example.data.model.TransactionType
import com.example.data.model.UserStats
import com.example.data.model.WithdrawalRecord
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class RewardRepository(private val db: AppDatabase) {

    val userStats: Flow<UserStats?> = db.userDao().getUserStats()
    val transactions: Flow<List<TransactionRecord>> = db.transactionDao().getAllTransactions()
    val tasks: Flow<List<TaskItem>> = db.taskDao().getAllTasks()
    val scratchCards: Flow<List<ScratchCard>> = db.scratchDao().getAllScratchCards()
    val withdrawals: Flow<List<WithdrawalRecord>> = db.withdrawalDao().getAllWithdrawals()

    suspend fun initializeDefaultDataIfEmpty() {
        val user = db.userDao().getUserStatsSync()
        if (user == null) {
            val randomCode = "CASH" + (1000 + Random.nextInt(9000))
            val initialStats = UserStats(
                id = 1,
                coins = 300,
                totalEarned = 300,
                totalWithdrawn = 0,
                dailySpinsLeft = 10,
                lastSpinDate = System.currentTimeMillis(),
                streakDays = 1,
                lastStreakDate = 0L,
                referralCode = randomCode,
                scratchCardsLeft = 5,
                lastScratchDate = System.currentTimeMillis()
            )
            db.userDao().insertOrUpdate(initialStats)

            // Add Welcome Bonus transaction
            db.transactionDao().insert(
                TransactionRecord(
                    title = "Welcome Signup Bonus",
                    type = TransactionType.WELCOME_BONUS,
                    coins = 300,
                    description = "Instant joining gift credited to your wallet!"
                )
            )
        }

        // Initialize tasks if empty
        if (db.taskDao().getCount() == 0) {
            val defaultTasks = listOf(
                TaskItem(
                    id = "task_daily_login",
                    title = "Daily Attendance Check-in",
                    description = "Claim your consecutive daily streak coins",
                    rewardCoins = 100,
                    category = TaskCategory.DAILY,
                    iconName = "CALENDAR"
                ),
                TaskItem(
                    id = "task_spin_5",
                    title = "Lucky Spinner Challenge",
                    description = "Complete 5 spins on the Wheel of Fortune",
                    rewardCoins = 150,
                    category = TaskCategory.DAILY,
                    iconName = "CASINO"
                ),
                TaskItem(
                    id = "task_watch_3",
                    title = "Watch 3 Video Spotlights",
                    description = "Stream high-earning sponsored video clips",
                    rewardCoins = 200,
                    category = TaskCategory.DAILY,
                    iconName = "PLAY"
                ),
                TaskItem(
                    id = "task_math_master",
                    title = "Math Sprint Champion",
                    description = "Score 5 correct answers in Math Game",
                    rewardCoins = 120,
                    category = TaskCategory.DAILY,
                    iconName = "CALCULATOR"
                ),
                TaskItem(
                    id = "task_survey_gaming",
                    title = "Gaming Preferences Survey",
                    description = "Answer 3 quick questions about mobile gaming",
                    rewardCoins = 350,
                    category = TaskCategory.SURVEY,
                    iconName = "POLL"
                ),
                TaskItem(
                    id = "task_survey_shopping",
                    title = "Shopping Habits Pulse",
                    description = "Share your favorite shopping category insights",
                    rewardCoins = 400,
                    category = TaskCategory.SURVEY,
                    iconName = "SHOPPING"
                ),
                TaskItem(
                    id = "task_social_tg",
                    title = "Join Official Community Channel",
                    description = "Get instant notifications for promo codes & giveaways",
                    rewardCoins = 250,
                    category = TaskCategory.SOCIAL,
                    iconName = "TELEGRAM"
                ),
                TaskItem(
                    id = "task_rate_app",
                    title = "Rate 5-Stars & Support Us",
                    description = "Give feedback to unlock developer supporter badge",
                    rewardCoins = 500,
                    category = TaskCategory.SPECIAL,
                    iconName = "STAR"
                )
            )
            db.taskDao().insertAll(defaultTasks)
        }

        // Initialize scratch cards if empty
        if (db.scratchDao().getCount() == 0) {
            val cards = listOf(
                ScratchCard(title = "Diamond Strike", rewardCoins = 80, isScratched = false, cardIndex = 1),
                ScratchCard(title = "Golden Fortune", rewardCoins = 120, isScratched = false, cardIndex = 2),
                ScratchCard(title = "Lucky 777", rewardCoins = 200, isScratched = false, cardIndex = 3),
                ScratchCard(title = "Jackpot Blaster", rewardCoins = 350, isScratched = false, cardIndex = 4),
                ScratchCard(title = "Royal Surprise", rewardCoins = 150, isScratched = false, cardIndex = 5)
            )
            db.scratchDao().insertAll(cards)
        }
    }

    suspend fun addCoins(amount: Long, title: String, type: TransactionType, description: String = "") {
        val current = db.userDao().getUserStatsSync() ?: return
        val newCoins = current.coins + amount
        val newTotal = current.totalEarned + amount
        db.userDao().update(current.copy(coins = newCoins, totalEarned = newTotal))
        db.transactionDao().insert(
            TransactionRecord(
                title = title,
                type = type,
                coins = amount,
                description = description
            )
        )
    }

    suspend fun performSpin(earnedCoins: Long): Boolean {
        val current = db.userDao().getUserStatsSync() ?: return false
        if (current.dailySpinsLeft <= 0) return false

        val newSpinsLeft = current.dailySpinsLeft - 1
        val newCoins = current.coins + earnedCoins
        val newTotal = current.totalEarned + earnedCoins

        db.userDao().update(
            current.copy(
                coins = newCoins,
                totalEarned = newTotal,
                dailySpinsLeft = newSpinsLeft,
                lastSpinDate = System.currentTimeMillis()
            )
        )

        db.transactionDao().insert(
            TransactionRecord(
                title = "Lucky Wheel Spin",
                type = TransactionType.SPIN_WHEEL,
                coins = earnedCoins,
                description = "Won +$earnedCoins coins from the Lucky Fortune Wheel!"
            )
        )
        return true
    }

    suspend fun refillSpins(extraSpins: Int = 5) {
        val current = db.userDao().getUserStatsSync() ?: return
        db.userDao().update(current.copy(dailySpinsLeft = current.dailySpinsLeft + extraSpins))
        db.transactionDao().insert(
            TransactionRecord(
                title = "Spin Refill Bonus",
                type = TransactionType.WATCH_VIDEO,
                coins = 50,
                description = "Claimed +$extraSpins free spins!"
            )
        )
    }

    suspend fun claimDailyCheckIn(): Pair<Boolean, Long> {
        val current = db.userDao().getUserStatsSync() ?: return Pair(false, 0L)
        val todayStr = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        val lastCheckInStr = if (current.lastStreakDate > 0) {
            SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date(current.lastStreakDate))
        } else ""

        if (todayStr == lastCheckInStr) {
            return Pair(false, 0L) // Already claimed today
        }

        val streak = if (current.streakDays >= 7) 1 else current.streakDays + 1
        val streakRewards = listOf(50L, 80L, 120L, 180L, 250L, 350L, 600L)
        val reward = streakRewards.getOrElse(streak - 1) { 100L }

        val newCoins = current.coins + reward
        val newTotal = current.totalEarned + reward

        db.userDao().update(
            current.copy(
                coins = newCoins,
                totalEarned = newTotal,
                streakDays = streak,
                lastStreakDate = System.currentTimeMillis()
            )
        )

        db.transactionDao().insert(
            TransactionRecord(
                title = "Day $streak Streak Reward",
                type = TransactionType.DAILY_CHECKIN,
                coins = reward,
                description = "Claimed daily attendance streak reward for Day $streak!"
            )
        )

        return Pair(true, reward)
    }

    suspend fun completeTask(taskId: String, rewardCoins: Long, title: String): Boolean {
        db.taskDao().markCompleted(taskId)
        addCoins(rewardCoins, title, TransactionType.TASK_COMPLETE, "Completed task: $title")
        return true
    }

    suspend fun scratchCard(cardId: Long, rewardCoins: Long, title: String): Boolean {
        val current = db.userDao().getUserStatsSync() ?: return false
        db.scratchDao().markScratched(cardId)
        val cardsLeft = (current.scratchCardsLeft - 1).coerceAtLeast(0)
        db.userDao().update(current.copy(scratchCardsLeft = cardsLeft))
        addCoins(rewardCoins, "$title Scratch Card", TransactionType.SCRATCH_CARD, "Scratched & revealed +$rewardCoins coins!")
        return true
    }

    suspend fun applyReferralCode(enteredCode: String): Pair<Boolean, String> {
        val current = db.userDao().getUserStatsSync() ?: return Pair(false, "User not found")
        if (current.referredBy != null) {
            return Pair(false, "You have already applied a referral code!")
        }
        if (enteredCode.trim().equals(current.referralCode, ignoreCase = true)) {
            return Pair(false, "You cannot use your own referral code!")
        }
        if (enteredCode.trim().length < 4) {
            return Pair(false, "Invalid referral code format!")
        }

        val bonus = 250L
        val newCoins = current.coins + bonus
        val newTotal = current.totalEarned + bonus
        db.userDao().update(
            current.copy(
                coins = newCoins,
                totalEarned = newTotal,
                referredBy = enteredCode.trim().uppercase()
            )
        )

        db.transactionDao().insert(
            TransactionRecord(
                title = "Referral Friend Bonus",
                type = TransactionType.REFERRAL_REWARD,
                coins = bonus,
                description = "Applied referral code: ${enteredCode.trim().uppercase()} (+250 Coins bonus)"
            )
        )

        return Pair(true, "Successfully claimed 250 Bonus Coins!")
    }

    suspend fun simulateFriendInvite(): Long {
        val current = db.userDao().getUserStatsSync() ?: return 0L
        val bonus = 500L
        val newCoins = current.coins + bonus
        val newTotal = current.totalEarned + bonus
        val newCount = current.referralCount + 1
        val newReferralEarnings = current.referralEarnings + bonus

        db.userDao().update(
            current.copy(
                coins = newCoins,
                totalEarned = newTotal,
                referralCount = newCount,
                referralEarnings = newReferralEarnings
            )
        )

        db.transactionDao().insert(
            TransactionRecord(
                title = "Friend Joined via Referral",
                type = TransactionType.REFERRAL_INVITE,
                coins = bonus,
                description = "Your friend joined using your code! (+500 Coins)"
            )
        )

        return bonus
    }

    suspend fun requestWithdrawal(method: String, coins: Long, amountFormatted: String, destination: String): Pair<Boolean, String> {
        val current = db.userDao().getUserStatsSync() ?: return Pair(false, "User not found")
        if (current.coins < coins) {
            return Pair(false, "Insufficient coins balance! You have ${current.coins} coins.")
        }
        if (destination.isBlank()) {
            return Pair(false, "Please enter a valid payout account/ID.")
        }

        val newCoins = current.coins - coins
        val newWithdrawn = current.totalWithdrawn + coins

        db.userDao().update(current.copy(coins = newCoins, totalWithdrawn = newWithdrawn))

        db.withdrawalDao().insert(
            WithdrawalRecord(
                method = method,
                coinsDeducted = coins,
                amountFormatted = amountFormatted,
                destinationAccount = destination,
                status = "PENDING"
            )
        )

        db.transactionDao().insert(
            TransactionRecord(
                title = "Redeem $amountFormatted via $method",
                type = TransactionType.WITHDRAWAL,
                coins = -coins,
                status = "PENDING",
                description = "Payout sent to $destination. Estimated transfer: 1-2 business hours."
            )
        )

        return Pair(true, "Withdrawal request of $amountFormatted submitted successfully!")
    }

    // ==================== ADMIN PANEL CONTROLS ====================

    suspend fun approveWithdrawal(withdrawalId: Long): Pair<Boolean, String> {
        val withdrawal = db.withdrawalDao().getWithdrawalById(withdrawalId)
            ?: return Pair(false, "Withdrawal request not found")

        db.withdrawalDao().updateStatus(withdrawalId, "APPROVED")

        db.transactionDao().insert(
            TransactionRecord(
                title = "Withdrawal Approved: ${withdrawal.amountFormatted}",
                type = TransactionType.WITHDRAWAL,
                coins = 0L,
                status = "COMPLETED",
                description = "Admin approved payout to ${withdrawal.destinationAccount} via ${withdrawal.method}"
            )
        )
        return Pair(true, "Withdrawal #${withdrawalId} for ${withdrawal.amountFormatted} has been APPROVED & MARKED PAID!")
    }

    suspend fun rejectWithdrawal(withdrawalId: Long, reason: String): Pair<Boolean, String> {
        val withdrawal = db.withdrawalDao().getWithdrawalById(withdrawalId)
            ?: return Pair(false, "Withdrawal request not found")

        if (withdrawal.status == "REJECTED") {
            return Pair(false, "This withdrawal request is already rejected.")
        }

        db.withdrawalDao().updateStatus(withdrawalId, "REJECTED")

        // Refund coins to user
        val current = db.userDao().getUserStatsSync()
        if (current != null) {
            val refundedCoins = current.coins + withdrawal.coinsDeducted
            val refundedWithdrawn = (current.totalWithdrawn - withdrawal.coinsDeducted).coerceAtLeast(0)
            db.userDao().update(current.copy(coins = refundedCoins, totalWithdrawn = refundedWithdrawn))
        }

        db.transactionDao().insert(
            TransactionRecord(
                title = "Refund: ${withdrawal.amountFormatted} (${withdrawal.coinsDeducted} Coins)",
                type = TransactionType.WITHDRAWAL,
                coins = withdrawal.coinsDeducted,
                status = "REFUNDED",
                description = "Withdrawal rejected by Admin: $reason. Coins refunded to wallet."
            )
        )
        return Pair(true, "Withdrawal #${withdrawalId} REJECTED. ${withdrawal.coinsDeducted} coins refunded to user!")
    }

    suspend fun markWithdrawalUnderReview(withdrawalId: Long): Pair<Boolean, String> {
        val withdrawal = db.withdrawalDao().getWithdrawalById(withdrawalId)
            ?: return Pair(false, "Withdrawal request not found")

        db.withdrawalDao().updateStatus(withdrawalId, "UNDER_REVIEW")
        return Pair(true, "Withdrawal #${withdrawalId} marked as UNDER REVIEW.")
    }

    suspend fun adminAdjustUserCoins(amountDelta: Long, reason: String): Pair<Boolean, String> {
        val current = db.userDao().getUserStatsSync() ?: return Pair(false, "User not found")
        val updatedCoins = (current.coins + amountDelta).coerceAtLeast(0)
        val updatedTotal = if (amountDelta > 0) current.totalEarned + amountDelta else current.totalEarned
        db.userDao().update(current.copy(coins = updatedCoins, totalEarned = updatedTotal))

        val actionDesc = if (amountDelta >= 0) "+$amountDelta Coins added by Admin" else "$amountDelta Coins deducted by Admin"
        db.transactionDao().insert(
            TransactionRecord(
                title = "Admin Wallet Adjustment",
                type = TransactionType.WELCOME_BONUS,
                coins = amountDelta,
                status = "COMPLETED",
                description = "$actionDesc: $reason"
            )
        )
        return Pair(true, "Successfully updated user wallet! New Balance: $updatedCoins Coins")
    }

    suspend fun adminResetDailyLimits(newSpins: Int = 10, newCards: Int = 5): Pair<Boolean, String> {
        val current = db.userDao().getUserStatsSync() ?: return Pair(false, "User not found")
        db.userDao().update(current.copy(dailySpinsLeft = newSpins, scratchCardsLeft = newCards))
        db.taskDao().resetAllTasks()
        db.scratchDao().resetAllCards()
        return Pair(true, "All daily limits, spins ($newSpins), scratch cards ($newCards), and tasks reset!")
    }

    suspend fun adminUpdateReferralCode(newCode: String): Pair<Boolean, String> {
        val current = db.userDao().getUserStatsSync() ?: return Pair(false, "User not found")
        db.userDao().update(current.copy(referralCode = newCode.trim().uppercase()))
        return Pair(true, "Referral Code updated to ${newCode.trim().uppercase()}")
    }
}
