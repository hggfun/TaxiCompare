package com.example.taxicompare.ui

import android.app.Application
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

@Composable
fun TaxiCompareApp(appDatabase: AppDatabase) {
    NavigationControllerSetup(appDatabase)
}

class App : Application() {
    lateinit var appDatabase: AppDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        appDatabase = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "database"
        ).build()
    }
}

//fun SetupApplicationBase(
//    activity: MainActivity,
//    application: Application,
//) {
//    MapKitFactory.setApiKey(
//        "90610862-263d-45b7-b5c0-c0872919a2b3")
//    MapKitFactory.initialize(activity)
//
//    val db = (application as App).appDatabase
//}
