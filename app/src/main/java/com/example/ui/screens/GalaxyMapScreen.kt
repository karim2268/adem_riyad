package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CompletedLessonEntity
import com.example.data.local.UserProgressEntity
import com.example.data.model.PlanetLesson
import com.example.data.model.Trimester
import com.example.data.repository.CurriculumRepository
import com.example.ui.components.CosmicBackground
import com.example.ui.components.TopBarHeader
import com.example.ui.theme.*

@Composable
fun GalaxyMapScreen(
    progress: UserProgressEntity,
    completedLessons: List<CompletedLessonEntity>,
    selectedTrimester: Trimester,
    onTrimesterSelected: (Trimester) -> Unit,
    onPlanetClick: (PlanetLesson) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val completedMap = completedLessons.associateBy { it.lessonId }
    val planetsInTrimester = CurriculumRepository.getPlanetsForTrimester(selectedTrimester)

    CosmicBackground {
        Column(modifier = modifier.fillMaxSize()) {
            TopBarHeader(
                progress = progress,
                title = "خريطة المجرات الفضائية",
                onBackClick = onBackClick
            )

            // Trimester Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(SpaceCardSurface.copy(alpha = 0.8f))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Trimester.values().forEach { trim ->
                    val isSelected = trim == selectedTrimester
                    val isTrimesterOpen = when (trim) {
                        Trimester.TRIMESTER_1 -> true
                        Trimester.TRIMESTER_2 -> completedLessons.count { completed ->
                            CurriculumRepository.planets.any { it.id == completed.lessonId && it.trimester == Trimester.TRIMESTER_1 }
                        } >= 4
                        Trimester.TRIMESTER_3 -> completedLessons.count { completed ->
                            CurriculumRepository.planets.any { it.id == completed.lessonId && it.trimester == Trimester.TRIMESTER_2 }
                        } >= 3
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) CosmicCyan else Color.Transparent
                            )
                            .clickable { onTrimesterSelected(trim) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (!isTrimesterOpen) {
                                Text(text = "🔒 ", fontSize = 11.sp)
                            }
                            Text(
                                text = when (trim) {
                                    Trimester.TRIMESTER_1 -> "الثلاثي 1"
                                    Trimester.TRIMESTER_2 -> "الثلاثي 2"
                                    Trimester.TRIMESTER_3 -> "الثلاثي 3"
                                },
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color(0xFF0B1021) else TextMuted
                            )
                        }
                    }
                }
            }

            // Trimester Subtitle
            Text(
                text = selectedTrimester.subtitle,
                fontSize = 12.sp,
                color = CosmicCyan.copy(alpha = 0.9f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp)
            )

            // List of Planet Stations in this galaxy
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                itemsIndexed(planetsInTrimester) { index, planet ->
                    val completed = completedMap[planet.id]
                    val isCompleted = completed != null
                    val starsEarned = completed?.starsEarned ?: 0

                    // Unlock logic:
                    // In Trimester 1: first is unlocked; subsequent unlocked if previous completed
                    // Or if lastUnlockedPlanetId reached it
                    val isUnlocked = when {
                        selectedTrimester == Trimester.TRIMESTER_1 && index == 0 -> true
                        completedMap.containsKey(planet.id) -> true
                        index > 0 && completedMap.containsKey(planetsInTrimester[index - 1].id) -> true
                        selectedTrimester == Trimester.TRIMESTER_1 -> true // Keep T1 fully open for exploration
                        else -> completedLessons.isNotEmpty() // T2/T3 open as kid progresses
                    }

                    PlanetNodeCard(
                        planet = planet,
                        isUnlocked = isUnlocked,
                        isCompleted = isCompleted,
                        starsEarned = starsEarned,
                        onClick = {
                            if (isUnlocked) {
                                onPlanetClick(planet)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PlanetNodeCard(
    planet: PlanetLesson,
    isUnlocked: Boolean,
    isCompleted: Boolean,
    starsEarned: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("planet_card_${planet.id}")
            .clickable(enabled = isUnlocked) { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) SpaceCardSurface.copy(alpha = 0.92f) else SpaceCardSurface.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(20.dp),
        border = if (isCompleted) {
            androidx.compose.foundation.BorderStroke(1.5.dp, CosmicCyan.copy(alpha = 0.6f))
        } else if (isUnlocked) {
            androidx.compose.foundation.BorderStroke(1.dp, SpaceCardBorder)
        } else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Planet Avatar Icon
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(CircleShape)
                    .background(Color(planet.planetColorHex).copy(alpha = if (isUnlocked) 0.25f else 0.1f))
                    .border(
                        width = 2.dp,
                        color = if (isUnlocked) Color(planet.planetColorHex) else Color.Gray.copy(alpha = 0.4f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isUnlocked) {
                    Text(
                        text = when (planet.category) {
                            com.example.data.model.SubjectCategory.NUMBERS -> "🪐"
                            com.example.data.model.SubjectCategory.OPERATIONS -> "⚡"
                            com.example.data.model.SubjectCategory.GEOMETRY -> "📐"
                            com.example.data.model.SubjectCategory.MEASUREMENT -> "⚖️"
                            com.example.data.model.SubjectCategory.PROBLEM_SOLVING -> "🏆"
                        },
                        fontSize = 28.sp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "مغلق",
                        tint = TextMuted,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                // Category Chip
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(planet.category.badgeColorHex).copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = planet.category.title,
                        color = Color(planet.category.badgeColorHex),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = planet.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isUnlocked) TextWhite else TextMuted
                )

                Text(
                    text = planet.subtitle,
                    fontSize = 11.sp,
                    color = TextMuted,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Stars Display
                if (isUnlocked) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        for (i in 1..3) {
                            Icon(
                                imageVector = if (i <= starsEarned) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = null,
                                tint = if (i <= starsEarned) StarGold else TextMuted.copy(alpha = 0.4f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        if (isCompleted) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "مكتمل بنجاح",
                                fontSize = 10.sp,
                                color = CosmicCyan,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Arrow / Points
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "+${planet.pointsReward}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SupernovaOrange
                )
                Text(
                    text = "نقطة",
                    fontSize = 10.sp,
                    color = TextMuted
                )
            }
        }
    }
}
