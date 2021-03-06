package ru.mrlargha.larghabike.presentation.ui.cusomviews

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.annotation.FontRes
import androidx.core.animation.doOnEnd
import androidx.core.content.res.ResourcesCompat
import ru.mrlargha.larghabike.R
import java.text.DecimalFormat
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class Speedometer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var _maxSpeed = 60

    @Dimension
    private var _borderSize = 20f

    @Dimension
    private var _textGap = 50f

    @ColorInt
    private var _borderColor = context.getColor(R.color.primaryDarkColor)

    @ColorInt
    private var _fillColor = context.getColor(R.color.primaryColor)

    @ColorInt
    private var _textColor = context.getColor(R.color.material_on_background_emphasis_medium)

    private var _metricText = "km/h"

    @FontRes
    private val _font = R.font.digital

    private val indicatorBorderRect = RectF()

    private val tickBorderRect = RectF()

    private val textBounds = Rect()

    private var angle = MIN_ANGLE

    private var speed = 0f

    private val centerX get() = width / 2f

    private val centerY get() = height / 2f

    private val decimalFormat = DecimalFormat("##")

    var maxSpeed: Int
        get() = _maxSpeed
        set(value) {
            _maxSpeed = value
            invalidate()
        }

    var odometerText = "00000 m"
        set(value) {
            field = value
            invalidate()
        }

    var borderSize: Float
        @Dimension get() = _borderSize
        set(@Dimension value) {
            _borderSize = value
            paintIndicatorBorder.strokeWidth = value
            paintIndicatorFill.strokeWidth = value
            invalidate()
        }

    var textGap: Float
        @Dimension get() = _textGap
        set(@Dimension value) {
            _textGap = value
            invalidate()
        }

    var metricText: String
        get() = _metricText
        set(value) {
            _metricText = value
            invalidate()
        }

    var borderColor: Int
        @ColorInt get() = _borderColor
        set(@ColorInt value) {
            _borderColor = value
            paintIndicatorBorder.color = value
            paintTickBorder.color = value
            paintMajorTick.color = value
            paintMinorTick.color = value
            invalidate()
        }

    var fillColor: Int
        @ColorInt get() = _fillColor
        set(@ColorInt value) {
            _fillColor = value
            paintIndicatorFill.color = value
            invalidate()
        }

    var textColor: Int
        @ColorInt get() = _textColor
        set(@ColorInt value) {
            _textColor = value
            paintTickText.color = value
            paintSpeed.color = value
            paintMetric.color = value
            invalidate()
        }

    // Paints
    private val paintIndicatorBorder = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        color = borderColor
        strokeWidth = borderSize
        strokeCap = Paint.Cap.ROUND
    }

    private val paintIndicatorFill = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        color = fillColor
        strokeWidth = borderSize
        strokeCap = Paint.Cap.ROUND
    }

    private val paintTickBorder = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        color = borderColor
        strokeWidth = 4f
        strokeCap = Paint.Cap.ROUND
    }

    private val paintMajorTick = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        color = borderColor
        strokeWidth = MAJOR_TICK_WIDTH
        strokeCap = Paint.Cap.BUTT
    }

    private val paintMinorTick = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        color = borderColor
        strokeWidth = MINOR_TICK_WIDTH
        strokeCap = Paint.Cap.BUTT
    }

    private val paintTickText = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = textColor
        textSize = 40f
    }

    private val paintSpeed = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = textColor
        textSize = 200f
        if (!isInEditMode)
            typeface = ResourcesCompat.getFont(getContext(), _font)
    }

    private val paintMetric = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = textColor
        textSize = 50f
    }

    private val paintOdometer = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = textColor
        textSize = 50f
        if (!isInEditMode)
            typeface = ResourcesCompat.getFont(getContext(), _font)
    }

    // Animators
    private val animator = ValueAnimator.ofFloat().apply {
        interpolator = AccelerateDecelerateInterpolator()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(widthMeasureSpec, widthMeasureSpec)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        indicatorBorderRect.set(
            borderSize / 2,
            borderSize / 2,
            width - borderSize / 2,
            width - borderSize / 2
        )
        tickBorderRect.set(
            borderSize + TICK_MARGIN,
            borderSize + TICK_MARGIN,
            width - borderSize - TICK_MARGIN,
            width - borderSize - TICK_MARGIN
        )
    }

    init {
        obtainStyledAttributes(attrs, defStyleAttr)
    }

    private fun obtainStyledAttributes(attrs: AttributeSet?, defStyleAttr: Int) {
        val typedArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.Speedometer,
            defStyleAttr,
            0
        )

        try {
            with(typedArray) {
                maxSpeed = getInt(
                    R.styleable.Speedometer_maxSpeed,
                    maxSpeed
                )
                borderSize = getDimension(
                    R.styleable.Speedometer_borderSize,
                    borderSize
                )
                textGap = getDimension(
                    R.styleable.Speedometer_textGap,
                    textGap
                )
                metricText = getString(
                    R.styleable.Speedometer_metricText
                ) ?: metricText
                borderColor = getColor(
                    R.styleable.Speedometer_borderColor,
                    borderColor
                )
                fillColor = getColor(
                    R.styleable.Speedometer_fillColor,
                    borderColor
                )
                textColor = getColor(
                    R.styleable.Speedometer_textColor,
                    borderColor
                )
            }
        } catch (e: Exception) {
            // ignored
        } finally {
            typedArray.recycle()
        }
    }

    override fun onDraw(canvas: Canvas) {
        renderMajorTicks(canvas)
        renderMinorTicks(canvas)
        renderBorder(canvas)
        renderBorderFill(canvas)
        renderTickBorder(canvas)
        renderSpeedAndMetricText(canvas)
        renderOdometerText(canvas)
    }

    private fun renderMinorTicks(canvas: Canvas) {
        for (s in MIN_SPEED..maxSpeed step (maxSpeed - MIN_SPEED) / MINOR_TICK_COUNT) {
            canvas.drawLine(
                centerX + (centerX - borderSize - MINOR_TICK_SIZE) * cos(mapSpeedToAngle(s).toRadian()),
                centerY - (centerY - borderSize - MINOR_TICK_SIZE) * sin(mapSpeedToAngle(s).toRadian()),
                centerX + (centerX - borderSize - TICK_MARGIN) * cos(mapSpeedToAngle(s).toRadian()),
                centerY - (centerY - borderSize - TICK_MARGIN) * sin(mapSpeedToAngle(s).toRadian()),
                paintMinorTick
            )
        }
    }

    private fun renderMajorTicks(canvas: Canvas) {
        for (s in MIN_SPEED..maxSpeed step (maxSpeed - MIN_SPEED) / MAJOR_TICK_COUNT) {
            canvas.drawLine(
                centerX + (centerX - borderSize - MAJOR_TICK_SIZE) * cos(mapSpeedToAngle(s).toRadian()),
                centerY - (centerY - borderSize - MAJOR_TICK_SIZE) * sin(mapSpeedToAngle(s).toRadian()),
                centerX + (centerX - borderSize - TICK_MARGIN) * cos(mapSpeedToAngle(s).toRadian()),
                centerY - (centerY - borderSize - TICK_MARGIN) * sin(mapSpeedToAngle(s).toRadian()),
                paintMajorTick
            )
            canvas.drawTextCentred(
                s.toString(),
                centerX + (centerX - borderSize - MAJOR_TICK_SIZE - TICK_MARGIN - TICK_TEXT_MARGIN) * cos(
                    mapSpeedToAngle(s).toRadian()
                ),
                centerY - (centerY - borderSize - MAJOR_TICK_SIZE - TICK_MARGIN - TICK_TEXT_MARGIN) * sin(
                    mapSpeedToAngle(s).toRadian()
                ),
                paintTickText
            )
        }
    }

    private fun renderBorder(canvas: Canvas) {
        canvas.drawArc(
            indicatorBorderRect,
            START_ANGLE,
            SWEEP_ANGLE,
            false,
            paintIndicatorBorder
        )
    }

    private fun renderTickBorder(canvas: Canvas) {
        canvas.drawArc(
            tickBorderRect,
            START_ANGLE,
            SWEEP_ANGLE,
            false,
            paintTickBorder
        )
    }

    private fun renderBorderFill(canvas: Canvas) {
        canvas.drawArc(
            indicatorBorderRect,
            START_ANGLE,
            MIN_ANGLE - angle,
            false,
            paintIndicatorFill
        )
    }

    private fun renderSpeedAndMetricText(canvas: Canvas) {
        canvas.drawTextCentred(
            decimalFormat.format(speed),
            width / 2f,
            height / 2f,
            paintSpeed
        )
        canvas.drawTextCentred(
            metricText,
            width / 2f,
            height / 2f + paintSpeed.textSize / 2 + textGap,
            paintMetric
        )
    }

    private fun renderOdometerText(canvas: Canvas) {
        canvas.drawTextCentred(
            odometerText,
            width / 2f,
            height - borderSize * 2,
            paintOdometer
        )
    }

    private fun mapSpeedToAngle(speed: Float): Float {
        return (MIN_ANGLE + ((MAX_ANGLE - MIN_ANGLE) / (maxSpeed - MIN_SPEED)) * (speed - MIN_SPEED))
    }

    private fun mapSpeedToAngle(speed: Int): Float {
        return (MIN_ANGLE + ((MAX_ANGLE - MIN_ANGLE) / (maxSpeed - MIN_SPEED)) * (speed - MIN_SPEED))
    }

    private fun mapAngleToSpeed(angle: Float): Float {
        return (MIN_SPEED + ((maxSpeed - MIN_SPEED) / (MAX_ANGLE - MIN_ANGLE)) * (angle - MIN_ANGLE))
    }

    fun setSpeed(s: Float, d: Long = 0, onEnd: (() -> Unit)? = null) {
        val newSpeed = if (s > maxSpeed) maxSpeed.toFloat() else s
        animator.apply {
            setFloatValues(mapSpeedToAngle(speed), mapSpeedToAngle(newSpeed))

            addUpdateListener {
                angle = it.animatedValue as Float
                speed = mapAngleToSpeed(angle)
                invalidate()
            }
            doOnEnd {
                it.removeAllListeners()
                onEnd?.invoke()
            }
            duration = d
            start()
        }
    }

    private fun Canvas.drawTextCentred(text: String, cx: Float, cy: Float, paint: Paint) {
        paint.getTextBounds(text, 0, text.length, textBounds)
        drawText(text, cx - textBounds.exactCenterX(), cy - textBounds.exactCenterY(), paint)
    }

    private fun Float.toRadian(): Float {
        return this * (PI / 180).toFloat()
    }

    companion object {
        private const val START_ANGLE = 120f
        private const val SWEEP_ANGLE = 300f

        private const val MIN_ANGLE = 360f - START_ANGLE
        private const val MAX_ANGLE = MIN_ANGLE - SWEEP_ANGLE

        private const val MIN_SPEED = 0
        private const val TICK_MARGIN = 10f
        private const val TICK_TEXT_MARGIN = 30f
        private const val MAJOR_TICK_SIZE = 20f
        private const val MINOR_TICK_SIZE = 20f
        private const val MAJOR_TICK_WIDTH = 4f
        private const val MINOR_TICK_WIDTH = 2f
        private const val MAJOR_TICK_COUNT = 10
        private const val MINOR_TICK_COUNT = MAJOR_TICK_COUNT * 5
    }
}
