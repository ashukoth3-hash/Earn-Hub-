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
            val randomCode = "EARN" + (1000 + Random.nextInt(9000))
            val initialStats = UserStats(
                id = 1,
                coins = 500,
                gems = 5,
                totalEarned = 500,
                totalWithdrawn = 0,
                dailySpinsLeft = 10,
                lastSpinDate = System.currentTimeMillis(),
                streakDays = 1,
                lastStreakDate = 0L,
                referralCode = randomCode,
                scratchCardsLeft = 5,
                lastScratchDate = System.currentTimeMillis(),
                megaOfferProgress = 0,
                superOfferProgress = 0
            )
            db.userDao().insertOrUpdate(initialStats)

            // Add Welcome Bonus transaction
            db.transactionDao().insert(
                TransactionRecord(
                    title = "Welcome Signup Bonus 🎁",
                    type = TransactionType.WELCOME_BONUS,
                    coins = 500,
                    description = "Instant joining gift + 5 Gems credited to your wallet!"
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
                    id = "task_mega_qa",
                    title = "Mega Offer 25 Gems Challenge",
                    description = "Answer Q&As, watch videos, and gather 25 Gems",
                    rewardCoins = 2500,
                    category = TaskCategory.MEGA_OFFER,
                    iconName = "DIAMOND",
                    isHot = true
                ),
                TaskItem(
                    id = "task_adgate_offer",
                    title = "AdGate Media Offerwall",
                    description = "Install trending apps and play games to earn coins",
                    rewardCoins = 1200,
                    category = TaskCategory.OFFERWALL,
                    iconName = "OFFERWALL",
                    partnerName = "AdGate Media",
                    isHot = true
                ),
                TaskItem(
                    id = "task_tapjoy_offer",
                    title = "Tapjoy Rewards Wall",
                    description = "Reach Level 10 in top arcade games for massive payout",
                    rewardCoins = 3500,
                    category = TaskCategory.OFFERWALL,
                    iconName = "OFFERWALL",
                    partnerName = "Tapjoy",
                    isHot = true
                ),
                TaskItem(
                    id = "task_fyber_offer",
                    title = "Fyber Interactive Surveys",
                    description = "Take brand surveys and share opinions",
                    rewardCoins = 800,
                    category = TaskCategory.OFFERWALL,
                    iconName = "OFFERWALL",
                    partnerName = "Fyber"
                ),
                TaskItem(
                    id = "task_cpalead_offer",
                    title = "CPALead Quick Tasks",
                    description = "Complete fast 1-minute lead verification tasks",
                    rewardCoins = 650,
                    category = TaskCategory.OFFERWALL,
                    iconName = "OFFERWALL",
                    partnerName = "CPALead"
                ),
                TaskItem(
                    id = "task_ludo_tourney",
                    title = "Join Ludo Championship",
                    description = "Enter 50 coins pool and win 500 coins prize",
                    rewardCoins = 500,
                    category = TaskCategory.SPECIAL,
                    iconName = "GAMES",
                    isHot = true
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
                ScratchCard(title = "Diamond Strike", rewardCoins = 100, isScratched = false, cardIndex = 1),
                ScratchCard(title = "Golden Fortune", rewardCoins = 150, isScratched = false, cardIndex = 2),
                ScratchCard(title = "Lucky 777", rewardCoins = 250, isScratched = false, cardIndex = 3),
                ScratchCard(title = "Mega Jackpot", rewardCoins = 400, isScratched = false, cardIndex = 4),
                ScratchCard(title = "Crown Royale", rewardCoins = 200, isScratched = false, cardIndex = 5)
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

    suspend fun addGems(amount: Long, title: String, description: String = "") {
        val current = db.userDao().getUserStatsSync() ?: return
        val newGems = current.gems + amount
        db.userDao().update(current.copy(gems = newGems))
    }

    suspend fun advanceMegaOfferProgress(isSuper: Boolean = false): Triple<Int, Long, Long> {
        val current = db.userDao().getUserStatsSync() ?: return Triple(0, 0L, 0L)
        val currentProg = if (isSuper) current.superOfferProgress else current.megaOfferProgress
        val newProg = (currentProg + 1).coerceAtMost(25)
        val coinsEarned = if (isSuper) 75L else 50L
        val gemsEarned = 1L

        var updatedUser = if (isSuper) {
            current.copy(
                superOfferProgress = newProg,
                gems = current.gems + gemsEarned,
                coins = current.coins + coinsEarned,
                totalEarned = current.totalEarned + coinsEarned
            )
        } else {
            current.copy(
                megaOfferProgress = newProg,
                gems = current.gems + gemsEarned,
                coins = current.coins + coinsEarned,
                totalEarned = current.totalEarned + coinsEarned
            )
        }

        val offerName = if (isSuper) "Super Offer" else "Mega Offer"
        db.transactionDao().insert(
            TransactionRecord(
                title = "$offerName (Step $newProg/25)",
                type = if (isSuper) TransactionType.SUPER_OFFER_REWARD else TransactionType.MEGA_OFFER_REWARD,
                coins = coinsEarned,
                description = "Answered Q&A and watched ad! +$coinsEarned Coins & +1 Gem 💎"
            )
        )

        // Milestone reached at 25!
        var milestoneBonus = 0L
        if (newProg == 25) {
            milestoneBonus = if (isSuper) 5000L else 2500L
            updatedUser = updatedUser.copy(
                coins = updatedUser.coins + milestoneBonus,
                totalEarned = updatedUser.totalEarned + milestoneBonus
            )
            db.transactionDao().insert(
                TransactionRecord(
                    title = "🎉 25 Gems Milestone Grand Bonus!",
                    type = if (isSuper) TransactionType.SUPER_OFFER_REWARD else TransactionType.MEGA_OFFER_REWARD,
                    coins = milestoneBonus,
                    description = "Completed 25 steps in $offerName! Huge +$milestoneBonus Coins Credited!"
                )
            )
        }

        db.userDao().update(updatedUser)
        return Triple(newProg, coinsEarned + milestoneBonus, gemsEarned)
    }

    suspend fun convertGemsToCoins(gemAmount: Long): Pair<Boolean, String> {
        val current = db.userDao().getUserStatsSync() ?: return Pair(false, "User not found")
        if (current.gems < gemAmount || gemAmount <= 0) {
            return Pair(false, "Insufficient gems! You have ${current.gems} Gems.")
        }
        val coinsToCredit = gemAmount * 100L // 1 Gem = 100 Coins (25 Gems = 2,500 Coins)
        val newGems = current.gems - gemAmount
        val newCoins = current.coins + coinsToCredit
        val newTotal = current.totalEarned + coinsToCredit

        db.userDao().update(current.copy(gems = newGems, coins = newCoins, totalEarned = newTotal))
        db.transactionDao().insert(
            TransactionRecord(
                title = "Gem Exchange ($gemAmount 💎 -> $coinsToCredit Coins)",
                type = TransactionType.MEGA_OFFER_REWARD,
                coins = coinsToCredit,
                description = "Converted $gemAmount Gems into +$coinsToCredit Coins!"
            )
        )
        return Pair(true, "Successfully exchanged $gemAmount Gems for +$coinsToCredit Coins!")
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
        val streakRewards = listOf(100L, 150L, 200L, 250L, 350L, 500L, 1000L)
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
                title = "Day $streak Streak Reward 🔥",
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

        val bonus = 500L
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
                title = "Referral Friend Bonus 🎁",
                type = TransactionType.REFERRAL_REWARD,
                coins = bonus,
                description = "Applied referral code: ${enteredCode.trim().uppercase()} (+500 Coins bonus)"
            )
        )

        return Pair(true, "Successfully claimed 500 Bonus Coins!")
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
                title = "Friend Joined via Referral 👥",
                type = TransactionType.REFERRAL_INVITE,
                coins = bonus,
                description = "Your friend joined using your code! (+500 Coins)"
            )
        )

        return bonus
    }

    suspend fun joinTournament(tournamentId: String, title: String, entryFee: Long): Pair<Boolean, String> {
        val current = db.userDao().getUserStatsSync() ?: return Pair(false, "User not found")
        if (current.coins < entryFee) {
            return Pair(false, "Insufficient coins balance! Entry requires $entryFee coins.")
        }
        val newCoins = current.coins - entryFee
        db.userDao().update(current.copy(coins = newCoins))

        db.transactionDao().insert(
            TransactionRecord(
                title = "Joined $title Tournament ⚔️",
                type = TransactionType.TOURNAMENT_WIN,
                coins = -entryFee,
                description = "Entry fee paid for $title. Match scheduled soon!"
            )
        )
        return Pair(true, "Successfully joined $title! Room details updated.")
    }

    suspend fun requestWithdrawal(
        method: String,
        coins: Long,
        amountFormatted: String,
        destination: String,
        accountHolderName: String = "",
        ifscCode: String = ""
    ): Pair<Boolean, String> {
        val current = db.userDao().getUserStatsSync() ?: return Pair(false, "User not found")
        if (current.coins < coins) {
            return Pair(false, "Insufficient coins balance! You have ${current.coins} coins.")
        }
        if (destination.isBlank()) {
            return Pair(false, "Please enter a valid payout account/ID or email.")
        }

        val newCoins = current.coins - coins
        val newWithdrawn = current.totalWithdrawn + coins

        db.userDao().update(current.copy(coins = newCoins, totalWithdrawn = newWithdrawn))

        val recordId = db.withdrawalDao().insert(
            WithdrawalRecord(
                method = method,
                coinsDeducted = coins,
                amountFormatted = amountFormatted,
                destinationAccount = destination,
                accountHolderName = accountHolderName,
                ifscCode = ifscCode,
                status = "PENDING"
            )
        )

        db.transactionDao().insert(
            TransactionRecord(
                title = "Redeem $amountFormatted via $method",
                type = TransactionType.WITHDRAWAL,
                coins = -coins,
                status = "PENDING",
                description = "Redeem request #$recordId sent for $destination. Under Admin Review."
            )
        )

        return Pair(true, "Withdrawal request of $amountFormatted submitted successfully!")
    }

    // ==================== ADMIN PANEL CONTROLS ====================

    suspend fun approveWithdrawalWithVoucher(
        withdrawalId: Long,
        voucherCode: String? = null,
        utrReference: String? = null,
        adminNote: String? = null
    ): Pair<Boolean, String> {
        val withdrawal = db.withdrawalDao().getWithdrawalById(withdrawalId)
            ?: return Pair(false, "Withdrawal request not found")

        val updatedRecord = withdrawal.copy(
            status = "APPROVED",
            voucherCode = voucherCode?.takeIf { it.isNotBlank() },
            utrReference = utrReference?.takeIf { it.isNotBlank() },
            adminNote = adminNote?.takeIf { it.isNotBlank() }
        )
        db.withdrawalDao().update(updatedRecord)

        val codeDesc = when {
            !voucherCode.isNullOrBlank() -> "Voucher Code: $voucherCode"
            !utrReference.isNullOrBlank() -> "UTR / Ref: $utrReference"
            else -> "Payment sent successfully"
        }

        db.transactionDao().insert(
            TransactionRecord(
                title = "Withdrawal Approved: ${withdrawal.amountFormatted} ✅",
                type = TransactionType.WITHDRAWAL,
                coins = 0L,
                status = "COMPLETED",
                description = "Admin approved payout for ${withdrawal.method}. $codeDesc",
                voucherCode = voucherCode
            )
        )
        return Pair(true, "Withdrawal #${withdrawalId} APPROVED! Voucher/UTR saved for user.")
    }

    suspend fun rejectWithdrawal(withdrawalId: Long, reason: String): Pair<Boolean, String> {
        val withdrawal = db.withdrawalDao().getWithdrawalById(withdrawalId)
            ?: return Pair(false, "Withdrawal request not found")

        if (withdrawal.status == "REJECTED") {
            return Pair(false, "This withdrawal request is already rejected.")
        }

        db.withdrawalDao().update(withdrawal.copy(status = "REJECTED", adminNote = reason))

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

        db.withdrawalDao().update(withdrawal.copy(status = "UNDER_REVIEW"))
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

    suspend fun adminAdjustUserGems(deltaGems: Long, reason: String): Pair<Boolean, String> {
        val current = db.userDao().getUserStatsSync() ?: return Pair(false, "User not found")
        val updatedGems = (current.gems + deltaGems).coerceAtLeast(0)
        db.userDao().update(current.copy(gems = updatedGems))
        return Pair(true, "Successfully updated Gems balance! New Gems: $updatedGems 💎")
    }

    suspend fun adminResetDailyLimits(newSpins: Int = 10, newCards: Int = 5): Pair<Boolean, String> {
        val current = db.userDao().getUserStatsSync() ?: return Pair(false, "User not found")
        db.userDao().update(current.copy(dailySpinsLeft = newSpins, scratchCardsLeft = newCards, megaOfferProgress = 0, superOfferProgress = 0))
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
