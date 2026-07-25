package com.spelltype.keyboard.presentation.ads

import android.content.Context
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.spelltype.keyboard.BuildConfig

enum class BannerType { KEYBOARD_TOP, KEYBOARD_BOTTOM }
enum class InterstitialType { SETTINGS, PRO_TOOLS }

object AdManager {

    private var initialized = false

    fun init(context: Context) {
        if (initialized) return
        try {
            MobileAds.initialize(context) {
                initialized = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Resolve dynamic Ad ID based on BuildConfig flag
    private fun getAdUnitId(realId: String, testId: String): String {
        return if (BuildConfig.DEBUG) testId else realId
    }

    fun getBannerId(type: BannerType): String {
        return when (type) {
            BannerType.KEYBOARD_TOP -> getAdUnitId(
                realId = "ca-app-pub-8178045957849630/7446171329",
                testId = "ca-app-pub-3940256099942544/6300978111"
            )
            BannerType.KEYBOARD_BOTTOM -> getAdUnitId(
                realId = "ca-app-pub-8178045957849630/7941709351",
                testId = "ca-app-pub-3940256099942544/6300978111"
            )
        }
    }

    fun getInterstitialId(type: InterstitialType): String {
        return when (type) {
            InterstitialType.SETTINGS -> getAdUnitId(
                realId = "ca-app-pub-8178045957849630/8996036658",
                testId = "ca-app-pub-3940256099942544/1033173712"
            )
            InterstitialType.PRO_TOOLS -> getAdUnitId(
                realId = "ca-app-pub-8178045957849630/1117546633",
                testId = "ca-app-pub-3940256099942544/1033173712"
            )
        }
    }

    fun getRewardedId(): String {
        return getAdUnitId(
            realId = "ca-app-pub-8178045957849630/6002061869",
            testId = "ca-app-pub-3940256099942544/5224354917"
        )
    }

    fun getNativeId(): String {
        return getAdUnitId(
            realId = "ca-app-pub-8178045957849630/3743709978",
            testId = "ca-app-pub-3940256099942544/2247696110"
        )
    }

    // 1. Loading Banner Ads
    fun loadBanner(context: Context, type: BannerType, adSize: AdSize, onLoaded: (AdView) -> Unit) {
        try {
            val adView = AdView(context)
            adView.adUnitId = getBannerId(type)
            adView.setAdSize(adSize)
            adView.adListener = object : AdListener() {
                override fun onAdLoaded() {
                    onLoaded(adView)
                }
            }
            adView.loadAd(AdRequest.Builder().build())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 2. Loading Interstitial Ads
    fun loadInterstitial(
        context: Context,
        type: InterstitialType,
        onLoaded: (InterstitialAd) -> Unit,
        onFailed: (() -> Unit)? = null
    ) {
        try {
            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(
                context,
                getInterstitialId(type),
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        onLoaded(interstitialAd)
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        onFailed?.invoke()
                    }
                }
            )
        } catch (e: Exception) {
            e.printStackTrace()
            onFailed?.invoke()
        }
    }

    // 3. Loading Rewarded Ads
    fun loadRewarded(
        context: Context,
        onLoaded: (RewardedAd) -> Unit,
        onFailed: (() -> Unit)? = null
    ) {
        try {
            val adRequest = AdRequest.Builder().build()
            RewardedAd.load(
                context,
                getRewardedId(),
                adRequest,
                object : RewardedAdLoadCallback() {
                    override fun onAdLoaded(rewardedAd: RewardedAd) {
                        onLoaded(rewardedAd)
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        onFailed?.invoke()
                    }
                }
            )
        } catch (e: Exception) {
            e.printStackTrace()
            onFailed?.invoke()
        }
    }

    // 4. Loading Native Ads
    fun loadNativeAd(
        context: Context,
        onLoaded: (NativeAd) -> Unit,
        onFailed: (() -> Unit)? = null
    ) {
        try {
            val adLoader = AdLoader.Builder(context, getNativeId())
                .forNativeAd { nativeAd ->
                    onLoaded(nativeAd)
                }
                .withAdListener(object : AdListener() {
                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        onFailed?.invoke()
                    }
                })
                .build()
            adLoader.loadAd(AdRequest.Builder().build())
        } catch (e: Exception) {
            e.printStackTrace()
            onFailed?.invoke()
        }
    }
}
