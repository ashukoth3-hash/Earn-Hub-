package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_stats")
data class UserStats(
    @PrimaryKey val id: Int = 1,
    val coins: Long = 250, // Starting bonus
    val totalEarned: Long = 250,
    val totalWithdrawn: Long = 0,
    val dailySpinsLeft: Int = 10,
    val lastSpinDate: Long = 0L,
    val streakDays: Int = 1,
    val lastStreakDate: Long = 0L,
    val referralCode: String = "CASH892",
    val referredBy: String? = null,
    val referralCount: Int = 0,
    val referralEarnings: Long = 0,
    val scratchCardsLeft: Int = 5,
    val lastScratchDate: Long = 0L
)

enum class TransactionType {
    WELCOME_BONUS,
    DAILY_CHECKIN,
    SPIN_WHEEL,
    MATH_GAME,
    LUCKY_NUMBER_GAME,
    MEMORY_GAME,
    TAP_RUSH_GAME,
    WATCH_VIDEO,
    SCRATCH_CARD,
    TASK_COMPLETE,
    REFERRAL_INVITE,
    REFERRAL_REWARD,
    WITHDRAWAL
}

@Entity(tableName = "transactions")
data class TransactionRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val type: TransactionType,
    val coins: Long, // Positive for earnings, negative for withdrawals
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "COMPLETED", // COMPLETED, PENDING, REJECTED
    val description: String = ""
)

enum class TaskCategory {
    DAILY,
    SURVEY,
    SOCIAL,
    SPECIAL
}

@Entity(tableName = "tasks")
data class TaskItem(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val rewardCoins: Long,
    val category: TaskCategory,
    val isCompleted: Boolean = false,
    val iconName: String = "STAR",
    val actionUrl: String = ""
)

@Entity(tableName = "scratch_cards")
data class ScratchCard(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val rewardCoins: Long,
    val isScratched: Boolean = false,
    val cardIndex: Int = 0
)

@Entity(tableName = "withdrawals")
data class WithdrawalRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val method: String, // UPI, PayPal, Amazon, GooglePlay, Crypto
    val coinsDeducted: Long,
    val amountFormatted: String, // e.g. "₹100" or "$5.00"
    val destinationAccount: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "PENDING" // PENDING, PROCESSING, COMPLETED
)
