package com.example.tetrispiola.ui.views

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.animation.doOnEnd
import com.example.tetrispiola.ui.viewmodels.TetrisViewModel

class TetrisView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private lateinit var viewModel: TetrisViewModel

    private var linesToAnimate: List<Int> = emptyList()
    private var isAnimating = false

    private val paint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.GRAY
        strokeWidth = 2f
    }

    private val rows = 20
    private val cols = 10
    private var cellSize = 0f

    private val handler = Handler(Looper.getMainLooper())
    private val autoFallInterval: Long = 1000

    private val autoFallRunnable = object : Runnable {
        override fun run() {
            if (::viewModel.isInitialized && !viewModel.isGameOver) {
                viewModel.moveDown()
                invalidate()
                handler.postDelayed(this, viewModel.speed)
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        cellSize = (width / cols).coerceAtMost(height / rows).toFloat()
    }

    fun animateLines(lines: List<Int>) {
        linesToAnimate = lines
        isAnimating = true
        val animator = ValueAnimator.ofInt(0, 4).apply {
            duration = 300
            repeatCount = 3
            repeatMode = ValueAnimator.REVERSE
            addUpdateListener {
                invalidate()
            }
            doOnEnd {
                isAnimating = false
                linesToAnimate = emptyList()
                viewModel.removeLines(lines)
                invalidate()
            }
        }
        animator.start()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        paint.style = Paint.Style.STROKE
        paint.color = Color.GRAY
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                val left = j * cellSize
                val top = i * cellSize
                canvas.drawRect(left, top, left + cellSize, top + cellSize, paint)
            }
        }

        paint.style = Paint.Style.FILL
        if (::viewModel.isInitialized) {
            for (i in 0 until rows) {
                for (j in 0 until cols) {
                    if (viewModel.board[i][j] != 0) {
                        paint.color = viewModel.board[i][j]

                        if (isAnimating && i in linesToAnimate) {
                            paint.alpha = if ((System.currentTimeMillis() / 100) % 2 == 0L) 50 else 255
                        } else {
                            paint.alpha = 255
                        }

                        val left = j * cellSize
                        val top = i * cellSize
                        canvas.drawRect(left, top, left + cellSize, top + cellSize, paint)
                    }
                }
            }

            viewModel.currentPiece.shape.forEachIndexed { rowIndex, row ->
                row.forEachIndexed { colIndex, cell ->
                    if (cell == 1) {
                        paint.color = viewModel.currentPiece.color
                        paint.alpha = 255
                        val x = (viewModel.currentPiece.x + colIndex) * cellSize
                        val y = (viewModel.currentPiece.y + rowIndex) * cellSize
                        canvas.drawRect(x, y, x + cellSize, y + cellSize, paint)
                    }
                }
            }
        }
    }



    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        handler.postDelayed(autoFallRunnable, autoFallInterval)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        handler.removeCallbacks(autoFallRunnable)
    }

    fun restartAutoFall() {
        handler.removeCallbacks(autoFallRunnable)
        handler.postDelayed(autoFallRunnable, viewModel.speed)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN && ::viewModel.isInitialized) {
            val widthThird = width / 3
            val heightQuarter = height / 4

            if (event.y < heightQuarter) {
                viewModel.rotatePiece()
            } else {
                when {
                    event.x < widthThird -> viewModel.moveLeft()
                    event.x > widthThird * 2 -> viewModel.moveRight()
                    else -> viewModel.moveToBottom()
                }
            }
            invalidate()
        }
        return true
    }

    fun setViewModel(vm: TetrisViewModel) {
        viewModel = vm
        invalidate()
    }
}
