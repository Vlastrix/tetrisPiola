package com.example.tetrispiola.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tetrispiola.databinding.ActivityMainBinding
import com.example.tetrispiola.ui.viewmodels.TetrisViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: TetrisViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.tetrisView.setViewModel(viewModel)

        viewModel.linesClearedListener = {
            binding.tvScore.text = "Score: ${viewModel.score}"
            binding.tvLevel.text = "Level: ${viewModel.level}"
            binding.tetrisView.restartAutoFall()
        }
        viewModel.gameOverListener = {
            val intent = Intent(this, ScoreActivity::class.java).apply {
                putExtra("finalScore", viewModel.score)
            }
            startActivity(intent)
            finish()
        }
    }
}