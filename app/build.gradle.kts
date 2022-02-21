plugins {
    id("dagger.hilt.android.plugin")
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("internal")
    id("de.mannodermaus.android-junit5")
    kotlin("kapt")
    kotlin("plugin.serialization")
}

android {
    compileSdk = internal.Android.compileSdk
    buildToolsVersion = internal.Android.buildTools

    defaultConfig {
        applicationId = "com.ryanamaral.arykey"
        minSdk = internal.Android.minSdk
        targetSdk = internal.Android.targetSdk
        versionCode = 202202211
        versionName = "2022.02.21.1"
        testInstrumentationRunner = "com.ryanamaral.arykey.runner.AppTestRunner"
        //testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArgument(
            "runnerBuilder",
            "de.mannodermaus.junit5.AndroidJUnit5Builder"
        )
        vectorDrawables {
            useSupportLibrary = true
        }
        missingDimensionStrategy("device", "anyDevice")
        buildConfigField("Long", "TIMEOUT_MILLIS", "5000L")
        resValue("bool", "uses_clear_text_traffic", "false")
        resValue("string", "gravatar_base_url", "https://s.gravatar.com")
        buildConfigField("String", "GRAVATAR_BASE_URL", "\"https://s.gravatar.com\"")
        buildConfigField(
            "String",
            "GRAVATAR_PIN_SET",
            "\"sha256/sSAE6ZWFQvZ1mQB8kh4utc/VpbMVSPQEuedwea9FrtM=\""
        )
    }

    sourceSets["test"].kotlin {
        srcDir("$projectDir/src/testShared")
    }
    sourceSets["androidTest"].kotlin {
        srcDir("$projectDir/src/testShared")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = internal.Versions.compose
    }
    packagingOptions {
        resources {
            excludes.add("META-INF/AL2.0")
            excludes.add("META-INF/LGPL2.1")
        }
    }
}

dependencies {
    internal.Dependencies.hilt.apply {
        implementation(hiltAndroid)
        kapt(hiltCompiler)
        kapt(daggerHiltCompiler)
        kaptAndroidTest(daggerHiltCompiler)
        implementation("androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha03")
        kapt("com.google.dagger:hilt-android-compiler:${internal.Versions.hiltCore}")
        androidTestImplementation("com.google.dagger:hilt-android-testing:${internal.Versions.hiltCore}")
        kaptAndroidTest("com.google.dagger:hilt-android-compiler:${internal.Versions.hiltCore}")
    }
    internal.Dependencies.network.apply {
        implementation(okhttp)
        implementation(interceptor)
    }
    internal.Dependencies.security.apply {
        implementation(crypto)
    }
    internal.Dependencies.other.apply {
        implementation(material)
        implementation(lifecycleRuntime)
        implementation(timber)
        implementation(ktxCore)
        implementation(ktxCoroutinesAndroid)
        implementation("androidx.appcompat:appcompat:1.4.1")
        implementation("androidx.core:core-ktx:1.7.0")
        implementation("androidx.fragment:fragment-ktx:1.4.1")
        val lifecycle_version = "2.4.0-alpha01" // Flow.flowWithLifecycle introduced
        implementation("androidx.lifecycle:lifecycle-runtime-ktx:${lifecycle_version}")
        implementation("androidx.lifecycle:lifecycle-livedata-ktx:${lifecycle_version}")
        implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:${lifecycle_version}")
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:${internal.Versions.ktxCore}")
        implementation("io.coil-kt:coil:1.4.0")
        implementation("io.coil-kt:coil-compose:1.4.0")
        implementation("androidx.datastore:datastore-preferences:1.0.0")
        implementation("com.github.felHR85:UsbSerial:6.1.0")
        implementation("androidx.preference:preference-ktx:1.2.0")
        implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
        implementation("com.github.davidmigloz:number-keyboard:3.0.0")
        implementation("com.github.mukeshsolanki:android-otpview-pinview:2.1.2")
        implementation("com.airbnb.android:lottie:4.2.2")
        debugImplementation("com.squareup.leakcanary:leakcanary-android:2.7")
    }
    internal.Dependencies.test.apply {
        testImplementation(junit) // junit4
        val junit5_version = "5.8.2"
        testImplementation("org.junit.jupiter:junit-jupiter-api:${junit5_version}")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${junit5_version}")
        testImplementation("org.junit.jupiter:junit-jupiter-params:${junit5_version}")
        testRuntimeOnly("org.junit.vintage:junit-vintage-engine:${junit5_version}")
        androidTestImplementation("de.mannodermaus.junit5:android-test-core:1.3.0")
        androidTestRuntimeOnly("de.mannodermaus.junit5:android-test-runner:1.3.0")
        implementation("androidx.test:runner:1.4.0")
        implementation("androidx.test.espresso:espresso-core:3.4.0")
        testImplementation("com.google.truth:truth:1.1.3")
        testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.0")
        val mockk_version = "1.12.0"
        testImplementation("io.mockk:mockk:${mockk_version}")
        testImplementation("io.mockk:mockk:${mockk_version}")
        testImplementation("io.mockk:mockk-agent-jvm:${mockk_version}")
        androidTestImplementation("io.mockk:mockk-android:${mockk_version}")
        androidTestImplementation("io.mockk:mockk-agent-jvm:${mockk_version}")
        testImplementation("androidx.arch.core:core-testing:2.1.0")
    }
}
