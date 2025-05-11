package com.example.taxicompare.home

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.viewinterop.AndroidView
import com.yandex.mobile.ads.banner.BannerAdSize
import com.yandex.mobile.ads.banner.BannerAdView
import com.yandex.mobile.ads.common.AdRequest


@Composable
fun AdvertisementWidget() {

    Card(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth()
            .fillMaxHeight(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        BannerScreen(
            modifier = Modifier
                .fillMaxSize()
        )
    }
}


@Composable
fun BannerScreen(
    modifier: Modifier
) {
    val bannerAdView = BannerAdView(LocalContext.current)

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        AndroidView(
            modifier = modifier,
            factory = {
                bannerAdView.apply {
                    bannerAdView.setAdUnitId("R-M-15092512-1")
                    bannerAdView.setAdSize(BannerAdSize.fixedSize(this.context, maxWidth.value.toInt(), maxHeight.value.toInt()))
                    val request = AdRequest.Builder().build()
                    bannerAdView.loadAd(request)
                }
            }
        )
    }

}