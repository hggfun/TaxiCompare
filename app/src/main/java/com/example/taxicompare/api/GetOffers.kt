package com.example.taxicompare.api

import android.util.Log
import com.example.taxicompare.R
import com.example.taxicompare.model.TripOffer
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.*

@Serializable
data class ResponseData(
    val success: Boolean,
    val price: String,
    val universalDialog: String? // Nullable since it might be null
)

fun GetOffers(): List<TripOffer> {
    val sampleTripOffers = listOf(
        TripOffer(iconResId = R.drawable.yandex, companyName = "Yandex", price = "209", tripTime = "15"),
        TripOffer(iconResId = R.drawable.citimobil, companyName = "Ситимобил", price = "180", tripTime = "12"),
        TripOffer(iconResId = R.drawable.taksovichkof, companyName = "Таксовичкоф", price = GetTaxovichkoffOffer(), tripTime = "20"),
        TripOffer(iconResId = R.drawable.maxim, companyName = "Maxim", price = "300", tripTime = "15"),
        TripOffer(iconResId = R.drawable.omega, companyName = "Omega", price = "189", tripTime = "12")
    )
    return sampleTripOffers
}

fun GetOneOffer(): String {
    Log.v("Bober", "starting the request")
    val client = HttpClient(CIO) {
//        install(ContentNegotiation) {
//            json(Json { ignoreUnknownKeys = true })
//        }
    }
    Log.v("Bober", "created client")


    val url = "https://client.taximaxim.ru/ru-RU/service/calculate/?org=maxim&baseId=6&tax-id=yFL33BWu8yOEhqH0C0bV8BfGWKFjFba7Sxdwcdfppe71sHd4uxidkbS5%2B%2BYzBsW%2BiAH1yXFh2Na5bJdvZaNNTNRa6w%2BY1xpwqd1XUGEIcJc%3D"

    val cookies = "__utm_params=87a31d3e4dd49c8d35891c5529b8532139c556997c254dfa297a41c946823d28a%3A2%3A%7Bi%3A0%3Bs%3A12%3A%22__utm_params%22%3Bi%3A1%3Bs%3A32%3A%22%7B%22utm_content%22%3A%22organic_search%22%7D%22%3B%7D; __intl=1a6c3f19d825211b4b399b7960038c6d1a93438adf1deb9825ed10de11d478f0a%3A2%3A%7Bi%3A0%3Bs%3A6%3A%22__intl%22%3Bi%3A1%3Bs%3A5%3A%22ru-RU%22%3B%7D; tmr_lvid=a11a3dc6c3afc8368270b684da94c9f2; tmr_lvidTS=1745173742653; _ym_uid=1745173743597455357; _ym_d=1745173743; _ym_visorc=w; _ym_isad=2; TAXSEE_V3MAXIM=nttojkpsnbk75ks719gg1a452p; __finger_print_hash=52020001f003d3003ba65459eff26a4f21eb3ac81476f65aa6dc8a1328955b97a%3A2%3A%7Bi%3A0%3Bs%3A19%3A%22__finger_print_hash%22%3Bi%3A1%3Bs%3A36%3A%229b21c982-9748-4b93-b12c-a91ff42015e4%22%3B%7D; __taxsee_country=a3955a5ac353f2e4e7d17c290443120d839243c2fd0771f171b22d86d37dd1aea%3A2%3A%7Bi%3A0%3Bs%3A16%3A%22__taxsee_country%22%3Bi%3A1%3Bs%3A2%3A%22RU%22%3B%7D; __taxsee_base=9e565dda8e6bd20cac1c9102001cb2c9eefacc9b3b39f8ab498ac1bb9d285a2da%3A2%3A%7Bi%3A0%3Bs%3A13%3A%22__taxsee_base%22%3Bi%3A1%3Bi%3A6%3B%7D; _csrf=bcac49f683f5b856b9ef41218b9841b6cbd919bb42ccbc8e5180fa8f1174fc37a%3A2%3A%7Bi%3A0%3Bs%3A5%3A%22_csrf%22%3Bi%3A1%3Bs%3A32%3A%22RRpK9U0YnKjrw0C3N7rs47_sqDeYmOun%22%3B%7D; _ga_T2DPT2GHZ5=GS1.1.1745173743.1.0.1745173743.0.0.0; _ga=GA1.2.1038059881.1745173743; _gid=GA1.2.2026264913.1745173743; tmr_detect=0%7C1745173745452"

    val jsonBody = "_csrf=2G3Izybek5SFX-nEcwZv7WjSRLXr5tIuv4OsBmTF1cuKP7iEH4ujzesUg7YENizeJuU2xt_RjV3Ox8lfCYqgpQ%3D%3D&OrderForm%5Bid%5D=&OrderForm%5BbaseId%5D=6&AddressForm%5B0%5D%5BpointField%5D=%D0%AD%D0%BD%D1%82%D1%83%D0%B7%D0%B8%D0%B0%D1%81%D1%82%D0%BE%D0%B2+%D1%88%D0%BE%D1%81%D1%81%D0%B5%2C+13&AddressForm%5B0%5D%5Brem%5D=&AddressForm%5B0%5D%5Bhouse%5D=13&AddressForm%5B0%5D%5Blatitude%5D=55.7516263200415&AddressForm%5B0%5D%5Blongitude%5D=37.7150392286973&AddressForm%5B0%5D%5BaddressName%5D=%D0%AD%D0%BD%D1%82%D1%83%D0%B7%D0%B8%D0%B0%D1%81%D1%82%D0%BE%D0%B2+%D1%88%D0%BE%D1%81%D1%81%D0%B5%2C+13&AddressForm%5B0%5D%5BplaceName%5D=%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0&AddressForm%5B0%5D%5Bpoint%5D=QP6S8425616Z3056050&AddressForm%5B1%5D%5BpointField%5D=%D0%9F%D0%BE%D0%BA%D1%80%D0%BE%D0%B2%D1%81%D0%BA%D0%B8%D0%B9+%D0%B1%D1%83%D0%BB%D1%8C%D0%B2%D0%B0%D1%80%2C+11%D0%A11&AddressForm%5B1%5D%5Brem%5D=&AddressForm%5B1%5D%5Bhouse%5D=11%D0%A11&AddressForm%5B1%5D%5Blatitude%5D=55.7547595777471&AddressForm%5B1%5D%5Blongitude%5D=37.6487705111504&AddressForm%5B1%5D%5BaddressName%5D=%D0%9F%D0%BE%D0%BA%D1%80%D0%BE%D0%B2%D1%81%D0%BA%D0%B8%D0%B9+%D0%B1%D1%83%D0%BB%D1%8C%D0%B2%D0%B0%D1%80%2C+11%D0%A11&AddressForm%5B1%5D%5BplaceName%5D=%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0&AddressForm%5B1%5D%5Bpoint%5D=QP6S8423480Z3066974&OrderForm%5BtariffClasses%5D%5B%5D=1&OrderForm%5BpreOrder%5D=&OrderForm%5BpreOrder%5D=0&OrderForm%5BdateField%5D=20.4.2025&OrderForm%5BhourField%5D=00&OrderForm%5BminuteField%5D=00&ServiceForm%5B274%5D%5Bid%5D=274&ServiceForm%5B274%5D%5Bparam%5D=&ServiceForm%5B322%5D%5Bid%5D=322&ServiceForm%5B322%5D%5Bparam%5D=&ServiceForm%5B196%5D%5Bid%5D=196&ServiceForm%5B465%5D%5Bid%5D=465&ServiceForm%5B223%5D%5Bid%5D=223&ServiceForm%5B288%5D%5Bid%5D=288&ServiceForm%5B255%5D%5Bid%5D=255&ServiceForm%5B394%5D%5Bid%5D=394&ServiceForm%5B394%5D%5Bparam%5D=&ServiceForm%5B448%5D%5Bid%5D=448&ServiceForm%5B448%5D%5Bparam%5D=&ServiceForm%5B536%5D%5Bid%5D=536&ServiceForm%5B536%5D%5Bparam%5D=&OrderForm%5Bphone2%5D=&OrderForm%5Bcashback%5D=&OrderForm%5Brem%5D=&OrderForm%5BsubTypeSource%5D=0"

    val value = runBlocking {
        val response = client.post(url) {
            headers {
                append(HttpHeaders.Accept, "application/json, text/javascript, */*; q=0.01")
                append(HttpHeaders.AcceptLanguage, "ru,en;q=0.9")
                append(HttpHeaders.Connection, "keep-alive")
                append(HttpHeaders.ContentType, "application/x-www-form-urlencoded; charset=UTF-8")
                append(HttpHeaders.Cookie, cookies)
                append(HttpHeaders.Origin, "https://client.taximaxim.ru")
                append("Referer", "https://client.taximaxim.ru/ru/frame/?tax-id=yFL33BWu8yOEhqH0C0bV8BfGWKFjFba7Sxdwcdfppe71sHd4uxidkbS5%2B%2BYzBsW%2BiAH1yXFh2Na5bJdvZaNNTNRa6w%2BY1xpwqd1XUGEIcJc%3D&c=ru&l=ru&b=6&p=1&theme=maximV3&country=ru&city=6&fp=9b21c982-9748-4b93-b12c-a91ff42015e4&t=1")
                append("Sec-Fetch-Dest", "empty")
                append("Sec-Fetch-Mode", "cors")
                append("Sec-Fetch-Site", "same-origin")
                append(HttpHeaders.UserAgent, "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 YaBrowser/24.10.0.0 Safari/537.36")
                append("X-CSRF-Token", "2G3Izybek5SFX-nEcwZv7WjSRLXr5tIuv4OsBmTF1cuKP7iEH4ujzesUg7YENizeJuU2xt_RjV3Ox8lfCYqgpQ==")
                append("X-Requested-With", "XMLHttpRequest")
                append("sec-ch-ua", """ "Chromium";v="128", "Not;A=Brand";v="24", "YaBrowser";v="24.10", "Yowser";v="2.5" """)
                append("sec-ch-ua-mobile", "?0")
                append("sec-ch-ua-platform", """macOS""")
            }
            contentType(ContentType.Application.Json)
            setBody(jsonBody)
        }
//            .body()

        Log.v("Bober", "ready to get response.bodyAsText()")
        Log.v("Bober", response.request.headers.toString())
        Log.v("Bober", response.request.content.toString())
        Log.v("Bober", response.bodyAsText())
        return@runBlocking response.bodyAsText()
//        Log.v("Bober", response.price)
//        return@runBlocking response.price.toInt()
    }

    Log.v("Bober", value.toString())
    return value
}

