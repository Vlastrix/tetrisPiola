package com.example.tetrispiola.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.tetrispiola.db.models.Score
import kotlinx.coroutines.flow.Flow

@Dao
interface ScoreDao {
    @Insert
    suspend fun insert(score: Score)

    @Query("SELECT * FROM scores ORDER BY score DESC")
    fun getAllScores(): Flow<List<Score>>
}
