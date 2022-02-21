@file:Suppress("unused")

package internal.dependencies

import internal.Versions

object Test {
    /**
     * [JUnit](https://mvnrepository.com/artifact/junit/junit)
     * JUnit is a unit testing framework for Java, created by Erich Gamma and Kent Beck.
     */
    const val junit = "junit:junit:4.13.2"

    /**
     * [MockWebServer](https://github.com/square/okhttp/tree/master/mockwebserver)
     * A scriptable web server for testing HTTP clients
     */
    const val mockWebServer = "com.squareup.okhttp3:mockwebserver:${Versions.mockWebServer}"

    /**
     * [Mockito](https://github.com/mockito/mockito)
     */
    const val mockitoCore = "org.mockito:mockito-core:${Versions.mockitoCore}"

    /**
     * [UI Test Junit4](https://developer.android.com/jetpack/compose/testing#setup)
     */
    const val uiTestJunit4 = "androidx.compose.ui:ui-test-junit4:${Versions.compose}"

    /**
     * [UI Test Manifest](https://developer.android.com/jetpack/compose/testing#setup)
     */
    const val uiTestManifest = "androidx.compose.ui:ui-test-manifest:${Versions.compose}"
}
