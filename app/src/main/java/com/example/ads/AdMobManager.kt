package com.example.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

object AdMobManager {
    private const val TAG = "AdMobManager"

    // Primary Production Ad Unit IDs provided by user
    const val WATCH_AND_EARN_REWARDED_AD_UNIT_ID = "ca-app-pub-1601992247643052/8378590953"
    const val GAME_OVER_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-1601992247643052/2384912724"

    // Google AdMob Standard Test IDs as seamless fallback if production ad not filled yet
    private const val TEST_REWARDED_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"
    private const val TEST_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"

    private var isInitialized = false
    private var rewardedAd: RewardedAd? = null
    private var interstitialAd: InterstitialAd? = null

    fun initialize(context: Context) {
        if (isInitialized) return
        try {
            MobileAds.initialize(context) { status ->
                Log.d(TAG, "Google Mobile Ads SDK Initialized: ${status.adapterStatusMap}")
                isInitialized = true
            }
        } catch (e: Throwable) {
            Log.e(TAG, "Error initializing MobileAds", e)
        }
    }

    /**
     * Loads and displays a Rewarded Video Ad for Watch & Earn.
     */
    fun showRewardedAd(
        activity: Activity,
        rewardCoins: Long = 100L,
        onRewardEarned: (Long) -> Unit,
        onAdClosed: () -> Unit,
        onAdFailed: (String) -> Unit
    ) {
        try {
            val adRequest = AdRequest.Builder().build()
            Log.d(TAG, "Loading Rewarded Ad with ID: $WATCH_AND_EARN_REWARDED_AD_UNIT_ID")

            RewardedAd.load(
                activity,
                WATCH_AND_EARN_REWARDED_AD_UNIT_ID,
                adRequest,
                object : RewardedAdLoadCallback() {
                    override fun onAdLoaded(ad: RewardedAd) {
                        Log.d(TAG, "Rewarded Ad loaded successfully")
                        rewardedAd = ad
                        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                Log.d(TAG, "Rewarded Ad dismissed")
                                rewardedAd = null
                                onAdClosed()
                            }

                            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                Log.e(TAG, "Rewarded Ad failed to show: ${adError.message}")
                                rewardedAd = null
                                onAdFailed(adError.message)
                            }

                            override fun onAdShowedFullScreenContent() {
                                Log.d(TAG, "Rewarded Ad showing full screen")
                            }
                        }

                        ad.show(activity) { rewardItem ->
                            val coins = if (rewardItem.amount > 0) rewardItem.amount.toLong() else rewardCoins
                            Log.d(TAG, "User earned reward: $coins coins")
                            onRewardEarned(coins)
                        }
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        Log.w(TAG, "Primary Rewarded Ad failed to load: ${loadAdError.message}. Trying test ad fallback...")
                        try {
                            RewardedAd.load(
                                activity,
                                TEST_REWARDED_AD_UNIT_ID,
                                adRequest,
                                object : RewardedAdLoadCallback() {
                                    override fun onAdLoaded(testAd: RewardedAd) {
                                        testAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                                            override fun onAdDismissedFullScreenContent() {
                                                onAdClosed()
                                            }
                                            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                                onAdFailed(adError.message)
                                            }
                                        }
                                        testAd.show(activity) {
                                            onRewardEarned(rewardCoins)
                                        }
                                    }

                                    override fun onAdFailedToLoad(error: LoadAdError) {
                                        Log.e(TAG, "Rewarded test ad also failed: ${error.message}")
                                        onRewardEarned(rewardCoins)
                                        onAdClosed()
                                    }
                                }
                            )
                        } catch (e: Throwable) {
                            Log.e(TAG, "Fallback rewarded load failed", e)
                            onRewardEarned(rewardCoins)
                            onAdClosed()
                        }
                    }
                }
            )
        } catch (e: Throwable) {
            Log.e(TAG, "Error invoking showRewardedAd", e)
            onRewardEarned(rewardCoins)
            onAdClosed()
        }
    }

    /**
     * Loads and displays an Interstitial Ad on Game Over / Game completions.
     */
    fun showGameOverAd(
        activity: Activity,
        onAdClosed: () -> Unit = {}
    ) {
        try {
            val adRequest = AdRequest.Builder().build()
            Log.d(TAG, "Loading Game Over Interstitial Ad with ID: $GAME_OVER_INTERSTITIAL_AD_UNIT_ID")

            InterstitialAd.load(
                activity,
                GAME_OVER_INTERSTITIAL_AD_UNIT_ID,
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(ad: InterstitialAd) {
                        Log.d(TAG, "Interstitial Ad loaded")
                        interstitialAd = ad
                        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                Log.d(TAG, "Interstitial Ad dismissed")
                                interstitialAd = null
                                onAdClosed()
                            }

                            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                Log.e(TAG, "Interstitial failed to show: ${adError.message}")
                                interstitialAd = null
                                onAdClosed()
                            }
                        }
                        ad.show(activity)
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        Log.w(TAG, "Primary Interstitial Ad failed: ${loadAdError.message}. Trying test ad...")
                        try {
                            InterstitialAd.load(
                                activity,
                                TEST_INTERSTITIAL_AD_UNIT_ID,
                                adRequest,
                                object : InterstitialAdLoadCallback() {
                                    override fun onAdLoaded(testAd: InterstitialAd) {
                                        testAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                                            override fun onAdDismissedFullScreenContent() {
                                                onAdClosed()
                                            }
                                            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                                onAdClosed()
                                            }
                                        }
                                        testAd.show(activity)
                                    }

                                    override fun onAdFailedToLoad(error: LoadAdError) {
                                        Log.d(TAG, "Test Interstitial ad failed: ${error.message}")
                                        onAdClosed()
                                    }
                                }
                            )
                        } catch (e: Throwable) {
                            Log.e(TAG, "Fallback interstitial load failed", e)
                            onAdClosed()
                        }
                    }
                }
            )
        } catch (e: Throwable) {
            Log.e(TAG, "Error invoking showGameOverAd", e)
            onAdClosed()
        }
    }
}
