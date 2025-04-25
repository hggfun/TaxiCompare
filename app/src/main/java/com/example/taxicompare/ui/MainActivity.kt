package com.example.taxicompare.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.taxicompare.BuildConfig
import com.yandex.mapkit.MapKitFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapKitFactory.setApiKey(
            BuildConfig.MAPKIT_API_KEY)
        MapKitFactory.initialize(this)

        val appDatabase = (application as App).appDatabase

        enableEdgeToEdge()
        setContent {
            TaxiCompareApp(appDatabase)
        }
    }
}
