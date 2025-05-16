package com.example.project5

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View

class GameView(context: Context, private val screenW: Int, private val screenH: Int) : View(context) {
    private val paintBrush = Paint()
    private val gameEngine: BrickBreaker
    private val colorPalette = arrayOf(
        Color.RED, Color.BLACK, Color.GREEN, Color.BLUE,
        Color.CYAN, Color.GRAY, Color.YELLOW, Color.MAGENTA
    )

    init {
        paintBrush.strokeWidth = 20f
        paintBrush.isAntiAlias = true
        paintBrush.style = Paint.Style.FILL

        val brickW = (screenW / 6f).toInt()
        val brickH = (screenH / 25f).toInt()
        val brickGrid: Array<Array<Rect?>> = Array(4) { row ->
            Array<Rect?>(6) { col -> // explicitly nullable
                val left = col * brickW
                val top = row * brickH
                val right = left + brickW
                val bottom = top + brickH
                Rect(left, top, right, bottom)
            }
        }

        val paddleLeft = (screenW / 2 - 90)
        val paddleTop = screenH - 25
        val paddleRight = screenW / 2 + 90
        val paddleBottom = screenH - 9

        gameEngine = BrickBreaker(screenH, screenW, context)
        gameEngine.setPaddle(paddleLeft, paddleTop, paddleRight, paddleBottom)
        gameEngine.setBricks(brickGrid)
        gameEngine.setBallRadius(12f)
        gameEngine.setBallLocation((screenW / 2).toFloat(), (screenH / 21) * 5f)
        gameEngine.setBallVelocity(screenW * 0.03f)
    }

    fun getGame(): BrickBreaker = gameEngine

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for (row in 0 until 4) {
            for (col in 0 until 6) {
                paintBrush.color = if (col % 2 == 0) colorPalette[row * 2] else colorPalette[row * 2 + 1]
                val brick = gameEngine.getBricks()[row][col]
                if (brick != null) {
                    canvas.drawRect(brick, paintBrush)
                }
            }
        }

        paintBrush.color = Color.DKGRAY
        canvas.drawRect(gameEngine.getPaddle(), paintBrush)
        canvas.drawCircle(gameEngine.getBallX(), gameEngine.getBallY(), gameEngine.getBallRadius(), paintBrush)

        if (gameEngine.isGameOver()) {
            paintBrush.textSize = 100f
            canvas.drawText("Game Over", screenW / 4f, screenH / 3f, paintBrush)
            canvas.drawText("Score: ${gameEngine.getScore()}", screenW / 4f, screenH / 2.5f, paintBrush)
            canvas.drawText("High Score: ${gameEngine.getHighScore()}", screenW / 4f, screenH / 2.2f, paintBrush)
        }
    }
}