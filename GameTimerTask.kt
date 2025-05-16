package com.example.project5

import java.util.TimerTask

class GameTimerTask(private val mainController: MainActivity) : TimerTask() {
    override fun run() {
        mainController.refreshGameLogic()
        mainController.refreshGameDisplay()
    }
}