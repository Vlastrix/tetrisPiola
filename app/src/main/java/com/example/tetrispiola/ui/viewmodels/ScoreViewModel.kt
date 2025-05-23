package com.example.tetrispiola.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.tetrispiola.db.ScoreDatabase
import com.example.tetrispiola.db.models.Score
import kotlinx.coroutines.launch

class ScoreViewModel(application: Application) : AndroidViewModel(application) {

    private val scoreDao = ScoreDatabase.getDatabase(application).scoreDao()

    fun saveScore(score: Score) = viewModelScope.launch {
        scoreDao.insert(score)
    }

    fun getAllScores(): LiveData<List<Score>> =
        scoreDao.getAllScores().asLiveData()
}

