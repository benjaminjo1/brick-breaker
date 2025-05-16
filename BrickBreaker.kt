package com.example.project5

import android.content.Context
import android.graphics.Rect
import android.media.SoundPool

class BrickBreaker(private val gameHeight: Int, private val gameWidth: Int, private val context: Context) {
    private lateinit var blockGrid: Array<Array<Rect?>>
    private lateinit var paddleBox: Rect

    private var ballPosX = 0f
    private var ballPosY = 0f
    private var velocity = 0f
    private var dirX = 0f
    private var dirY = 0f
    private var isBallInMotion = false
    private var ballSize = 0f

    private var currentPoints = 0
    private var maxPoints = 0
    private var totalBricks = 24
    private var hasGameEnded = false

    private var audioPool: SoundPool
    private var soundHitId: Int = 0

    init {
        val preferences = context.getSharedPreferences("BrickBreakerPrefs", Context.MODE_PRIVATE)
        maxPoints = preferences.getInt("high_score", 0)

        val builder = SoundPool.Builder()
        audioPool = builder.build()
        soundHitId = audioPool.load(context, R.raw.hit, 1)
    }

    fun setBallLocation(x: Float, y: Float) {
        ballPosX = x
        ballPosY = y
    }

    fun getBallX() = ballPosX
    fun getBallY() = ballPosY
    fun getBallRadius() = ballSize
    fun getScore() = currentPoints
    fun getHighScore() = maxPoints
    fun isGameOver() = hasGameEnded
    fun setBallRadius(radius: Float) { ballSize = radius }
    fun setBallVelocity(speed: Float) { velocity = speed }
    fun getPaddle(): Rect = paddleBox

    fun setBricks(grid: Array<Array<Rect?>>) {
        blockGrid = grid
    }

    fun getBricks(): Array<Array<Rect?>> = blockGrid

    fun setPaddle(left: Int, top: Int, right: Int, bottom: Int) {
        paddleBox = Rect(left, top, right, bottom)
    }

    fun startBallMovement() {
        if (!isBallInMotion) {
            dirX = velocity
            dirY = velocity
            isBallInMotion = true
        }
    }

    fun moveBall() {
        if (isBallInMotion) {
            ballPosX += dirX
            ballPosY += dirY

            if (ballPosX - ballSize < 0 || ballPosX + ballSize > gameWidth) dirX *= -1
            if (ballPosY - ballSize < 0) dirY *= -1
        }

        for (row in blockGrid.indices) {
            for (col in blockGrid[row].indices) {
                val brick = blockGrid[row][col]
                if (brick != null && intersectsBrick(brick)) {
                    blockGrid[row][col] = null
                    dirY *= -1
                    currentPoints++
                    totalBricks--

                    if (totalBricks == 0) {
                        updateHighScore()
                        hasGameEnded = true
                    }
                    return
                }
            }
        }

        if (ballPosY + ballSize >= paddleBox.top &&
            ballPosX >= paddleBox.left &&
            ballPosX <= paddleBox.right) {
            dirY *= -1
            playBounceSound()
            ballPosY = paddleBox.top - ballSize
        }

        if (ballPosY > gameHeight) {
            updateHighScore()
            hasGameEnded = true
        }
    }

    fun movePaddle(distanceX: Float) {
        val newLeft = paddleBox.left - distanceX.toInt()
        val newRight = paddleBox.right - distanceX.toInt()
        if (newLeft >= 0 && newRight <= gameWidth) {
            paddleBox.offset(-distanceX.toInt(), 0)
        }
    }

    private fun intersectsBrick(brick: Rect): Boolean {
        return ballPosX + ballSize > brick.left &&
                ballPosX - ballSize < brick.right &&
                ballPosY + ballSize > brick.top &&
                ballPosY - ballSize < brick.bottom
    }

    private fun playBounceSound() {
        audioPool.play(soundHitId, 2.0f, 2.0f, 1, 0, 1.0f)
    }

    private fun updateHighScore() {
        if (currentPoints > maxPoints) {
            maxPoints = currentPoints
            saveHighScore()
        }
    }

    private fun saveHighScore() {
        val prefs = context.getSharedPreferences("BrickBreakerPrefs", Context.MODE_PRIVATE)
        prefs.edit().putInt("high_score", maxPoints).apply()
    }
}
