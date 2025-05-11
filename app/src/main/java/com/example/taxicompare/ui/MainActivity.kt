package com.example.taxicompare.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.taxicompare.BuildConfig
import com.yandex.mapkit.MapKitFactory
import com.yandex.mobile.ads.common.MobileAds

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapKitFactory.setApiKey(
            BuildConfig.MAPKIT_API_KEY)
        MapKitFactory.initialize(this)

        MobileAds.setUserConsent(true)
        MobileAds.initialize(this) {}

        val appDatabase = (application as App).appDatabase
        (application as App).mainActivity = this

        enableEdgeToEdge()
        setContent {
            TaxiCompareApp(appDatabase)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val app = (application as App)
        app.mainActivity = null
        app.rewardedAdLoader?.setAdLoadListener(null)
        app.rewardedAdLoader = null
        app.destroyRewardedAd()
    }
}
