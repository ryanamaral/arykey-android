package com.ryanamaral.arykey.common.extension

import android.animation.Animator
import com.airbnb.lottie.LottieAnimationView


fun LottieAnimationView.addAnimationListener(
    onAnimationRepeat: () -> Unit = {},
    onAnimationEnd: () -> Unit = {},
    onAnimationCancel: () -> Unit = {},
    onAnimationStart: () -> Unit = {}
) {
    addAnimatorListener(object : Animator.AnimatorListener {
        override fun onAnimationRepeat(p0: Animator?) {
            onAnimationRepeat()
        }

        override fun onAnimationEnd(p0: Animator?) {
            onAnimationEnd()
        }

        override fun onAnimationCancel(p0: Animator?) {
            onAnimationCancel()
        }

        override fun onAnimationStart(p0: Animator?) {
            onAnimationStart()
        }
    })
}