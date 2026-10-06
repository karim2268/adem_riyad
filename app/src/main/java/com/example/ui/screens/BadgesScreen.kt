package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UserProgressEntity
import com.example.data.repository.CurriculumRepository
import com.example.ui.components.CosmicBackground
import com.example.ui.components.TopBarHeader
import com.example.ui.theme.*

@Composable
fun BadgesScreen(
    progress: UserProgressEntity,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val unlockedSet = progress.unlockedBadges.split(",").filter { it.isNotBlank() }.toSet()
    val allBadges = CurriculumRepository.badges

    CosmicBackground {
        Column(modifier = modifier.fillMaxSize()) {
            TopBarHeader(
                progress = progress,
                title = "معرض كؤوس وأوسمة آدم 🏆",
                onBackClick = onBackClick
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(allBadges) { badge ->
                    val isUnlocked = unlockedSet.contains(badge.id) || progress.totalStars >= badge.requiredStars

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isUnlocked) SpaceCardSurface.copy(alpha = 0.95f) else SpaceCardSurface.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(18.dp),
                        border = if (isUnlocked) androidx.compose.foundation.BorderStroke(1.dp, StarGold.copy(alpha = 0.5f)) else null
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(if (isUnlocked) StarGold.copy(alpha = 0.2f) else Color.Gray.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isUnlocked) {
                                    Text(text = badge.iconEmoji, fontSize = 28.sp)
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "مغلق",
                                        tint = TextMuted,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = badge.title,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isUnlocked) TextWhite else TextMuted
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = badge.description,
                                    fontSize = 12.sp,
                                    color = TextMuted
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = if (isUnlocked) StarGold else TextMuted,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "يتطلب ${badge.requiredStars} نجوم",
                                        fontSize = 11.sp,
                                        color = if (isUnlocked) CosmicCyan else TextMuted
                                    )
                                }
                            }

                            if (isUnlocked) {
                                Text(
                                    text = "مكتسب ✨",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StarGold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
