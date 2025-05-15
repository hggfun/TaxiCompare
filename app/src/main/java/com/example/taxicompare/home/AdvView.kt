package com.example.taxicompare.home

import android.util.Log
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.updateLayoutParams
import com.yandex.mobile.ads.banner.BannerAdSize
import com.yandex.mobile.ads.banner.BannerAdView
import com.yandex.mobile.ads.common.AdRequest
import kotlin.math.min
import kotlin.math.roundToInt


@Composable
fun AdvertisementWidget() {
    var width by remember { mutableIntStateOf(0) }
    var height by remember { mutableIntStateOf(0) }

    Card(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .fillMaxWidth()
            .aspectRatio(1f),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        BannerScreen(
            modifier = Modifier.fillMaxSize()
        )
    }
}


@Composable
fun BannerScreen(
    modifier: Modifier
) {
    val bannerAdView = BannerAdView(LocalContext.current)
    val density = LocalDensity.current.density

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        AndroidView(
            modifier = modifier,
            factory = {
                bannerAdView.apply {
                    bannerAdView.setAdUnitId("demo-banner-yandex")
                    bannerAdView.setAdSize(BannerAdSize.fixedSize(this.context, maxWidth.value.toInt(), maxHeight.value.toInt()))
                    Log.v("Bober size", "${maxWidth.value.toInt()} * ${maxHeight.value.toInt()}")
                    val request = AdRequest.Builder().build()
                    bannerAdView.loadAd(request)
                }
            }
        )
    }
}

fun makeSuitableSize(width: Int, height: Int): Pair<Int, Int> {
    if (height > width * 2) {
        return Pair (width, width * 2)
    }
    if (width > height * 2) {
        return Pair (height * 2, height)
    }
    val min = min(width, height)
    return Pair(min, min)
}

@Composable
fun BannerAd(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var bannerSize by remember { mutableStateOf(IntSize.Zero) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned { coordinates ->
                val size = coordinates.size // Size in pixels
                if (bannerSize != size) {
                    bannerSize = size
                }
            }
    ) {
        if (bannerSize.width > 0 && bannerSize.height > 0) {
            AndroidView(
                factory = {
                    BannerAdView(context).apply {
                        setAdUnitId("R-M-15092512-1")
                        setAdSize(
                            BannerAdSize.inlineSize(
                                context,
                                bannerSize.width/4,
                                bannerSize.height/4
                            )
                        )
                        Log.v("Bober size adaptive", "${bannerSize.width} * ${bannerSize.height}")
                        loadAd(AdRequest.Builder().build())
                    }
                },
                // If you want to update only if the size changes:
                update = { bannerAdView ->
                    bannerAdView.setAdSize(
                        BannerAdSize.inlineSize(
                            context,
                            bannerSize.width,
                            bannerSize.height
                        )
                    )
                }
            )
        }
    }
}