fun GetTaxovichkoffOffer(): String {
    Log.v("Bober", "starting the request")
    val client = HttpClient(CIO) {
//        install(ContentNegotiation) {
//            json(Json { ignoreUnknownKeys = true })
//        }
    }
    Log.v("Bober", "created client")


    val url = "https://api.gruzovichkof.ru/3/calculator/prices?v=2"

    val unixTimestamp = System.currentTimeMillis()

    val jsonBody = """{"loc":[{"lat":55.751591,"lng":37.714939},{"lat":55.753975,"lng":37.648425}],"paymentType":0,"date":$unixTimestamp,"ordertime":618,"carOptions":{"car_id":1,"passengers":0,"porters":null},"options":[]}"""

    val value = runBlocking {
        val response = client.post(url) {
            headers {
                append("accept", "application/json, text/plain, */*")
                append("accept-language", "ru,en;q=0.9")
                append("api-key", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0b2tlbklkIjo1NiwiYmFzZVVybCI6Imh0dHBzOi8vYXBpLmdydXpvdmljaGtvZi5ydSJ9.hIKxaVXdJRGAkL42vCKJSaBoQ_m6xGkChHJh-7id9DQ")
                append("content-type", "application/json")
                append("origin", "https://msk.taxovichkof.ru")
                append("priority", "u=1, i")
                append("referer", "https://msk.taxovichkof.ru/")
                append("sec-ch-ua", """"Chromium";v="128", "Not;A=Brand";v="24", "YaBrowser";v="24.10", "Yowser";v="2.5"""")
                append("sec-ch-ua-mobile", "?0")
                append("sec-ch-ua-platform", "macOS")
                append("sec-fetch-dest", "empty")
                append("sec-fetch-mode", "cors")
                append("sec-fetch-site", "cross-site")
                append("user-agent", """Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 YaBrowser/24.10.0.0 Safari/537.36""")
            }
            contentType(ContentType.Application.Json)
            setBody(jsonBody)
        }
//            .body()

        Log.v("Bober", "ready to get response.bodyAsText()")
        Log.v("Bober", response.request.headers.toString())
        Log.v("Bober", response.request.content.toString())
        Log.v("Bober", response.bodyAsText())
        return@runBlocking response.bodyAsText()
//        Log.v("Bober", response.price)
//        return@runBlocking response.price.toInt()
    }

    Log.v("Bober", value.toString())
    return value
}