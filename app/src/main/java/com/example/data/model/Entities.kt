package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_stats")
data class UserStats(
    @PrimaryKey val id: Int = 1,
    val coins: Long = 500, // Starting bonus
    val gems: Long = 5,    // Gem balance for Mega/Super offers
    val totalEarned: Long = 500,
    val totalWithdrawn: Long = 0,
    val dailySpinsLeft: Int = 10,
    val lastSpinDate: Long = 0L,
    val streakDays: Int = 1,
    val lastStreakDate: Long = 0L,
    val referralCode: String = "EARN777",
    val referredBy: String? = null,
    val referralCount: Int = 0,
    val referralEarnings: Long = 0,
    val scratchCardsLeft: Int = 5,
    val lastScratchDate: Long = 0L,
    val megaOfferProgress: Int = 0, // 0 to 25 Ads/Questions
    val superOfferProgress: Int = 0, // 0 to 25 Ads/Questions
    val userName: String = "Pro Earner",
    val userLevel: Int = 1
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
    MEGA_OFFER_REWARD,
    SUPER_OFFER_REWARD,
    TOURNAMENT_WIN,
    OFFERWALL_REWARD,
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
    val description: String = "",
    val voucherCode: String? = null
)

enum class TaskCategory {
    DAILY,
    OFFERWALL,
    MEGA_OFFER,
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
    val actionUrl: String = "",
    val partnerName: String? = null,
    val isHot: Boolean = false
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
    val method: String, // GOOGLE_PLAY, UPI, BANK_TRANSFER, AMAZON_PAY, FLIPKART
    val coinsDeducted: Long,
    val amountFormatted: String, // e.g. "₹100" or "₹500"
    val destinationAccount: String, // UPI ID / Email / Phone / A/C number
    val accountHolderName: String = "",
    val ifscCode: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "PENDING", // PENDING, APPROVED, REJECTED
    val voucherCode: String? = null, // Redeemed Gift Code given by Admin!
    val utrReference: String? = null, // Transaction UTR / Ref No for UPI & Bank
    val adminNote: String? = null
)

data class TournamentItem(
    val id: String,
    val title: String,
    val gameType: String, // LUDO, SNAKES_LADDERS, FREE_FIRE, BGMI
    val subtitle: String,
    val entryFeeCoins: Long,
    val prizePoolCoins: Long,
    val perKillCoins: Long = 0,
    val status: String, // LIVE_NOW, REGISTRATION_OPEN, COMING_SOON
    val scheduleTime: String,
    val totalSlots: Int,
    val filledSlots: Int,
    val isJoined: Boolean = false,
    val roomId: String? = null,
    val roomPassword: String? = null,
    val matchType: String = "Squad / 1v1"
)

data class OfferwallPartner(
    val id: String,
    val name: String,
    val badge: String,
    val payoutMultiplier: String,
    val description: String,
    val availableOffers: Int = 30,
    val gradientColors: List<Long>
)

data class OfferQuestion(
    val questionId: Int,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val rewardGems: Int = 1,
    val rewardCoins: Long = 50L
)

