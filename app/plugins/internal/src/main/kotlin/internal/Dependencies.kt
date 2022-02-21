@file:Suppress("unused")
package internal


object Dependencies {
    val hilt = internal.dependencies.Di // https://dagger.dev/hilt/
    val security = internal.dependencies.Security // https://developer.android.com/topic/security/data
    val network = internal.dependencies.Network
    val other = internal.dependencies.Other
    val test = internal.dependencies.Test
}
