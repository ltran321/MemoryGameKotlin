package com.example.assignment3

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.get
import androidx.gridlayout.widget.GridLayout
import android.os.CountDownTimer
import android.os.Looper
import android.util.Log
import android.widget.TextView
import android.widget.Toast


class GameFrag : Fragment() {
    private lateinit var gridLayout: GridLayout
    private lateinit var timerTextView: TextView
    private lateinit var gameBoard: Array<Array<ImageView>>
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var scoreTextView: TextView
    private lateinit var selectedTiles: List<Pair<Int, Int>>

    private val userSelectedTiles = mutableListOf<Pair<Int, Int>>()
    private var isUserInteractionEnabled = false
    private var correctRoundsCount = 0
    private var selectedTilesCount = 4
    private var scoreMultiplier = 1

    private var score = 0
    private var lives = 3

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_game, container, false)

        gridLayout = view.findViewById(R.id.gameBoard)
        timerTextView = view.findViewById(R.id.timer)
        scoreTextView = view.findViewById(R.id.scoreText)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startGame()
    }

    private fun startGame() {
        // Create gameBoard from GridLayout
        gameBoard = Array(gridLayout.rowCount) { row ->
            Array(gridLayout.columnCount) { col ->
                val imageView = gridLayout[row * gridLayout.columnCount + col] as ImageView
                imageView
            }
        }
        // Start the first round
        startNewRound()
    }

    // Call this function when starting a new round
    private fun startNewRound() {
        showTilesAndStartTimer()
    }
    private fun showTilesAndStartTimer() {
        // Show mint tiles
        showMintTiles()
        // Start the countdown timer
        startCountDownTimer()
    }

    private fun showMintTiles() {
        // Get the coordinates of 4 random tiles
        selectedTiles = getRandomTiles(selectedTilesCount)

        // Change the images of the selected tiles to mint
        selectedTiles.forEach { (row, col) ->
            gameBoard[row][col].setImageResource(R.drawable.mint)
        }

        // Delay showing lavender till 4 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            // Change the images back to lavender after the delay
            selectedTiles.forEach { (row, col) ->
                gameBoard[row][col].setImageResource(R.drawable.lavender)
            }
        }, 4000)
    }

    private fun startCountDownTimer() {
        countDownTimer = object : CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // Update the timer text
                timerTextView.text = (millisUntilFinished / 1000).toString()
            }

            override fun onFinish() {
                // When the countdown is finished
                isUserInteractionEnabled = true
                setUpClickListeners()

                // Delay for a short duration
                Handler().postDelayed({
                    isUserInteractionEnabled = false

                    handleUserSelection()
                    // Clear the previous selected tiles along with userSelected tiles
                    selectedTiles = emptyList()
                    userSelectedTiles.clear()

                    startNewRound()
                }, 5000)

            }
        }
        // Start the countdown timer
        countDownTimer.start()
    }

    private fun getRandomTiles(count: Int): List<Pair<Int, Int>> {
        val selectedTiles = mutableListOf<Pair<Int, Int>>()

        // get random tiles within the grid layout
        while (selectedTiles.size < count) {
            val randomRow = (0 until gridLayout.rowCount).random()
            val randomCol = (0 until gridLayout.columnCount).random()
            val randomCoordinate = Pair(randomRow, randomCol)

            if (!selectedTiles.contains(randomCoordinate)) {
                selectedTiles.add(randomCoordinate)
            }
        }

        return selectedTiles
    }

    private fun handleUserSelection() {
        // Compare the user's selection with selectedTiles
        val userSelectedTiles = getUserSelectedTiles()

        Log.d("CompareTiles", "UserSelected: $userSelectedTiles, SelectedTiles(highlight): $selectedTiles")

        if (userSelectedTiles.size == selectedTiles.size && userSelectedTiles.containsAll(selectedTiles)) {
            // User made a correct selection
            correctRoundsCount++
            score += 10 * scoreMultiplier

            if(correctRoundsCount == 3){
                increaseDifficulty()
                correctRoundsCount = 0
            }
        } else {
            // User made an incorrect selection
            lives--

            // Check if there are remaining lives
            if (lives <= 0) {
                // If no lives remaining it's game over and navigate to highscore fragment
                gameOver()
            } else {
                // Tell user about wrong selection and number of lives remaining
                showToast("Incorrect selection! Lives remaining: $lives")
            }
        }
        // Update the score
        updateScore()
    }

    private fun setUpClickListeners() {
        // Set up click listeners for each ImageView in the gameBoard
        for (row in 0 until gridLayout.rowCount) {
            for (col in 0 until gridLayout.columnCount) {
                gameBoard[row][col].setOnClickListener {
                    // Check if user clicks allowed
                    if (isUserInteractionEnabled) {
                        handleImageViewClick(row, col)
                    }
                }
            }
        }
    }

    private fun handleImageViewClick(row: Int, col: Int) {
        // When an ImageView is clicked, add coordinates to user selection if unique
        val clickedCoordinate = Pair(row, col)

        if (!userSelectedTiles.contains(clickedCoordinate)) {
            userSelectedTiles.add(clickedCoordinate)
        }
    }

    private fun getUserSelectedTiles(): List<Pair<Int, Int>> {
        return userSelectedTiles
    }

    private fun updateScore() {
        scoreTextView.text = "Score: $score"
    }

    private fun gameOver() {

        val highScoreFragment = HighScoreFrag()

        // get username from arguments
        val username = arguments?.getString("username") ?: "aaaa"

        // Pass the username and score as arguments
        val args = Bundle()
        args.putString("username", username)
        args.putInt("score", score)

        highScoreFragment.arguments = args

        // Replace the game fragment with the HighScoreFrag
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, highScoreFragment)
            .commit()

        resetGameState()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun resetGameState() {
        // Reset variables for the game
        score = 0
        lives = 3
        userSelectedTiles.clear()
        selectedTiles = emptyList()
        selectedTilesCount = 4
        correctRoundsCount = 0
        scoreMultiplier = 1
    }

    private fun increaseDifficulty() {
        // Increase the number of selected tiles and score multiplier
        selectedTilesCount++
        scoreMultiplier++
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer.cancel()
    }
}