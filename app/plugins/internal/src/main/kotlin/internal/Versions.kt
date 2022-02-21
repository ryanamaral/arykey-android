@file:Suppress("unused")

package internal


object Versions {
    // jetpack
    const val ktxCore = "1.6.10" // https://kotlinlang.org/docs/releases.html#release-details
    const val ktxCoroutinesAndroid = "1.6.0"
    // compose
    const val compose = "1.1.0-rc01" // https://developer.android.com/jetpack/androidx/versions/all-channel
    // is compatible with Kotlin 1.6.0
    // https://developer.android.com/jetpack/androidx/releases/compose-compiler#1.1.0-rc01
    const val material3 = "1.0.0-alpha02" // https://developer.android.com/jetpack/androidx/releases/compose-material3
    const val constraint = "1.0.0-alpha08"
    const val pagingCompose = "1.0.0-alpha09"
    const val paging = "3.0.0"
    const val accompanist = "0.22.0-rc" // https://github.com/google/accompanist/releases
    const val lottie = "4.2.2" // http://airbnb.io/lottie/#/android-compose
    const val material = "1.5.0"
    const val startup = "1.0.0"
    const val hilt = "1.0.0"
    const val hiltCore = "2.38.1" // https://github.com/google/dagger/releases
    const val hiltComposeNavigation = "1.0.0-beta01" // https://developer.android.com/jetpack/androidx/releases/hilt
    const val room = "2.3.0"
    const val okhttp = "4.9.3"
    const val retrofit2 = "2.9.0"
    const val interceptor = "4.9.1"
    const val sandwich = "1.1.0"
    const val serialization = "1.2.1"
    const val browserCustomTabs = "1.3.0"
    const val kotlinxDatetime = "0.2.1"
    const val securityCrypto = "1.1.0-alpha03"
    const val securityIdentityCredential = "1.0.0-alpha02"
    const val timber = "4.7.1"
    // test
    const val testManifest = "1.1.0-beta04"
    const val mockWebServer = "4.9.1"
    const val mockitoCore = "3.10.0"
}
