// MainActivity.kt
package com.example.project5

import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Rect
import java.util.Timer

class MainActivity : AppCompatActivity() {
    private lateinit var gameView: GameView
    private lateinit var gestureHandler: GestureDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun refreshGameLogic() {
        val activeGame = gameView.getGame()
        if (!activeGame.isGameOver()) {
            activeGame.moveBall()
        }
    }

    fun refreshGameDisplay() {
        gameView.postInvalidate()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        initializeGameComponents()
    }

    private fun initializeGameComponents() {
        val deviceWidth = resources.displayMetrics.widthPixels
        val deviceHeight = resources.displayMetrics.heightPixels
        val statusFrame = Rect(0, 0, 0, 0)
        window.decorView.getWindowVisibleDisplayFrame(statusFrame)
        val statusBarHeight = statusFrame.top

        gameView = GameView(this, deviceWidth, deviceHeight - statusBarHeight)
        setContentView(gameView)

        gestureHandler = GestureDetector(this, PaddleScrollHandler())

        val updateTimer = Timer()
        val loopTask = GameTimerTask(this)
        updateTimer.schedule(loopTask, 0, 100)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            gameView.getGame().startBallMovement()
        }
        gestureHandler.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    inner class PaddleScrollHandler : GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            gameView.getGame().movePaddle(distanceX)
            return true
        }
    }
}