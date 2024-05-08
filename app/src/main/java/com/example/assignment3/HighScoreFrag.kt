package com.example.assignment3


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


class HighScoreFrag : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var highScoreTextView: TextView
    private lateinit var highScoreModel: HighScoreModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_high_score, container, false)

        highScoreTextView = view.findViewById(R.id.highScoreText)
        highScoreModel = HighScoreModel(requireContext())

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve username and score from arguments
        val username = arguments?.getString("username")
        val score = arguments?.getInt("score")

        // Load and display high scores
        displayHighScores()

        // Check if the current score is a high score
        if (isHighScore(score)) {
            // Save the new high score
            saveHighScore(username, score)
            // Display updated high scores
            displayHighScores()
        }
    }

    private fun displayHighScores() {
        // Load high scores from SharedPreferences
        sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val highScores = getHighScores()

        // Display high scores in TextView
        highScoreTextView.text = highScoreModel.highScoresString(highScores)
    }

    private fun getHighScores(): List<Pair<String, Int>> {
        // Load high scores from SharedPreferences
        val highScoresSet = sharedPreferences.getStringSet(HIGH_SCORES_KEY, setOf()) ?: setOf()

        // Convert the set to a list of pairs
        return highScoresSet.map { highScoreString ->
            val parts = highScoreString.split(":")
            if (parts.size == 2) {
                Pair(parts[0], parts[1].toInt())
            } else {
                Pair("", 0)
            }
        }
    }

    private fun isHighScore(score: Int?): Boolean {
        val highScores = getHighScores()

        val isHighScore = if (highScores.size < 3) {
            // If less than 3 high scores, current score is a high score so will be added
            true
        } else {
            // If already 3 high scores, check provided score higher than any of them
            highScores.any { score != null && score > it.second }
        }

        return isHighScore
    }

    private fun saveHighScore(username: String?, score: Int?) {

        // Save the new high score to SharedPreferences
        if (username != null && score != null) {
            val editor = sharedPreferences.edit()
            val highScores = getHighScores().toMutableList()

            // Add the new high score to the list
            highScores.add(Pair(username, score))

            // Sort the list by score in descending order
            highScores.sortByDescending { it.second }

            // Limit to top 3 high scores
            val limitedHighScores = highScores.take(3)

            // Convert the list to a set of strings and save to SharedPreferences
            editor.putStringSet(HIGH_SCORES_KEY, limitedHighScores.map { "${it.first}:${it.second}" }.toSet())
            editor.apply()
        }
    }

    companion object {
        private const val HIGH_SCORES_KEY = "highScores"
    }
}