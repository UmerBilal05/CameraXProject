package com.example.cameraxproject.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.cameraxproject.util.Tools.Companion.dp2px

class FocusView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private var focusSize //焦点框的大小
            = 0
    private var focusColor //焦点框的颜色
            = 0
    private var focusTime //焦点框显示的时长
            = 0
    private var focusStrokeSize //焦点框线条的尺寸
            = 0
    private var cornerSize //焦点框圆角尺寸
            = 0
    private var handler: Handler? = null
    private var runnable: Runnable? = null
    private var mPaint: Paint? = null
    private var rect: RectF? = null
    private fun init(context: Context) {
        handler = Handler()
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        rect = RectF()
        runnable = Runnable { hideFocusView() }
    }

    fun setParam(
        focusViewSize: Int, focusViewColor: Int, focusViewTime: Int,
        focusViewStrokeSize: Int, cornerViewSize: Int
    ) {
        if (focusViewSize == -1) {
            focusSize = dp2px(context, 60f)
        } else {
            focusSize = focusViewSize
        }
        if (focusViewColor == -1) {
            focusColor = Color.GREEN
        } else {
            focusColor = focusViewColor
        }
        focusTime = focusViewTime
        if (focusViewStrokeSize == -1) {
            focusStrokeSize = dp2px(context, 2f)
        } else {
            focusStrokeSize = focusViewStrokeSize
        }
        if (cornerViewSize == -1) {
            cornerSize = focusSize / 5
        } else {
            cornerSize = cornerViewSize
        }
        mPaint!!.style = Paint.Style.STROKE
        mPaint!!.strokeWidth = focusStrokeSize.toFloat()
        mPaint!!.color = focusColor
        rect!!.top = 0f
        rect!!.left = rect!!.top
        rect!!.bottom = focusSize.toFloat()
        rect!!.right = rect!!.bottom
    }

    fun showFocusView(x: Int, y: Int) {
        visibility = VISIBLE
        val layoutParams = layoutParams as ConstraintLayout.LayoutParams
        layoutParams.leftMargin = x - focusSize / 2
        layoutParams.topMargin = y - focusSize / 2
        setLayoutParams(layoutParams)
        invalidate()
        handler!!.postDelayed(runnable!!, (focusTime * 1000).toLong())
    }

    fun hideFocusView() {
        visibility = GONE
        if (handler != null) {
            handler!!.removeCallbacks(runnable!!)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(focusSize, focusSize)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRoundRect(rect!!, cornerSize.toFloat(), cornerSize.toFloat(), mPaint!!)
    }

    override fun onDetachedFromWindow() {
        if (handler != null) {
            handler!!.removeCallbacks(runnable!!)
        }
        super.onDetachedFromWindow()
    }

    init {
        init(context)
    }
}