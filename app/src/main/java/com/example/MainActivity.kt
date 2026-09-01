package com.example

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ads.AdMobManager
import com.example.ui.components.CelebrationDialog
import com.example.ui.components.GemExchangeDialog
import com.example.ui.components.MegaOfferChallengeDialog
import com.example.ui.components.OfferwallPartnerDetailDialog
import com.example.ui.components.RewardBottomNavBar
import com.example.ui.components.RewardTopBar
import com.example.ui.screens.AdminPanelScreen
import com.example.ui.screens.GamesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ReferralScreen
import com.example.ui.screens.SpinWheelScreen
import com.example.ui.screens.TasksScreen
import com.example.ui.screens.TournamentsScreen
import com.example.ui.screens.WalletScreen
import com.example.ui.screens.WatchEarnScreen
import com.example.ui.theme.RewardCashTheme
import com.example.ui.viewmodel.ActiveGame
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.RewardViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Google AdMob Mobile Ads SDK
        AdMobManager.initialize(this)

        setContent {
            RewardCashTheme {
                RewardAppContent()
            }
        }
    }
}

@Composable
fun RewardAppContent(
    viewModel: RewardViewModel = viewModel()
) {
    val context = LocalContext.current
    val activity = context as? Activity

    val userStats by viewModel.userStats.collectAsStateWithLifecycle()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val scratchCards by viewModel.scratchCards.collectAsStateWithLifecycle()
    val withdrawals by viewModel.withdrawals.collectAsStateWithLifecycle()
    val tournaments by viewModel.tournaments.collectAsStateWithLifecycle()
    val offerwalls by viewModel.offerwalls.collectAsStateWithLifecycle()

    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val activeGame by viewModel.activeGame.collectAsStateWithLifecycle()

    val mathSprintState by viewModel.mathSprintState.collectAsStateWithLifecycle()
    val luckyNumberState by viewModel.luckyNumberState.collectAsStateWithLifecycle()
    val memoryGameState by viewModel.memoryGameState.collectAsStateWithLifecycle()
    val tapRushState by viewModel.tapRushState.collectAsStateWithLifecycle()

    val spinAngle by viewModel.spinAngle.collectAsStateWithLifecycle()
    val isSpinning by viewModel.isSpinning.collectAsStateWithLifecycle()

    val videoList by viewModel.videoList.collectAsStateWithLifecycle()
    val activeVideo by viewModel.activeVideo.collectAsStateWithLifecycle()
    val videoProgress by viewModel.videoProgress.collectAsStateWithLifecycle()
    val isVideoPlaying by viewModel.isVideoPlaying.collectAsStateWithLifecycle()
    val videoClaimable by viewModel.isVideoClaimable.collectAsStateWithLifecycle()

    val isMegaOfferDialogVisible by viewModel.isMegaOfferDialogVisible.collectAsStateWithLifecycle()
    val isSuperOfferMode by viewModel.isSuperOfferMode.collectAsStateWithLifecycle()
    val activeOfferwallDetail by viewModel.activeOfferwallDetail.collectAsStateWithLifecycle()
    val isGemExchangeVisible by viewModel.isGemExchangeVisible.collectAsStateWithLifecycle()

    val celebration by viewModel.celebration.collectAsStateWithLifecycle()
    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearUserMessage()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (activeGame == ActiveGame.NONE && currentTab != AppTab.ADMIN) {
                RewardTopBar(
                    coins = userStats?.coins ?: 500L,
                    gems = userStats?.gems ?: 0L,
                    streakDays = userStats?.streakDays ?: 1,
                    onWalletClick = { viewModel.selectTab(AppTab.WALLET) },
                    onGemsClick = { viewModel.showGemExchangeDialog() },
                    onStreakClick = { viewModel.claimDailyCheckIn() },
                    onAdminClick = { viewModel.selectTab(AppTab.ADMIN) }
                )
            }
        },
        bottomBar = {
            if (activeGame == ActiveGame.NONE && currentTab != AppTab.ADMIN) {
                RewardBottomNavBar(
                    selectedTab = currentTab,
                    onTabSelected = { viewModel.selectTab(it) }
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                AppTab.HOME -> {
                    HomeScreen(
                        userStats = userStats,
                        onNavigateTab = { viewModel.selectTab(it) },
                        onOpenGame = {
                            viewModel.selectTab(AppTab.GAMES)
                            viewModel.openGame(it)
                        },
                        onOpenMegaOffer = { isSuper ->
                            viewModel.openMegaOffer(isSuper)
                        },
                        onOpenGemExchange = {
                            viewModel.showGemExchangeDialog()
                        },
                        onClaimCheckIn = { viewModel.claimDailyCheckIn() }
                    )
                }
                AppTab.TOURNAMENTS -> {
                    TournamentsScreen(
                        userStats = userStats,
                        tournaments = tournaments,
                        onJoinTournament = { t -> viewModel.joinTournament(t) }
                    )
                }
                AppTab.GAMES -> {
                    GamesScreen(
                        activeGame = activeGame,
                        mathState = mathSprintState,
                        luckyNumberState = luckyNumberState,
                        memoryState = memoryGameState,
                        tapRushState = tapRushState,
                        onOpenGame = { viewModel.openGame(it) },
                        onCloseGame = {
                            if (activity != null) {
                                AdMobManager.showGameOverAd(activity)
                            }
                            viewModel.closeGame()
                        },
                        onAnswerMath = { viewModel.answerMathQuestion(it) },
                        onGuessChange = { viewModel.onGuessInputChanged(it) },
                        onSubmitGuess = { viewModel.submitGuess() },
                        onRestartLuckyNumber = {
                            if (activity != null) {
                                AdMobManager.showGameOverAd(activity)
                            }
                            viewModel.restartLuckyNumber()
                        },
                        onFlipCard = { viewModel.flipCard(it) },
                        onRestartMemory = {
                            if (activity != null) {
                                AdMobManager.showGameOverAd(activity)
                            }
                            viewModel.restartMemoryGame()
                        },
                        onCoinTapped = { viewModel.onCoinTapped(it) },
                        onRestartTapRush = {
                            if (activity != null) {
                                AdMobManager.showGameOverAd(activity)
                            }
                            viewModel.restartTapRush()
                        }
                    )
                }
                AppTab.SPIN -> {
                    SpinWheelScreen(
                        dailySpinsLeft = userStats?.dailySpinsLeft ?: 10,
                        isSpinning = isSpinning,
                        spinAngle = spinAngle,
                        onSpinClick = { viewModel.spinTheWheel() },
                        onRefillSpinsClick = {
                            if (activity != null) {
                                AdMobManager.showRewardedAd(
                                    activity = activity,
                                    rewardCoins = 150L,
                                    onRewardEarned = {
                                        viewModel.refillSpinsWithVideo()
                                    },
                                    onAdClosed = {},
                                    onAdFailed = {
                                        viewModel.refillSpinsWithVideo()
                                    }
                                )
                            } else {
                                viewModel.refillSpinsWithVideo()
                            }
                        }
                    )
                }
                AppTab.WATCH -> {
                    WatchEarnScreen(
                        videoList = videoList,
                        activeVideo = activeVideo,
                        videoProgress = videoProgress,
                        isVideoPlaying = isVideoPlaying,
                        isVideoClaimable = videoClaimable,
                        onStartVideo = { viewModel.startWatchingVideo(it) },
                        onWatchAdMobAd = {
                            if (activity != null) {
                                AdMobManager.showRewardedAd(
                                    activity = activity,
                                    rewardCoins = 100L,
                                    onRewardEarned = { coins ->
                                        viewModel.awardAdMobVideoReward(coins)
                                    },
                                    onAdClosed = {},
                                    onAdFailed = {
                                        viewModel.awardAdMobVideoReward(100L)
                                    }
                                )
                            }
                        },
                        onTogglePlayPause = { viewModel.toggleVideoPlayPause() },
                        onClaimReward = { viewModel.claimVideoReward() },
                        onClosePlayer = { viewModel.closeVideoPlayer() }
                    )
                }
                AppTab.TASKS -> {
                    TasksScreen(
                        tasks = tasks,
                        scratchCards = scratchCards,
                        offerwalls = offerwalls,
                        onCompleteTask = { viewModel.completeTask(it) },
                        onScratchCard = { viewModel.scratchCard(it) },
                        onOpenOfferwallDetail = { viewModel.openOfferwallDetail(it) },
                        onOpenMegaOffer = { isSuper -> viewModel.openMegaOffer(isSuper) }
                    )
                }
                AppTab.REFERRAL -> {
                    ReferralScreen(
                        userStats = userStats,
                        onApplyCode = { viewModel.applyReferralCode(it) },
                        onSimulateFriendJoined = { viewModel.simulateFriendJoined() }
                    )
                }
                AppTab.WALLET -> {
                    WalletScreen(
                        userStats = userStats,
                        transactions = transactions,
                        withdrawals = withdrawals,
                        onRequestWithdrawal = { method, coins, amount, dest, holder, ifsc ->
                            viewModel.requestWithdrawal(method, coins, amount, dest, holder, ifsc)
                        }
                    )
                }
                AppTab.ADMIN -> {
                    AdminPanelScreen(
                        userStats = userStats,
                        withdrawals = withdrawals,
                        onApproveWithdrawal = { id, voucher, utr, note ->
                            viewModel.approveWithdrawal(id, voucher, utr, note)
                        },
                        onRejectWithdrawal = { id, reason ->
                            viewModel.rejectWithdrawal(id, reason)
                        },
                        onMarkUnderReview = { id ->
                            viewModel.markWithdrawalUnderReview(id)
                        },
                        onAdjustCoins = { delta, reason ->
                            viewModel.adminAdjustCoins(delta, reason)
                        },
                        onAdjustGems = { delta, reason ->
                            viewModel.adminAdjustGems(delta, reason)
                        },
                        onResetLimits = { spins, cards ->
                            viewModel.adminResetLimits(spins, cards)
                        },
                        onUpdateReferralCode = { code ->
                            viewModel.adminUpdateReferralCode(code)
                        },
                        onExitAdmin = { viewModel.selectTab(AppTab.HOME) }
                    )
                }
            }
        }
    }

    // 💎 Mega & Super Offer Progression Dialog
    if (isMegaOfferDialogVisible) {
        val currentProgress = if (isSuperOfferMode) (userStats?.superOfferProgress ?: 0) else (userStats?.megaOfferProgress ?: 0)
        MegaOfferChallengeDialog(
            isSuperOffer = isSuperOfferMode,
            currentProgress = currentProgress,
            onStepAnswered = {
                viewModel.progressMegaOfferStep()
            },
            onDismiss = { viewModel.closeMegaOffer() }
        )
    }

    // 🌐 Offerwall Detail Dialog
    activeOfferwallDetail?.let { partner ->
        OfferwallPartnerDetailDialog(
            partner = partner,
            onActionTask = { actionTitle, coinsReward ->
                viewModel.completeOfferwallAction(actionTitle, coinsReward)
            },
            onDismiss = { viewModel.closeOfferwallDetail() }
        )
    }

    // 💎 Gem Exchange Dialog
    if (isGemExchangeVisible) {
        GemExchangeDialog(
            currentGems = userStats?.gems ?: 0L,
            onConvertGems = { gems, coins ->
                viewModel.convertGemsToCoins(gems, coins)
            },
            onDismiss = { viewModel.closeGemExchangeDialog() }
        )
    }

    // Celebration Dialog when coins are earned
    CelebrationDialog(
        celebration = celebration,
        onDismiss = { viewModel.dismissCelebration() }
    )
}
