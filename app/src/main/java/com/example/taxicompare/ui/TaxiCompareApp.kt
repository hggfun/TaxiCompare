package com.example.taxicompare.ui

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.compose.foundation.Image
import com.example.taxicompare.ui.theme.TaxiCompareTheme
import com.example.taxicompare.R


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.Room
import com.example.taxicompare.cache.AppDatabase
import com.example.taxicompare.cache.PricePredictionRepository
import com.example.taxicompare.navigation.NavigationControllerSetup
import com.yandex.mapkit.MapKitFactory
import com.yandex.mobile.ads.common.AdError
import com.yandex.mobile.ads.common.AdRequestConfiguration
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.rewarded.Reward
import com.yandex.mobile.ads.rewarded.RewardedAd
import com.yandex.mobile.ads.rewarded.RewardedAdLoader
import com.yandex.mobile.ads.rewarded.RewardedAdLoadListener
import com.yandex.mobile.ads.rewarded.RewardedAdEventListener

@Composable
fun TaxiCompareApp(appDatabase: AppDatabase) {
    NavigationControllerSetup(appDatabase)
}

class App : Application() {
    lateinit var appDatabase: AppDatabase
        private set
    internal var mainActivity: MainActivity? = null

    internal var rewardedAd: RewardedAd? = null
    internal var rewardedAdLoader: RewardedAdLoader? = null

    override fun onCreate() {
        super.onCreate()

        appDatabase = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "database"
        ).build()

        rewardedAdLoader = RewardedAdLoader(this).apply {
            setAdLoadListener(object: RewardedAdLoadListener {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                }

                override fun onAdFailedToLoad(adRequestError: AdRequestError) {
                }
            })
        }
        loadRewardedAd()
    }

    private fun loadRewardedAd() {
//        val adRequestConfiguration = AdRequestConfiguration.Builder("R-M-15092512-2").build()
        val adRequestConfiguration = AdRequestConfiguration.Builder("demo-rewarded-yandex").build()
        rewardedAdLoader?.loadAd(adRequestConfiguration)
    }

    fun showAd(onRewarded: () -> Unit) {
        rewardedAd?.apply {
            setAdEventListener(object: RewardedAdEventListener {
                override fun onAdShown() {
                    Log.v("Bober show", "shown")
                    // Called when ad is shown.
                }

                override fun onAdFailedToShow(adError: AdError) {
                    Log.v("Bober show", "failed")
                    // Called when an RewardedAd failed to show

                    // Clean resources after Ad failed to show
                    destroyRewardedAd()

                    // Now you can preload the next rewarded ad.
                    loadRewardedAd()
                }

                override fun onAdDismissed() {
                    Log.v("Bober show", "dismissed")
                    // Called when ad is dismissed.
                    // Clean resources after Ad dismissed
                    destroyRewardedAd()

                    // Now you can preload the next rewarded ad.
                    loadRewardedAd()
                }

                override fun onAdClicked() {
                    Log.v("Bober show", "clicked")
                    // Called when a click is recorded for an ad.
                }

                override fun onAdImpression(impressionData: ImpressionData?) {
                    Log.v("Bober show", "impressioned")
                    // Called when an impression is recorded for an ad.
                }

                override fun onRewarded(reward: Reward) {
                    Log.v("Bober show", "bober rewarded")
                    onRewarded()
                }
            })
            Log.v("Bober show", "ready to show ad")
            if (mainActivity != null) {
                Log.v("Bober show", "activity found, showing ad")
                show(mainActivity!!)
            }
        }
    }

    internal fun destroyRewardedAd() {
        rewardedAd?.setAdEventListener(null)
        rewardedAd = null
    }
}
