package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UserProgressEntity
import com.example.data.repository.CurriculumRepository
import com.example.ui.components.CosmicBackground
import com.example.ui.components.TopBarHeader
import com.example.ui.theme.*
import com.example.ui.viewmodel.MentalSprintState

@Composable
fun MentalSprintScreen(
    state: MentalSprintState,
    progress: UserProgressEntity,
    onSelectAnswer: (Int) -> Unit,
    onNextQuestion: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val items = CurriculumRepository.mentalMathSprintList
    val total = items.size
    val currentItem = items.getOrNull(state.currentIndex)

    CosmicBackground {
        Column(modifier = modifier.fillMaxSize()) {
            TopBarHeader(
                progress = progress,
                title = "صاروخ الحساب الذهني لآدم ⚡",
                onBackClick = onBackClick
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (state.isCompleted) {
                    // Completed Summary Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = SpaceCardSurface.copy(alpha = 0.95f)),
                        shape = RoundedCornerShape(24.dp),
                        border = androidx.compose.foundation.BorderStroke(2.dp, SupernovaOrange.copy(alpha = 0.6f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "🚀⚡", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "رائع يا آدم!",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                            Text(
                                text = "أنهيت صاروخ الحساب الذهني السريع",
                                fontSize = 13.sp,
                                color = CosmicCyan
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "النقاط المكتسبة: +${state.score} نقطة",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = SupernovaOrange
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = onBackClick,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CosmicCyan, contentColor = Color(0xFF0B1021)),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text(text = "العودة إلى قمرة القيادة 🏠", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else if (currentItem != null) {
                    // Active Question
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "السؤال ${state.currentIndex + 1} من $total",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = CosmicCyan
                        )
                        Text(
                            text = "النقاط: ${state.score}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = SupernovaOrange
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = SpaceCardSurface.copy(alpha = 0.95f)),
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SupernovaOrange.copy(alpha = 0.4f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = currentItem.question,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    currentItem.options.forEachIndexed { index, option ->
                        val isSelected = state.selectedAnswer == index
                        val isCorrect = index == currentItem.correctIndex

                        val bg = when {
                            state.showFeedback && isCorrect -> EmeraldSuccess.copy(alpha = 0.25f)
                            state.showFeedback && isSelected && !state.isCorrect -> LaserRose.copy(alpha = 0.25f)
                            isSelected -> CosmicCyan.copy(alpha = 0.2f)
                            else -> SpaceCardSurface.copy(alpha = 0.8f)
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp)
                                .testTag("sprint_opt_$index")
                                .clickable(enabled = !state.showFeedback) {
                                    onSelectAnswer(index)
                                },
                            colors = CardDefaults.cardColors(containerColor = bg),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (state.showFeedback && isCorrect) EmeraldSuccess else SpaceCardBorder
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = option,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextWhite,
                                    modifier = Modifier.weight(1f)
                                )
                                if (state.showFeedback) {
                                    Text(text = if (isCorrect) "✅" else if (isSelected) "❌" else "", fontSize = 16.sp)
                                }
                            }
                        }
                    }

                    if (state.showFeedback) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = StarGold.copy(alpha = 0.12f)),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(
                                text = "💡 استراتيجية الحساب السريع: ${currentItem.strategy}",
                                fontSize = 12.sp,
                                color = TextWhite,
                                modifier = Modifier.padding(12.dp),
                                lineHeight = 17.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = onNextQuestion,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("sprint_next_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = SupernovaOrange, contentColor = Color.White),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = if (state.currentIndex + 1 < total) "السؤال التالي ➔" else "عرض نتيجة الصاروخ 🏆",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
