package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MathViewModel
import com.example.ui.viewmodel.Screen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AdamMathSpaceApp()
                }
            }
        }
    }
}

@Composable
fun AdamMathSpaceApp(
    viewModel: MathViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val progress by viewModel.userProgress.collectAsStateWithLifecycle()
    val completedLessons by viewModel.completedLessons.collectAsStateWithLifecycle()
    val selectedTrimester by viewModel.selectedTrimester.collectAsStateWithLifecycle()
    val planetStationState by viewModel.planetStationState.collectAsStateWithLifecycle()
    val mentalSprintState by viewModel.mentalSprintState.collectAsStateWithLifecycle()

    // Back handling for navigation
    if (currentScreen != Screen.Launchpad) {
        BackHandler {
            viewModel.navigateBack()
        }
    }

    when (val screen = currentScreen) {
        is Screen.Launchpad -> {
            LaunchpadScreen(
                progress = progress,
                completedLessons = completedLessons,
                onSelectTrimesterStation = { trimester ->
                    viewModel.selectTrimester(trimester)
                    viewModel.navigateTo(Screen.GalaxyMap)
                },
                onMentalSprintClick = { viewModel.startMentalSprint() },
                onBadgesClick = { viewModel.navigateTo(Screen.Badges) }
            )
        }

        is Screen.GalaxyMap -> {
            GalaxyMapScreen(
                progress = progress,
                completedLessons = completedLessons,
                selectedTrimester = selectedTrimester,
                onTrimesterSelected = { viewModel.selectTrimester(it) },
                onPlanetClick = { planet -> viewModel.navigateTo(Screen.PlanetStation(planet.id)) },
                onBackClick = { viewModel.navigateTo(Screen.Launchpad) }
            )
        }

        is Screen.PlanetStation -> {
            val state = planetStationState
            if (state != null) {
                PlanetStationScreen(
                    state = state,
                    progress = progress,
                    onStepSelected = { viewModel.setPlanetStep(it) },
                    onSelectPracticeAnswer = { viewModel.selectPracticeAnswer(it) },
                    onNextPracticeQuestion = { viewModel.nextPracticeQuestion() },
                    onSelectProblemStepAnswer = { viewModel.selectProblemStepAnswer(it) },
                    onNextProblemStep = { viewModel.nextProblemStep() },
                    onSelectProblemFinalAnswer = { viewModel.selectProblemFinalAnswer(it) },
                    onFinishLesson = { viewModel.finishLessonAndCelebrate() },
                    onReturnToGalaxy = { viewModel.navigateTo(Screen.GalaxyMap) }
                )
            } else {
                GalaxyMapScreen(
                    progress = progress,
                    completedLessons = completedLessons,
                    selectedTrimester = selectedTrimester,
                    onTrimesterSelected = { viewModel.selectTrimester(it) },
                    onPlanetClick = { planet -> viewModel.navigateTo(Screen.PlanetStation(planet.id)) },
                    onBackClick = { viewModel.navigateTo(Screen.Launchpad) }
                )
            }
        }

        is Screen.MentalSprint -> {
            MentalSprintScreen(
                state = mentalSprintState,
                progress = progress,
                onSelectAnswer = { viewModel.selectSprintAnswer(it) },
                onNextQuestion = { viewModel.nextSprintQuestion() },
                onBackClick = { viewModel.navigateTo(Screen.Launchpad) }
            )
        }

        is Screen.Badges -> {
            BadgesScreen(
                progress = progress,
                onBackClick = { viewModel.navigateTo(Screen.Launchpad) }
            )
        }
    }
}
