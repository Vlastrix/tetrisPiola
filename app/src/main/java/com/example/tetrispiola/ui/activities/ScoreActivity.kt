package com.example.tetrispiola.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.tetrispiola.R
import com.example.tetrispiola.databinding.ActivityScoreBinding
import com.example.tetrispiola.db.ScoreDatabase
import com.example.tetrispiola.db.dao.ScoreDao
import com.example.tetrispiola.db.models.Score
import com.example.tetrispiola.ui.viewmodels.ScoreViewModel
import kotlinx.coroutines.launch

class ScoreActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScoreBinding
    private val viewModel: ScoreViewModel by viewModels()
    private var playerScore: Int = 0
    private var hasSavedScore = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playerScore = intent.getIntExtra("finalScore", 0)

        binding.btnSaveScore.setOnClickListener {
            val playerName = binding.etName.text.toString()
            if (playerName.isNotEmpty() && !hasSavedScore) {
                viewModel.saveScore(Score(name = playerName, score = playerScore))
                Toast.makeText(this, "Score saved!", Toast.LENGTH_SHORT).show()
                binding.btnSaveScore.isEnabled = false
                binding.etName.isEnabled = false
                hasSavedScore = true
            } else if (hasSavedScore) {
                Toast.makeText(this, "You already saved your score", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Specify a valid name", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnRestartGame.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }

        viewModel.getAllScores().observe(this) { scores ->
            binding.tvScoreList.text = scores.joinToString("\n") {
                "${it.name}: ${it.score}"
            }
        }
    }
}
