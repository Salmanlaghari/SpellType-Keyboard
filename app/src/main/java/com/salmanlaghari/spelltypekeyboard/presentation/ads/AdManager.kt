package com.salmanlaghari.spelltypekeyboard.presentation.ads

import android.content.Context
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.nativead.NativeAd
import com.salmanlaghari.spelltypekeyboard.BuildConfig
import com.salmanlaghari.spelltypekeyboard.core.AppLog

enum class BannerType { KEYBOARD_TOP, KEYBOARD_BOTTOM, HOME, SETTINGS }
enum class InterstitialType { SETTINGS, PRO_TOOLS, APP_OPEN, EXIT }
enum class RewardedType { VIDEO, COIN, KEYBOARD }

object AdManager {

    private var initialized = false

    // ═══ NEW PRODUCTION ADMOB IDS ═══
    // App ID: ca-app-pub-8178045957849630~2823451917

    fun init(context: Context) {
        if (initialized) return
        try {
            MobileAds.initialize(context) {
                initialized = true
            }
        } catch (e: Exception) {
            AppLog.e("AdManager.init", e)
        }
    }

    private fun getAdUnitId(realId: String, testId: String): String {
        return if (BuildConfig.DEBUG) testId else realId
    }

    // ═══════════════════════════════════════
    //  BANNER ADS — ca-app-pub-8178045957849630/5258043569
    // ═══════════════════════════════════════

    fun getBannerId(type: BannerType): String {
        return when (type) {
            BannerType.KEYBOARD_TOP, BannerType.KEYBOARD_BOTTOM,
            BannerType.HOME, BannerType.SETTINGS -> getAdUnitId(
                realId = "ca-app-pub-8178045957849630/5258043569",
                testId = "ca-app-pub-3940256099942544/6300978111"
            )
        }
    }

    // ═══════════════════════════════════════
    //  INTERSTITIAL ADS — ca-app-pub-8178045957849630/7309491830
    // ═══════════════════════════════════════

    fun getInterstitialId(type: InterstitialType): String {
        return when (type) {
            InterstitialType.SETTINGS, InterstitialType.PRO_TOOLS,
            InterstitialType.APP_OPEN, InterstitialType.EXIT -> getAdUnitId(
                realId = "ca-app-pub-8178045957849630/7309491830",
                testId = "ca-app-pub-3940256099942544/1033173712"
            )
        }
    }

    // ═══════════════════════════════════════
    //  REWARDED ADS
    // ═══════════════════════════════════════

    fun getRewardedId(type: RewardedType = RewardedType.VIDEO): String {
        return when (type) {
            RewardedType.VIDEO, RewardedType.COIN -> getAdUnitId(
                realId = "ca-app-pub-8178045957849630/1865593464",
                testId = "ca-app-pub-3940256099942544/5224354917"
            )
            RewardedType.KEYBOARD -> getAdUnitId(
                realId = "ca-app-pub-8178045957849630/3066994497",
                testId = "ca-app-pub-3940256099942544/5224354917"
            )
        }
    }

    // ═══════════════════════════════════════
    //  REWARDED INTERSTITIAL — ca-app-pub-8178045957849630/8866116084
    // ═══════════════════════════════════════

    fun getRewardedInterstitialId(): String {
        return getAdUnitId(
            realId = "ca-app-pub-8178045957849630/8866116084",
            testId = "ca-app-pub-3940256099942544/5224354917"
        )
    }

    // ═══════════════════════════════════════
    //  NATIVE ADS — ca-app-pub-8178045957849630/4024852945
    // ═══════════════════════════════════════

    fun getNativeId(): String {
        return getAdUnitId(
            realId = "ca-app-pub-8178045957849630/4024852945",
            testId = "ca-app-pub-3940256099942544/2247696110"
        )
    }

    // ═══════════════════════════════════════
    //  APP OPEN ADS — ca-app-pub-8178045957849630/2081215889
    // ═══════════════════════════════════════

