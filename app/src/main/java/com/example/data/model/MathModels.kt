package com.example.data.model

enum class Trimester(val id: Int, val title: String, val subtitle: String) {
    TRIMESTER_1(1, "المجرّة الأولى (الثلاثي الأول)", "عالم الأعداد الكبيرة والعمليات وقيس الأطوال والهندسة"),
    TRIMESTER_2(2, "المجرّة الثانية (الثلاثي الثاني)", "عالم الأعداد العشرية والكسور والكتل والسعات والمحيط"),
    TRIMESTER_3(3, "المجرّة الثالثة (الثلاثي الثالث)", "عالم التناسب والنسب المئوية والمساحات وحساب الزمن")
}

enum class SubjectCategory(val title: String, val badgeColorHex: Long) {
    NUMBERS("الأعداد والحساب", 0xFF00E5BC),
    OPERATIONS("العمليات الأساسية", 0xFFFF7A30),
    GEOMETRY("الهندسة الفضائية", 0xFF9B51E0),
    MEASUREMENT("أنظمة القيس", 0xFF2D9CDB),
    PROBLEM_SOLVING("المسألة والإدماج", 0xFFFFD23F)
}

enum class StepType {
    EXPLORE,      // الاستكشاف والفهم
    PRACTICE,     // التمرن المباشر
    PROBLEM,      // المسألة والتوظيف
    CELEBRATION   // التتويج والمكافأة
}

data class Question(
    val id: String,
    val text: String,
    val options: List<String>,
    val correctIndex: Int,
    val hint: String,
    val educationalNote: String = ""
)

data class ProblemStep(
    val stepTitle: String,
    val stepQuestion: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

data class ProblemChallenge(
    val id: String,
    val title: String,
    val storyContext: String,
    val dataItems: List<String>,
    val steps: List<ProblemStep>,
    val finalQuestion: String,
    val finalOptions: List<String>,
    val finalCorrectIndex: Int,
    val solutionSummary: String
)

data class PlanetLesson(
    val id: String,
    val trimester: Trimester,
    val category: SubjectCategory,
    val title: String,
    val subtitle: String,
    val planetColorHex: Long,
    val conceptTitle: String,
    val conceptExplanation: List<String>,
    val keyRule: String,
    val practiceQuestions: List<Question>,
    val problemChallenge: ProblemChallenge,
    val pointsReward: Int = 100,
    val starsMax: Int = 3
)

data class BadgeItem(
    val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val requiredStars: Int
)

data class MentalMathItem(
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val strategy: String
)
