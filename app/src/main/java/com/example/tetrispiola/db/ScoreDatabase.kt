package com.example.tetrispiola.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.tetrispiola.db.dao.ScoreDao
import com.example.tetrispiola.db.models.Score

@Database(entities = [Score::class], version = 1)
abstract class ScoreDatabase : RoomDatabase() {
    abstract fun scoreDao(): ScoreDao

    companion object {
        @Volatile private var instance: ScoreDatabase? = null

        fun getDatabase(context: Context): ScoreDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    ScoreDatabase::class.java,
                    "score_db"
                ).build().also { instance = it }
            }
        }
    }
}