    fun getAppOpenId(): String {
        return getAdUnitId(
            realId = "ca-app-pub-8178045957849630/2081215889",
            testId = "ca-app-pub-3940256099942544/3419835294"
        )
    }

    // ═══════════════════════════════════════
    //  AD LOADING METHODS
    // ═══════════════════════════════════════

    fun loadBanner(
        context: Context,
        type: BannerType,
        adSize: AdSize,
        onFailed: (() -> Unit)? = null,
        onLoaded: (AdView) -> Unit
    ) {
        try {
            val adView = AdView(context)
            adView.adUnitId = getBannerId(type)
            adView.setAdSize(adSize)
            adView.adListener = object : AdListener() {
                override fun onAdLoaded() {
                    onLoaded(adView)
                }
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    AppLog.e("AdManager.loadBanner($type)", loadAdError.message)
                    onFailed?.invoke()
                }
            }
            adView.loadAd(AdRequest.Builder().build())
        } catch (e: Exception) {
            AppLog.e("AdManager.loadBanner", e)
            onFailed?.invoke()
        }
    }

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
                        AppLog.e("AdManager.loadInterstitial($type)", loadAdError.message)
                        onFailed?.invoke()
                    }
                }
            )
        } catch (e: Exception) {
            AppLog.e("AdManager.loadInterstitial", e)
            onFailed?.invoke()
        }
    }

    fun loadRewarded(
        context: Context,
        type: RewardedType = RewardedType.VIDEO,
        onLoaded: (RewardedAd) -> Unit,
        onFailed: (() -> Unit)? = null
    ) {
        try {
            val adRequest = AdRequest.Builder().build()
            RewardedAd.load(
                context,
                getRewardedId(type),
                adRequest,
                object : RewardedAdLoadCallback() {
                    override fun onAdLoaded(rewardedAd: RewardedAd) {
                        onLoaded(rewardedAd)
                    }
                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        AppLog.e("AdManager.loadRewarded($type)", loadAdError.message)
                        onFailed?.invoke()
                    }
                }
            )
        } catch (e: Exception) {
            AppLog.e("AdManager.loadRewarded", e)
            onFailed?.invoke()
        }
    }

    fun loadRewardedInterstitial(
        context: Context,
        onLoaded: (RewardedInterstitialAd) -> Unit,
        onFailed: (() -> Unit)? = null
    ) {
        try {
            val adRequest = AdRequest.Builder().build()
            RewardedInterstitialAd.load(
                context,
                getRewardedInterstitialId(),
                adRequest,
                object : RewardedInterstitialAdLoadCallback() {
                    override fun onAdLoaded(ad: RewardedInterstitialAd) {
                        onLoaded(ad)
                    }
                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        AppLog.e("AdManager.loadRewardedInterstitial", loadAdError.message)
                        onFailed?.invoke()
                    }
                }
            )
        } catch (e: Exception) {
            AppLog.e("AdManager.loadRewardedInterstitial", e)
            onFailed?.invoke()
        }
    }

    fun loadAppOpen(
        context: Context,
        onLoaded: (AppOpenAd) -> Unit,
        onFailed: (() -> Unit)? = null
    ) {
        try {
            val adRequest = AdRequest.Builder().build()
            AppOpenAd.load(
                context,
                getAppOpenId(),
                adRequest,
                object : AppOpenAd.AppOpenAdLoadCallback() {
                    override fun onAdLoaded(ad: AppOpenAd) {
                        onLoaded(ad)
                    }
                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        AppLog.e("AdManager.loadAppOpen", loadAdError.message)
                        onFailed?.invoke()
                    }
                }
            )
        } catch (e: Exception) {
            AppLog.e("AdManager.loadAppOpen", e)
            onFailed?.invoke()
        }
    }

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
                        AppLog.e("AdManager.loadNativeAd", loadAdError.message)
                        onFailed?.invoke()
                    }
                })
                .build()
            adLoader.loadAd(AdRequest.Builder().build())
        } catch (e: Exception) {
            AppLog.e("AdManager.loadNativeAd", e)
            onFailed?.invoke()
        }
    }
}
