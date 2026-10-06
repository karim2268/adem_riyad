package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UserProgressEntity
import com.example.data.model.StepType
import com.example.ui.components.CosmicBackground
import com.example.ui.components.TopBarHeader
import com.example.ui.theme.*
import com.example.ui.viewmodel.PlanetStationState

@Composable
fun PlanetStationScreen(
    state: PlanetStationState,
    progress: UserProgressEntity,
    onStepSelected: (StepType) -> Unit,
    onSelectPracticeAnswer: (Int) -> Unit,
    onNextPracticeQuestion: () -> Unit,
    onSelectProblemStepAnswer: (Int) -> Unit,
    onNextProblemStep: () -> Unit,
    onSelectProblemFinalAnswer: (Int) -> Unit,
    onFinishLesson: () -> Unit,
    onReturnToGalaxy: () -> Unit,
    modifier: Modifier = Modifier
) {
    val planet = state.planet

    CosmicBackground {
        Column(modifier = modifier.fillMaxSize()) {
            TopBarHeader(
                progress = progress,
                title = planet.title,
                onBackClick = onReturnToGalaxy
            )

            // Step Progress Indicator Bar (4 Phases)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(SpaceCardSurface.copy(alpha = 0.8f))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val steps = listOf(
                    StepType.EXPLORE to "1. الفهم 📖",
                    StepType.PRACTICE to "2. التمرن ✍️",
                    StepType.PROBLEM to "3. المسألة 🧩",
                    StepType.CELEBRATION to "4. التتويج 🏆"
                )

                steps.forEach { (stepType, stepTitle) ->
                    val isCurrent = state.step == stepType
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isCurrent) CosmicCyan else Color.Transparent)
                            .clickable { onStepSelected(stepType) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stepTitle,
                            fontSize = 11.sp,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                            color = if (isCurrent) Color(0xFF0B1021) else TextMuted,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Phase Content Area
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                when (state.step) {
                    StepType.EXPLORE -> ExploreStepView(
                        state = state,
                        onStartPractice = { onStepSelected(StepType.PRACTICE) }
                    )
                    StepType.PRACTICE -> PracticeStepView(
                        state = state,
                        onSelectAnswer = onSelectPracticeAnswer,
                        onNextQuestion = onNextPracticeQuestion
                    )
                    StepType.PROBLEM -> ProblemStepView(
                        state = state,
                        onSelectStepAnswer = onSelectProblemStepAnswer,
                        onNextProblemStep = onNextProblemStep,
                        onSelectFinalAnswer = onSelectProblemFinalAnswer,
                        onProceedToCelebration = onFinishLesson
                    )
                    StepType.CELEBRATION -> CelebrationStepView(
                        state = state,
                        progress = progress,
                        onReturnToGalaxy = onReturnToGalaxy
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// STEP 1: EXPLORE & CONCEPT DISCOVERY
// -------------------------------------------------------------
@Composable
fun ExploreStepView(
    state: PlanetStationState,
    onStartPractice: () -> Unit
) {
    val planet = state.planet

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SpaceCardSurface.copy(alpha = 0.95f)),
            shape = RoundedCornerShape(22.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color(planet.planetColorHex).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🔭", fontSize = 22.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = planet.conceptTitle,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Text(
                            text = planet.category.title,
                            fontSize = 12.sp,
                            color = Color(planet.category.badgeColorHex)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Explanations bullets
                planet.conceptExplanation.forEachIndexed { idx, point ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "•",
                            color = CosmicCyan,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = point,
                            fontSize = 14.sp,
                            color = TextWhite,
                            lineHeight = 22.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Golden Rule Box
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = StarGold.copy(alpha = 0.12f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, StarGold.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "⭐", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = planet.keyRule,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = StarGold,
                            lineHeight = 19.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // CTA Button to advance to Practice
        Button(
            onClick = onStartPractice,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("start_practice_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = CosmicCyan,
                contentColor = Color(0xFF0B1021)
            ),
            shape = RoundedCornerShape(18.dp)
        ) {
            Text(
                text = "جاهز للتمرن يا آدم! انطلق ✍️",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// -------------------------------------------------------------
// STEP 2: PRACTICE & IMMEDIATE FEEDBACK
// -------------------------------------------------------------
@Composable
fun PracticeStepView(
    state: PlanetStationState,
    onSelectAnswer: (Int) -> Unit,
    onNextQuestion: () -> Unit
) {
    val planet = state.planet
    val question = planet.practiceQuestions.getOrNull(state.practiceIndex) ?: return
    val totalQuestions = planet.practiceQuestions.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp)
    ) {
        // Counter Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "تدريب رواد الفضاء: سؤال ${state.practiceIndex + 1} من $totalQuestions",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = CosmicCyan
            )
            Text(
                text = "الإجابات الصحيحة: ${state.practiceCorrectAnswersCount}",
                fontSize = 12.sp,
                color = StarGold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Question Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SpaceCardSurface.copy(alpha = 0.95f)),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, SpaceCardBorder)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = question.text,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite,
                    lineHeight = 24.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Options List
        question.options.forEachIndexed { index, option ->
            val isSelected = state.practiceSelectedAnswer == index
            val isCorrectOption = index == question.correctIndex

            val backgroundColor = when {
                !state.practiceShowFeedback && isSelected -> CosmicCyan.copy(alpha = 0.25f)
                state.practiceShowFeedback && isCorrectOption -> EmeraldSuccess.copy(alpha = 0.25f)
                state.practiceShowFeedback && isSelected && !state.practiceIsCorrect -> LaserRose.copy(alpha = 0.25f)
                else -> SpaceCardSurface.copy(alpha = 0.7f)
            }

            val borderColor = when {
                state.practiceShowFeedback && isCorrectOption -> EmeraldSuccess
                state.practiceShowFeedback && isSelected && !state.practiceIsCorrect -> LaserRose
                isSelected -> CosmicCyan
                else -> SpaceCardBorder
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .testTag("practice_option_${index}")
                    .clickable(enabled = !state.practiceShowFeedback) {
                        onSelectAnswer(index)
                    },
                colors = CardDefaults.cardColors(containerColor = backgroundColor),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, borderColor)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(borderColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${('أ'.code + index).toChar()}",
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = option,
                        fontSize = 15.sp,
                        color = TextWhite,
                        modifier = Modifier.weight(1f)
                    )

                    if (state.practiceShowFeedback) {
                        if (isCorrectOption) {
                            Text(text = "✅", fontSize = 18.sp)
                        } else if (isSelected) {
                            Text(text = "❌", fontSize = 18.sp)
                        }
                    }
                }
            }
        }

        // Didactic Feedback Message
        AnimatedVisibility(visible = state.practiceShowFeedback) {
            Column(modifier = Modifier.padding(top = 14.dp)) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (state.practiceIsCorrect)
                            EmeraldSuccess.copy(alpha = 0.15f)
                        else
                            LaserRose.copy(alpha = 0.15f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (state.practiceIsCorrect) EmeraldSuccess else LaserRose
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (state.practiceIsCorrect) "🌟 أحسنت يا بطل آدم!" else "💡 تلميح رياضي لآدم:",
                                fontWeight = FontWeight.Bold,
                                color = if (state.practiceIsCorrect) EmeraldSuccess else StarGold,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (state.practiceIsCorrect) {
                                if (question.educationalNote.isNotBlank()) question.educationalNote else "إجابة صحيحة ومتقنة وفق القواعد الرسمية!"
                            } else {
                                question.hint
                            },
                            fontSize = 13.sp,
                            color = TextWhite,
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onNextQuestion,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("next_practice_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SupernovaOrange,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = if (state.practiceIndex + 1 < totalQuestions)
                            "السؤال التالي ➔"
                        else
                            "الانتقال إلى مسألة التحدي الفضائية 🧩",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// STEP 3: AUTHENTIC TUNISIAN WORD PROBLEM
// -------------------------------------------------------------
@Composable
fun ProblemStepView(
    state: PlanetStationState,
    onSelectStepAnswer: (Int) -> Unit,
    onNextProblemStep: () -> Unit,
    onSelectFinalAnswer: (Int) -> Unit,
    onProceedToCelebration: () -> Unit
) {
    val problem = state.planet.problemChallenge
    val steps = problem.steps
    val isFinalQuestion = state.problemStepIndex >= steps.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp)
    ) {
        // Problem Header & Story Context
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SpaceCardSurface.copy(alpha = 0.95f)),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, NebulaViolet.copy(alpha = 0.6f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🧩", fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = problem.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = StarGold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = problem.storyContext,
                    fontSize = 13.sp,
                    color = TextWhite,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Data items chips
                Text(
                    text = "المعطيات الأساسية:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = CosmicCyan
                )
                Spacer(modifier = Modifier.height(4.dp))
                problem.dataItems.forEach { data ->
                    Text(
                        text = "• $data",
                        fontSize = 12.sp,
                        color = TextMuted,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (!isFinalQuestion) {
            // Intermediate Step
            val currentStep = steps[state.problemStepIndex]

            Text(
                text = "المرحلة ${state.problemStepIndex + 1} من ${steps.size}: ${currentStep.stepTitle}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = CosmicCyan
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SpaceCardSurface.copy(alpha = 0.85f)),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = currentStep.stepQuestion,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            currentStep.options.forEachIndexed { idx, opt ->
                val isSelected = state.problemSelectedAnswer == idx
                val isCorrect = idx == currentStep.correctIndex

                val bg = when {
                    state.problemShowFeedback && isCorrect -> EmeraldSuccess.copy(alpha = 0.25f)
                    state.problemShowFeedback && isSelected && !state.problemIsCorrect -> LaserRose.copy(alpha = 0.25f)
                    isSelected -> CosmicCyan.copy(alpha = 0.2f)
                    else -> SpaceCardSurface.copy(alpha = 0.7f)
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .testTag("problem_step_opt_$idx")
                        .clickable(enabled = !state.problemShowFeedback) {
                            onSelectStepAnswer(idx)
                        },
                    colors = CardDefaults.cardColors(containerColor = bg),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (state.problemShowFeedback && isCorrect) EmeraldSuccess else SpaceCardBorder
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = opt,
                            fontSize = 14.sp,
                            color = TextWhite,
                            modifier = Modifier.weight(1f)
                        )
                        if (state.problemShowFeedback) {
                            Text(text = if (isCorrect) "✅" else if (isSelected) "❌" else "", fontSize = 16.sp)
                        }
                    }
                }
            }

            if (state.problemShowFeedback) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CosmicCyan.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = currentStep.explanation,
                        fontSize = 12.sp,
                        color = TextWhite,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onNextProblemStep,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("next_problem_step_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = CosmicCyan, contentColor = Color(0xFF0B1021)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(text = "متابعة حل المسألة ➔", fontWeight = FontWeight.Bold)
                }
            }
        } else {
            // Final Question of Problem
            Text(
                text = "السؤال الختامي الشامل للمسألة (معيار التميز مع 5):",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = StarGold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SpaceCardSurface.copy(alpha = 0.85f)),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = problem.finalQuestion,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            problem.finalOptions.forEachIndexed { idx, opt ->
                val isSelected = state.problemFinalSelected == idx
                val isCorrect = idx == problem.finalCorrectIndex

                val bg = when {
                    state.problemFinalShowFeedback && isCorrect -> EmeraldSuccess.copy(alpha = 0.25f)
                    state.problemFinalShowFeedback && isSelected && !state.problemFinalIsCorrect -> LaserRose.copy(alpha = 0.25f)
                    isSelected -> CosmicCyan.copy(alpha = 0.2f)
                    else -> SpaceCardSurface.copy(alpha = 0.7f)
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .testTag("problem_final_opt_$idx")
                        .clickable(enabled = !state.problemFinalShowFeedback) {
                            onSelectFinalAnswer(idx)
                        },
                    colors = CardDefaults.cardColors(containerColor = bg),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (state.problemFinalShowFeedback && isCorrect) EmeraldSuccess else SpaceCardBorder
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = opt,
                            fontSize = 14.sp,
                            color = TextWhite,
                            modifier = Modifier.weight(1f)
                        )
                        if (state.problemFinalShowFeedback) {
                            Text(text = if (isCorrect) "✅" else if (isSelected) "❌" else "", fontSize = 16.sp)
                        }
                    }
                }
            }

            if (state.problemFinalShowFeedback) {
                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = StarGold.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "حوصلة الحل الرسمي:",
                            fontWeight = FontWeight.Bold,
                            color = StarGold,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = problem.solutionSummary,
                            fontSize = 12.sp,
                            color = TextWhite,
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onProceedToCelebration,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("celebration_proceed_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = StarGold, contentColor = Color(0xFF0B1021)),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text(
                        text = "تتويج البطل آدم بالنجوم والأوسمة 🏆",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// STEP 4: CELEBRATION & STAR AWARD
// -------------------------------------------------------------
@Composable
fun CelebrationStepView(
    state: PlanetStationState,
    progress: UserProgressEntity,
    onReturnToGalaxy: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SpaceCardSurface.copy(alpha = 0.95f)),
            shape = RoundedCornerShape(24.dp),
            border = androidx.compose.foundation.BorderStroke(2.dp, StarGold.copy(alpha = 0.7f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(StarGold.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "👑", fontSize = 44.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "مبروك يا رائد الفضاء آدم!",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "تم فتح الكوكب وتجاوز التحدي بنجاح فائق!",
                    fontSize = 13.sp,
                    color = CosmicCyan,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Stars row
                Row(horizontalArrangement = Arrangement.Center) {
                    for (i in 1..3) {
                        Icon(
                            imageVector = if (i <= state.starsAwarded) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = null,
                            tint = if (i <= state.starsAwarded) StarGold else TextMuted,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Rewards pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(SupernovaOrange.copy(alpha = 0.2f))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "⚡ +${state.pointsAwarded} نقطة فضائية", color = SupernovaOrange, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onReturnToGalaxy,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("back_to_galaxy_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = CosmicCyan, contentColor = Color(0xFF0B1021)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(text = "متابعة الرحلة في خريطة المجرة 🪐", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
