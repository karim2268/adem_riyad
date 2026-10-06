package com.example.data.repository

import com.example.data.local.CompletedLessonEntity
import com.example.data.local.MathDao
import com.example.data.local.UserProgressEntity
import com.example.data.model.BadgeItem
import com.example.data.model.PlanetLesson
import com.example.data.model.Trimester
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class MathRepository(private val mathDao: MathDao) {

    val userProgress: Flow<UserProgressEntity?> = mathDao.getUserProgress()
    val completedLessons: Flow<List<CompletedLessonEntity>> = mathDao.getAllCompletedLessons()

    suspend fun initializeProgressIfEmpty() {
        val current = mathDao.getUserProgress().firstOrNull()
        if (current == null) {
            mathDao.insertOrUpdateProgress(
                UserProgressEntity(
                    id = 1,
                    childName = "آدم",
                    totalScore = 50,
                    totalStars = 0,
                    currentTrimester = 1,
                    lastUnlockedPlanetId = "T1_P01",
                    unlockedBadges = "B01"
                )
            )
        }
    }

    suspend fun completeLesson(lessonId: String, stars: Int, points: Int) {
        val currentProgress = mathDao.getUserProgress().firstOrNull() ?: UserProgressEntity()
        val existingCompleted = mathDao.getCompletedLesson(lessonId)

        val previousStars = existingCompleted?.starsEarned ?: 0
        val starsDiff = maxOf(0, stars - previousStars)

        mathDao.saveCompletedLesson(
            CompletedLessonEntity(
                lessonId = lessonId,
                starsEarned = maxOf(stars, previousStars),
                scoreEarned = points
            )
        )

        // Find next planet to unlock
        val allPlanets = CurriculumRepository.planets
        val currentIndex = allPlanets.indexOfFirst { it.id == lessonId }
        val nextPlanetId = if (currentIndex != -1 && currentIndex + 1 < allPlanets.size) {
            allPlanets[currentIndex + 1].id
        } else {
            currentProgress.lastUnlockedPlanetId
        }

        // Calculate badges
        val newTotalStars = currentProgress.totalStars + starsDiff
        val newScore = currentProgress.totalScore + points
        val currentBadgesSet = currentProgress.unlockedBadges.split(",").filter { it.isNotBlank() }.toMutableSet()
        
        CurriculumRepository.badges.forEach { badge ->
            if (newTotalStars >= badge.requiredStars) {
                currentBadgesSet.add(badge.id)
            }
        }

        mathDao.insertOrUpdateProgress(
            currentProgress.copy(
                totalScore = newScore,
                totalStars = newTotalStars,
                lastUnlockedPlanetId = nextPlanetId,
                unlockedBadges = currentBadgesSet.joinToString(",")
            )
        )
    }

    suspend fun addSprintPoints(points: Int) {
        val currentProgress = mathDao.getUserProgress().firstOrNull() ?: UserProgressEntity()
        mathDao.insertOrUpdateProgress(
            currentProgress.copy(
                totalScore = currentProgress.totalScore + points
            )
        )
    }

    suspend fun updateTrimester(trimesterId: Int) {
        val currentProgress = mathDao.getUserProgress().firstOrNull() ?: UserProgressEntity()
        mathDao.insertOrUpdateProgress(
            currentProgress.copy(currentTrimester = trimesterId)
        )
    }
}
