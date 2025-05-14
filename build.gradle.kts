import java.io.FileInputStream
import java.util.Properties

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.ksp) apply false
}

val MapkitApiKey: String = run {
    val properties = Properties()
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        properties.load(FileInputStream(localPropertiesFile))
        properties.getProperty("MAPKIT_API_KEY", "")
    } else ""
}

val GeocoderApiKey: String = run {
    val properties = Properties()
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        properties.load(FileInputStream(localPropertiesFile))
        properties.getProperty("GEOCODER_API_KEY", "")
    } else ""
}

val YaTaxiApiKey: String = run {
    val properties = Properties()
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        properties.load(FileInputStream(localPropertiesFile))
        properties.getProperty("YA_TAXI_API_KEY", "")
    } else ""
}

val TaksovichkoffApiKey: String = run {
    val properties = Properties()
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        properties.load(FileInputStream(localPropertiesFile))
        properties.getProperty("TAKSOVICHKOFF_API_KEY", "")
    } else ""
}

ext["mapkitApiKey"] = MapkitApiKey
ext["geocoderApiKey"] = GeocoderApiKey
ext["yaTaxiApiKey"] = YaTaxiApiKey
ext["taksovichkoffApiKey"] = TaksovichkoffApiKey