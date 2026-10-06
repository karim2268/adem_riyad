package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_progress")
data class UserProgressEntity(
    @PrimaryKey val id: Int = 1,
    val childName: String = "آدم",
    val totalScore: Int = 0,
    val totalStars: Int = 0,
    val currentTrimester: Int = 1,
    val lastUnlockedPlanetId: String = "T1_P01",
    val unlockedBadges: String = "" // comma separated ids
)

@Entity(tableName = "completed_lessons")
data class CompletedLessonEntity(
    @PrimaryKey val lessonId: String,
    val starsEarned: Int,
    val scoreEarned: Int,
    val completedAt: Long = System.currentTimeMillis()
)
