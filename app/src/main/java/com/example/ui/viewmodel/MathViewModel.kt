package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.CompletedLessonEntity
import com.example.data.local.UserProgressEntity
import com.example.data.model.*
import com.example.data.repository.CurriculumRepository
import com.example.data.repository.MathRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class Screen {
    object Launchpad : Screen()
    object GalaxyMap : Screen()
    data class PlanetStation(val planetId: String) : Screen()
    object MentalSprint : Screen()
    object Badges : Screen()
}

data class PlanetStationState(
    val planet: PlanetLesson,
    val step: StepType = StepType.EXPLORE,
    val practiceIndex: Int = 0,
    val practiceSelectedAnswer: Int? = null,
    val practiceShowFeedback: Boolean = false,
    val practiceIsCorrect: Boolean = false,
    val practiceCorrectAnswersCount: Int = 0,
    val problemStepIndex: Int = 0,
    val problemSelectedAnswer: Int? = null,
    val problemShowFeedback: Boolean = false,
    val problemIsCorrect: Boolean = false,
    val problemFinalSelected: Int? = null,
    val problemFinalShowFeedback: Boolean = false,
    val problemFinalIsCorrect: Boolean = false,
    val starsAwarded: Int = 0,
    val pointsAwarded: Int = 0
)

data class MentalSprintState(
    val currentIndex: Int = 0,
    val selectedAnswer: Int? = null,
    val showFeedback: Boolean = false,
    val isCorrect: Boolean = false,
    val score: Int = 0,
    val isCompleted: Boolean = false
)

class MathViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MathRepository
    val userProgress: StateFlow<UserProgressEntity>
    val completedLessons: StateFlow<List<CompletedLessonEntity>>

    private val _currentScreen = MutableStateFlow<Screen>(Screen.Launchpad)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _selectedTrimester = MutableStateFlow(Trimester.TRIMESTER_1)
    val selectedTrimester: StateFlow<Trimester> = _selectedTrimester.asStateFlow()

    private val _planetStationState = MutableStateFlow<PlanetStationState?>(null)
    val planetStationState: StateFlow<PlanetStationState?> = _planetStationState.asStateFlow()

    private val _mentalSprintState = MutableStateFlow(MentalSprintState())
    val mentalSprintState: StateFlow<MentalSprintState> = _mentalSprintState.asStateFlow()

    init {
        val db = AppDatabase.getDatabase(application)
        repository = MathRepository(db.mathDao())

        userProgress = repository.userProgress
            .map { it ?: UserProgressEntity() }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = UserProgressEntity()
            )

        completedLessons = repository.completedLessons
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

        viewModelScope.launch {
            repository.initializeProgressIfEmpty()
        }
    }

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
        if (screen is Screen.PlanetStation) {
            val planet = CurriculumRepository.getPlanetById(screen.planetId)
            if (planet != null) {
                _planetStationState.value = PlanetStationState(planet = planet)
            }
        }
    }

    fun navigateBack() {
        when (_currentScreen.value) {
            is Screen.PlanetStation -> _currentScreen.value = Screen.GalaxyMap
            is Screen.GalaxyMap, is Screen.MentalSprint, is Screen.Badges -> _currentScreen.value = Screen.Launchpad
            is Screen.Launchpad -> { /* Keep at launchpad */ }
        }
    }

    fun selectTrimester(trimester: Trimester) {
        _selectedTrimester.value = trimester
        viewModelScope.launch {
            repository.updateTrimester(trimester.id)
        }
    }

    fun setPlanetStep(step: StepType) {
        _planetStationState.update { it?.copy(step = step) }
    }

    fun selectPracticeAnswer(index: Int) {
        val current = _planetStationState.value ?: return
        if (current.practiceShowFeedback) return

        val question = current.planet.practiceQuestions.getOrNull(current.practiceIndex) ?: return
        val isCorrect = (index == question.correctIndex)
        val newCount = if (isCorrect) current.practiceCorrectAnswersCount + 1 else current.practiceCorrectAnswersCount

        _planetStationState.update {
            it?.copy(
                practiceSelectedAnswer = index,
                practiceShowFeedback = true,
                practiceIsCorrect = isCorrect,
                practiceCorrectAnswersCount = newCount
            )
        }
    }

    fun nextPracticeQuestion() {
        val current = _planetStationState.value ?: return
        val nextIdx = current.practiceIndex + 1
        if (nextIdx < current.planet.practiceQuestions.size) {
            _planetStationState.update {
                it?.copy(
                    practiceIndex = nextIdx,
                    practiceSelectedAnswer = null,
                    practiceShowFeedback = false,
                    practiceIsCorrect = false
                )
            }
        } else {
            // Done with practice questions, move to problem step!
            _planetStationState.update {
                it?.copy(
                    step = StepType.PROBLEM,
                    practiceIndex = 0,
                    practiceSelectedAnswer = null,
                    practiceShowFeedback = false
                )
            }
        }
    }

    fun selectProblemStepAnswer(index: Int) {
        val current = _planetStationState.value ?: return
        if (current.problemShowFeedback) return

        val step = current.planet.problemChallenge.steps.getOrNull(current.problemStepIndex) ?: return
        val isCorrect = (index == step.correctIndex)

        _planetStationState.update {
            it?.copy(
                problemSelectedAnswer = index,
                problemShowFeedback = true,
                problemIsCorrect = isCorrect
            )
        }
    }

    fun nextProblemStep() {
        val current = _planetStationState.value ?: return
        val nextIdx = current.problemStepIndex + 1
        if (nextIdx < current.planet.problemChallenge.steps.size) {
            _planetStationState.update {
                it?.copy(
                    problemStepIndex = nextIdx,
                    problemSelectedAnswer = null,
                    problemShowFeedback = false,
                    problemIsCorrect = false
                )
            }
        } else {
            // Proceed to final problem question
            _planetStationState.update {
                it?.copy(
                    problemStepIndex = current.planet.problemChallenge.steps.size, // signifies final question
                    problemSelectedAnswer = null,
                    problemShowFeedback = false
                )
            }
        }
    }

    fun selectProblemFinalAnswer(index: Int) {
        val current = _planetStationState.value ?: return
        if (current.problemFinalShowFeedback) return

        val isCorrect = (index == current.planet.problemChallenge.finalCorrectIndex)
        val practiceRatio = current.practiceCorrectAnswersCount.toFloat() / maxOf(1, current.planet.practiceQuestions.size)
        val stars = if (isCorrect && practiceRatio >= 0.7f) 3 else if (isCorrect) 2 else 1
        val points = current.planet.pointsReward + (stars * 20)

        _planetStationState.update {
            it?.copy(
                problemFinalSelected = index,
                problemFinalShowFeedback = true,
                problemFinalIsCorrect = isCorrect,
                starsAwarded = stars,
                pointsAwarded = points
            )
        }

        viewModelScope.launch {
            repository.completeLesson(current.planet.id, stars, points)
        }
    }

    fun finishLessonAndCelebrate() {
        _planetStationState.update { it?.copy(step = StepType.CELEBRATION) }
    }

    // Mental Sprint logic
    fun startMentalSprint() {
        _mentalSprintState.value = MentalSprintState()
        navigateTo(Screen.MentalSprint)
    }

    fun selectSprintAnswer(index: Int) {
        val current = _mentalSprintState.value
        if (current.showFeedback || current.isCompleted) return

        val item = CurriculumRepository.mentalMathSprintList.getOrNull(current.currentIndex) ?: return
        val isCorrect = (index == item.correctIndex)
        val newScore = if (isCorrect) current.score + 25 else current.score

        _mentalSprintState.update {
            it.copy(
                selectedAnswer = index,
                showFeedback = true,
                isCorrect = isCorrect,
                score = newScore
            )
        }
    }

    fun nextSprintQuestion() {
        val current = _mentalSprintState.value
        val nextIdx = current.currentIndex + 1
        if (nextIdx < CurriculumRepository.mentalMathSprintList.size) {
            _mentalSprintState.update {
                it.copy(
                    currentIndex = nextIdx,
                    selectedAnswer = null,
                    showFeedback = false,
                    isCorrect = false
                )
            }
        } else {
            _mentalSprintState.update { it.copy(isCompleted = true) }
            viewModelScope.launch {
                repository.addSprintPoints(current.score)
            }
        }
    }
}
