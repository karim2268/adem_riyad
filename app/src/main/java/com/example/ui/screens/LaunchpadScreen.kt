package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.runtime.*
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
import com.example.data.local.CompletedLessonEntity
import com.example.data.local.UserProgressEntity
import com.example.data.model.Trimester
import com.example.data.repository.CurriculumRepository
import com.example.ui.components.CosmicBackground
import com.example.ui.components.TopBarHeader
import com.example.ui.theme.*

@Composable
fun LaunchpadScreen(
    progress: UserProgressEntity,
    completedLessons: List<CompletedLessonEntity>,
    onSelectTrimesterStation: (Trimester) -> Unit,
    onMentalSprintClick: () -> Unit,
    onBadgesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalPlanets = CurriculumRepository.planets.size
    val completedCount = completedLessons.size
    val progressPercent = if (totalPlanets > 0) completedCount.toFloat() / totalPlanets else 0f

    val completedT1Count = completedLessons.count { completed ->
        CurriculumRepository.planets.any { it.id == completed.lessonId && it.trimester == Trimester.TRIMESTER_1 }
    }
    val completedT2Count = completedLessons.count { completed ->
        CurriculumRepository.planets.any { it.id == completed.lessonId && it.trimester == Trimester.TRIMESTER_2 }
    }
    val completedT3Count = completedLessons.count { completed ->
        CurriculumRepository.planets.any { it.id == completed.lessonId && it.trimester == Trimester.TRIMESTER_3 }
    }

    // Modal or toast notice when clicking locked station
    var lockedDialogMessage by remember { mutableStateOf<String?>(null) }

    val rankTitle = when {
        completedCount >= 10 -> "قائد أسطول الرياضيات الأعظم 👑"
        completedCount >= 6 -> "أميرال الفضاء الرياضي 🚀"
        completedCount >= 3 -> "مستكشف الكواكب المتقدم 🪐"
        completedCount >= 1 -> "رائد فضاء الرياضيات البطل 🌟"
        else -> "رائد الفضاء الصاعد ✨"
    }

    CosmicBackground {
        Column(modifier = modifier.fillMaxSize()) {
            TopBarHeader(
                progress = progress,
                onTrophiesClick = onBadgesClick
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Astronaut Hero Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("launchpad_hero_card"),
                    colors = CardDefaults.cardColors(
                        containerColor = SpaceCardSurface.copy(alpha = 0.92f)
                    ),
                    shape = RoundedCornerShape(24.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SpaceCardBorder)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        CosmicCyan.copy(alpha = 0.15f),
                                        Color.Transparent
                                    )
                                )
                            )
                            .padding(18.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(CosmicCyan.copy(alpha = 0.3f), NebulaViolet.copy(alpha = 0.3f))
                                        )
                                    )
                                    .border(2.dp, CosmicCyan, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "👨‍🚀",
                                    fontSize = 34.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "مرحباً يا بطلنا آدم!",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite
                                )
                                Text(
                                    text = rankTitle,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = StarGold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "مسارك الفضائي للسنة الخامسة بتونس جاهز!",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Section Title: Trimester Cosmic Track
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🚀",
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "مسار المحطات الفضائية (الثلاثيات)",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Text(
                            text = "ابدأ بالمحطة الأولى المفتوحة لفتح بقية عوالم الفضاء",
                            fontSize = 11.sp,
                            color = CosmicCyan
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // -----------------------------------------------------------------
                // STATION 1: TRIMESTER 1 (OPEN & ACTIVE)
                // -----------------------------------------------------------------
                TrimesterStationCard(
                    trimesterNumber = "1",
                    title = "المحطة الأولى: مجرة الثلاثي الأول",
                    subtitle = "الأعداد الكبيرة، العمليات الأساسية، قيس الأطوال، والهندسة",
                    planetEmoji = "🪐",
                    isOpen = true,
                    completedPlanetsCount = completedT1Count,
                    totalPlanetsCount = 8,
                    glowColor = CosmicCyan,
                    badgeText = "مفتوحة للبدء 🟢",
                    topicsPreview = listOf(
                        "الأعداد ذات 6 أرقام والملايين",
                        "الجمع والطرح والضرب وتوزيعيته",
                        "القسمة الإقليدية وقابلية القسمة",
                        "الهندسة (التعامد والتوازي) وقيس الأطوال",
                        "مسألة إدماج الثلاثي الأول (تمور توزر)"
                    ),
                    onStationClick = { onSelectTrimesterStation(Trimester.TRIMESTER_1) },
                    modifier = Modifier.testTag("station_trimester_1")
                )

                // Cosmic connector line 1 -> 2
                CosmicConnectorPath(
                    isUnlocked = completedT1Count >= 4 || completedT2Count > 0,
                    sourceColor = CosmicCyan,
                    targetColor = NebulaViolet
                )

                // -----------------------------------------------------------------
                // STATION 2: TRIMESTER 2 (VISUALLY LOCKED)
                // -----------------------------------------------------------------
                TrimesterStationCard(
                    trimesterNumber = "2",
                    title = "المحطة الثانية: مجرة الثلاثي الثاني",
                    subtitle = "الأعداد العشرية، الكسور، المضلعات والمحيط، والكتل والسعات",
                    planetEmoji = "💎",
                    isOpen = completedT1Count >= 4 || completedT2Count > 0, // Unlocks when kid progresses in T1
                    completedPlanetsCount = completedT2Count,
                    totalPlanetsCount = 5,
                    glowColor = NebulaViolet,
                    badgeText = if (completedT1Count >= 4 || completedT2Count > 0) "مفتوحة للاستكشاف 🟢" else "محطة مغلقة 🔒",
                    topicsPreview = listOf(
                        "الأعداد العشرية (الفواصل ومقارنتها)",
                        "الكسور والعمليات العشرية",
                        "المضلعات وحساب محيط الأشكال",
                        "قيس الكتل (طن وقنطار) والسعات (لتر)",
                        "مسألة صابة البرتقال والأر"
                    ),
                    onStationClick = {
                        if (completedT1Count >= 4 || completedT2Count > 0) {
                            onSelectTrimesterStation(Trimester.TRIMESTER_2)
                        } else {
                            lockedDialogMessage = "يا بطلنا آدم، أكمل كواكب الثلاثي الأول لتشغيل محركات المركبة والانتقال إلى هذه المحطة!"
                        }
                    },
                    modifier = Modifier.testTag("station_trimester_2")
                )

                // Cosmic connector line 2 -> 3
                CosmicConnectorPath(
                    isUnlocked = completedT2Count >= 3 || completedT3Count > 0,
                    sourceColor = NebulaViolet,
                    targetColor = StarGold
                )

                // -----------------------------------------------------------------
                // STATION 3: TRIMESTER 3 (VISUALLY LOCKED)
                // -----------------------------------------------------------------
                TrimesterStationCard(
                    trimesterNumber = "3",
                    title = "المحطة الثالثة: مجرة الثلاثي الثالث والامتحان النهائي",
                    subtitle = "التناسب، النسب المئوية، قيس المساحات والزمن، والمسألة الشاملة",
                    planetEmoji = "👑",
                    isOpen = completedT2Count >= 3 || completedT3Count > 0,
                    completedPlanetsCount = completedT3Count,
                    totalPlanetsCount = 5,
                    glowColor = StarGold,
                    badgeText = if (completedT2Count >= 3 || completedT3Count > 0) "المحطة الكبرى مفتوحة 🟢" else "المحطة الكبرى مغلقة 🔒",
                    topicsPreview = listOf(
                        "التناسبية وجداول الرابع التناسبي",
                        "النسب المئوية وتخفيض الأسعار",
                        "المساحات الفلاحية (الآر والهكتار)",
                        "الأعداد التي تقيس الزمن (الساعات والدقائق)",
                        "مسألة تسييج الأرض الفلاحية الشاملة"
                    ),
                    onStationClick = {
                        if (completedT2Count >= 3 || completedT3Count > 0) {
                            onSelectTrimesterStation(Trimester.TRIMESTER_3)
                        } else {
                            lockedDialogMessage = "المحطة الختامية الكبرى مغلقة حالياً. اجتز كواكب الثلاثي الأول والثاني لتتويج رحلتك الفضائية!"
                        }
                    },
                    modifier = Modifier.testTag("station_trimester_3")
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Secondary Row: Mental Math Sprint & Badges
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Mental Math Sprint Card
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("mental_sprint_card")
                            .clickable { onMentalSprintClick() },
                        colors = CardDefaults.cardColors(
                            containerColor = SpaceCardSurface.copy(alpha = 0.9f)
                        ),
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SupernovaOrange.copy(alpha = 0.4f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(SupernovaOrange.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "⚡", fontSize = 24.sp)
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "صاروخ الحساب الذهني",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "تحديات سريعة للرصيد",
                                fontSize = 10.sp,
                                color = TextMuted,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // Badges Card
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("badges_trophies_card")
                            .clickable { onBadgesClick() },
                        colors = CardDefaults.cardColors(
                            containerColor = SpaceCardSurface.copy(alpha = 0.9f)
                        ),
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, StarGold.copy(alpha = 0.4f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(StarGold.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "🏆", fontSize = 24.sp)
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "كؤوس وأوسمة آدم",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "عرض الشارات المفتوحة",
                                fontSize = 10.sp,
                                color = TextMuted,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Curriculum Badge Info Banner
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = SpaceBackgroundMedium.copy(alpha = 0.6f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🇹🇳",
                            fontSize = 28.sp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "البرنامج الرسمي التونسي",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = CosmicCyan
                            )
                            Text(
                                text = "مطابق لدليل المعلم وكتاب الرياضيات للسنة الخامسة أساسي • وزارة التربية والتكوين",
                                fontSize = 11.sp,
                                color = TextMuted,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Friendly dialog for locked stations
        if (lockedDialogMessage != null) {
            AlertDialog(
                onDismissRequest = { lockedDialogMessage = null },
                containerColor = SpaceCardSurface,
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🔒", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "المحطة الفضائية مغلقة",
                            color = StarGold,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                text = {
                    Text(
                        text = lockedDialogMessage ?: "",
                        color = TextWhite,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { lockedDialogMessage = null },
                        colors = ButtonDefaults.buttonColors(containerColor = CosmicCyan, contentColor = Color(0xFF0B1021)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = "حسناً، فهمت!", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}

/**
 * Trimester Cosmic Station Card
 */
@Composable
fun TrimesterStationCard(
    trimesterNumber: String,
    title: String,
    subtitle: String,
    planetEmoji: String,
    isOpen: Boolean,
    completedPlanetsCount: Int,
    totalPlanetsCount: Int,
    glowColor: Color,
    badgeText: String,
    topicsPreview: List<String>,
    onStationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onStationClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isOpen)
                SpaceCardSurface.copy(alpha = 0.95f)
            else
                SpaceBackgroundMedium.copy(alpha = 0.55f)
        ),
        shape = RoundedCornerShape(22.dp),
        border = if (isOpen) {
            androidx.compose.foundation.BorderStroke(2.dp, glowColor.copy(alpha = 0.7f))
        } else {
            androidx.compose.foundation.BorderStroke(1.dp, SpaceCardBorder.copy(alpha = 0.5f))
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header Row: Station Number, Title, and Lock/Open Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Station Planet / Lock Icon
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            if (isOpen) glowColor.copy(alpha = 0.2f) else Color.Gray.copy(alpha = 0.15f)
                        )
                        .border(
                            width = 2.dp,
                            color = if (isOpen) glowColor else Color.Gray.copy(alpha = 0.4f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isOpen) {
                        Text(text = planetEmoji, fontSize = 28.sp)
                    } else {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "محطة مغلقة",
                            tint = TextMuted,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    // Status Badge
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isOpen) glowColor.copy(alpha = 0.18f) else Color.Gray.copy(alpha = 0.2f)
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badgeText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isOpen) glowColor else TextMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isOpen) TextWhite else TextMuted
                    )

                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = TextMuted,
                        lineHeight = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Topics Preview Bullets
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isOpen) SpaceBackgroundMedium.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.25f)
                    )
                    .padding(12.dp)
            ) {
                topicsPreview.take(3).forEach { topic ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Text(
                            text = if (isOpen) "✦" else "•",
                            color = if (isOpen) glowColor else TextMuted,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(end = 6.dp)
                        )
                        Text(
                            text = topic,
                            fontSize = 12.sp,
                            color = if (isOpen) TextWhite else TextMuted.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action / Status Bar at bottom of card
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Progress
                if (isOpen) {
                    Text(
                        text = "$completedPlanetsCount من $totalPlanetsCount كواكب مكتملة",
                        fontSize = 12.sp,
                        color = glowColor,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Text(
                        text = "يتطلب إكمال المحطة السابقة",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                // CTA Button
                Button(
                    onClick = onStationClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isOpen) glowColor else Color.Gray.copy(alpha = 0.3f),
                        contentColor = if (isOpen) Color(0xFF0B1021) else TextMuted
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (isOpen) "انطلق الآن 🚀" else "استكشف القفل 🔒",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Connecting cosmic path element between stations
 */
@Composable
fun CosmicConnectorPath(
    isUnlocked: Boolean,
    sourceColor: Color,
    targetColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for (i in 1..3) {
            Box(
                modifier = Modifier
                    .size(if (i == 2) 8.dp else 6.dp)
                    .clip(CircleShape)
                    .background(
                        if (isUnlocked)
                            sourceColor.copy(alpha = 0.7f)
                        else
                            SpaceCardBorder.copy(alpha = 0.5f)
                    )
            )
            if (i < 3) {
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}
