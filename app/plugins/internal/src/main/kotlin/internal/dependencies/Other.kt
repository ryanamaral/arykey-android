@file:Suppress("unused")
package internal.dependencies

import internal.*

object Other {
    /**
     * [Core Kotlin Extensions](https://developer.android.com/kotlin/ktx#core)
     * Kotlin extensions for 'core' artifact
     */
    const val ktxCore = "androidx.core:core-ktx:${Versions.ktxCore}"

    const val ktxCoroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.ktxCoroutinesAndroid}"

    /**
     * [Material Components For Android](https://mvnrepository.com/artifact/com.google.android.material/material)
     * Material Components for Android is a static library that you can add to your Android application in order to use APIs that provide
     * implementations of the Material Design specification. Compatible on devices running API 14 or later.
     */
    const val material = "com.google.android.material:material:${Versions.material}"

    const val lifecycleRuntime = "androidx.lifecycle:lifecycle-runtime-ktx:2.3.0"

    /**
     * [Timber](https://mvnrepository.com/artifact/com.jakewharton.timber/timber)
     * No-nonsense injectable logging.
     */
    const val timber = "com.jakewharton.timber:timber:${Versions.timber}"

    /**
     * [Kotlin multiplatform serialization](https://github.com/Kotlin/kotlinx.serialization)
     */
    const val serialization = "org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.serialization}"
}