package com.ryanamaral.arykey.common.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.ryanamaral.arykey.R
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.math.abs
import kotlin.math.min


/**
 * Solid circle with animated ripples coming out
 */
class CircularPulseView : View, CoroutineScope {

    private var mTimeInMillis = TIME_DEFAULT
    private var mStepCount = STEP_DEFAULT
    private var mInitialRadius: Float = 0f
    private var mMaxRadius: Float = 0f
    private var mRippleCount = 0
    private var mViewWidth = 0
    private var mViewHeight = 0
    private var mCircles = arrayOf(Circle(), Circle(), Circle())
    private var mIsAnimationRunning = false
    private var mPlayAnimation = false
    private var mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mInnerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mAnimationJob = Job()
    override val coroutineContext: CoroutineContext = Dispatchers.Default + mAnimationJob


    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(
        context,
        attrs,
        R.attr.circlePulseViewStyle
    )

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs, defStyleAttr)
    }

    /**
     * Initialize views
     */
    private fun init(attrs: AttributeSet?, defStyleAttr: Int) {
        if (isInEditMode) return

        setOnMeasureListener()

        // load defaults from resources
        val defaultRippleColor = ContextCompat.getColor(context, R.color.colorAccent)
        val defaultRippleCount = resources.getInteger(R.integer.cpv_ripple_count_default)
        val defaultMinRadius = resources.getDimension(R.dimen.cpv_radius_initial_default)

        if (attrs != null) {
            // retrieve styles attributes
            val attr = context.obtainStyledAttributes(
                attrs,
                R.styleable.CircularPulseView,
                defStyleAttr,
                0
            )
            val rippleColor = attr.getColor(
                R.styleable.CircularPulseView_cpv_color,
                defaultRippleColor
            )
            mRippleCount = attr.getInt(
                R.styleable.CircularPulseView_cpv_count,
                defaultRippleCount
            )
            mInitialRadius = attr.getDimension(
                R.styleable.CircularPulseView_cpv_radius_initial,
                defaultMinRadius
            )
            attr.recycle()
            initPaint(rippleColor)
        }
        initialiseCircles()
    }

    private fun initPaint(rippleColor: Int) {
        mPaint.apply {
            style = Paint.Style.FILL
            color = rippleColor
            alpha = ALPHA_MIN
        }
        mInnerPaint.apply {
            style = Paint.Style.FILL
            color = rippleColor
        }
    }

    /**
     * Sets up the animation coroutine
     */
    private fun setupAnimationCoroutine() {
        launch {
            animateCoroutine()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mAnimationJob.apply { if (isActive) cancel() }
    }

    /**
     * Coroutine to update the animation
     */
    private suspend fun animateCoroutine() {
        while (mPlayAnimation) {
            mIsAnimationRunning = true
            val startTime = System.currentTimeMillis()
            updateCircles()
            withContext(Dispatchers.Main) {
                postInvalidate()
            }
            val endTime = System.currentTimeMillis()
            val deltaTime = ((mTimeInMillis / FRAME_RATE) - (endTime - startTime))
            delay(abs(deltaTime))
        }
    }

    /**
     * Loops through all the circles and update the radius and alpha values
     */
    private fun updateCircles() {
        mCircles.indices.forEach { i ->
            // when step count reaches maximum, reset to default radius
            if (mCircles[i].radius + mStepCount >= mMaxRadius) {
                mCircles[i].radius = mInitialRadius

            } else if (i == 0) {
                mCircles[i].radius += mStepCount

            } else if (mCircles[i - 1].radius >= (mInitialRadius + (mMaxRadius - mInitialRadius) / (mRippleCount))
                || (mCircles[i - 1].radius >= mInitialRadius && mCircles[i].radius > mInitialRadius)
            ) {
                mCircles[i].radius += mStepCount
            }
            // set the alpha for the circles
            mCircles[i].alpha = getAlphaForRadius(mCircles[i].radius)
        }
    }

    /**
     * Returns the alpha value based on the radius of the circle
     *
     * @param radius radius of the circle
     * @return alpha alpha value based on the radius
     */
    private fun getAlphaForRadius(radius: Float): Int {
        val radiusPercentage = 100 - ((radius / mMaxRadius) * 100)
        return (radiusPercentage * (ALPHA_MAX / 100)).toInt()
    }

    /**
     * Initialize the circles
     */
    private fun initialiseCircles() {
        mCircles.forEach { element ->
            element.radius = mInitialRadius
        }
    }

    /**
     * Draw circles on canvas
     */
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        synchronized(mCircles) {
            mCircles.forEach { circleItem ->
                mPaint.alpha = circleItem.alpha
                canvas?.drawCircle(mViewWidth * 0.5F, mViewHeight * 0.5F, circleItem.radius, mPaint)
            }
            canvas?.drawCircle(mViewWidth * 0.5F, mViewHeight * 0.5F, mInitialRadius, mInnerPaint)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mViewWidth = w
        mViewHeight = h
        mMaxRadius = min(mViewWidth, mViewHeight).toFloat() / 2
    }

    private fun setOnMeasureListener() {
        val viewTreeObserver = viewTreeObserver
        viewTreeObserver.addOnGlobalLayoutListener {
            if (viewTreeObserver.isAlive) {
                viewTreeObserver.removeOnGlobalLayoutListener {}
            }
            mViewWidth = measuredWidth
            mViewHeight = measuredHeight
            mMaxRadius = min(mViewWidth, mViewHeight).toFloat() / 2
        }
    }

    /**
     * Sets the animation state
     *
     * @param isEnabled should play animation
     */
    fun setAnimationState(isEnabled: Boolean) {
        if (mPlayAnimation == isEnabled) return
        mPlayAnimation = isEnabled

        if (!mPlayAnimation) {
            mIsAnimationRunning = false
            initialiseCircles()
            postInvalidate()
        }
        if (!mIsAnimationRunning) {
            setupAnimationCoroutine()
        }
    }

    /**
     * Circle class to store the radius and the alpha values
     */
    inner class Circle(var radius: Float = 0F, var alpha: Int = ALPHA_MAX)

    companion object {
        const val FRAME_RATE = 60L
        const val TIME_DEFAULT = 3600L
        const val STEP_DEFAULT = 2
        const val ALPHA_MAX = 255
        const val ALPHA_MIN = 10
    }
}
