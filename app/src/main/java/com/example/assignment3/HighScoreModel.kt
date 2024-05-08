package com.example.assignment3

import android.content.Context

class HighScoreModel(context: Context) {

    fun highScoresString(highScores: List<Pair<String, Int>>): String {
        val sortedHighScores = highScores.sortedByDescending { it.second }
        val stringBuilder = StringBuilder()
        for ((index, highScore) in sortedHighScores.withIndex()) {
            stringBuilder.append("${index + 1}. ${highScore.first}: ${highScore.second}\n")
        }
        return stringBuilder.toString()
    }
}