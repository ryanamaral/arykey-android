@file:Suppress("unused")
package internal.dependencies


object Network {

    const val okhttp = "com.squareup.okhttp3:okhttp:${internal.Versions.okhttp}"

    /**
     * [OkHttp Logging Interceptor](https://mvnrepository.com/artifact/com.squareup.okhttp3/logging-interceptor)
     */
    const val interceptor = "com.squareup.okhttp3:logging-interceptor:${internal.Versions.interceptor}"
}
