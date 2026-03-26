package com.example.collegeschedule.ui.schedule

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.collegeschedule.data.dto.LessonDto
import com.example.collegeschedule.data.dto.LessonGroupPart
import com.example.collegeschedule.data.dto.ScheduleByDateDto

@Composable
fun ScheduleList(
    data: List<ScheduleByDateDto>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(data) { day ->
            DaySection(day)
        }
    }
}

@Composable
private fun DaySection(day: ScheduleByDateDto) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        DayHeader(
            date = day.lessonDate,
            weekday = day.weekday,
            lessonCount = day.lessons.size
        )

        if (day.lessons.isEmpty()) {
            EmptyStateCard()
        } else {
            day.lessons.forEach { lesson ->
                LessonCard(lesson = lesson)
            }
        }
    }
}

@Composable
private fun DayHeader(date: String, weekday: String, lessonCount: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = "Дата",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "$date, $weekday",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        if (lessonCount > 0) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier
            ) {
                Text(
                    text = "$lessonCount пар",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun EmptyStateCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.FreeCancellation,
                contentDescription = "Нет пар",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Нет пар",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun LessonCard(lesson: LessonDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            LessonHeader(lesson = lesson)

            Divider(
                color = MaterialTheme.colorScheme.outlineVariant,
                thickness = 0.5.dp
            )

            LessonContent(lesson = lesson)
        }
    }
}

@Composable
private fun LessonHeader(lesson: LessonDto) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(40.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${lesson.lessonNumber}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Schedule,
                    contentDescription = "Время",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = lesson.time,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun LessonContent(lesson: LessonDto) {
    val hasSubgroups = lesson.groupParts.size > 1

    Column(
        verticalArrangement = Arrangement.spacedBy(if (hasSubgroups) 16.dp else 8.dp)
    ) {
        lesson.groupParts.forEach { (part, info) ->
            if (info != null) {
                if (hasSubgroups) {
                    SubgroupSection(
                        part = part,
                        lessonInfo = info
                    )
                } else {
                    FullGroupSection(lessonInfo = info)
                }
            }
        }
    }
}

@Composable
private fun SubgroupSection(part: LessonGroupPart, lessonInfo: com.example.collegeschedule.data.dto.LessonPartDto) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier
            ) {
                Text(
                    text = when (part) {
                        LessonGroupPart.FULL -> "Вся группа"
                        LessonGroupPart.SUB1 -> "Подгруппа 1"
                        LessonGroupPart.SUB2 -> "Подгруппа 2"
                    },
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }

        LessonInfoCard(lessonInfo = lessonInfo)
    }
}

@Composable
private fun FullGroupSection(lessonInfo: com.example.collegeschedule.data.dto.LessonPartDto) {
    LessonInfoCard(lessonInfo = lessonInfo)
}

@Composable
private fun LessonInfoCard(lessonInfo: com.example.collegeschedule.data.dto.LessonPartDto) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = getSubjectIcon(lessonInfo.subject),
                contentDescription = "Предмет",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = lessonInfo.subject,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = "Преподаватель",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
            Column {
                Text(
                    text = lessonInfo.teacher,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (lessonInfo.teacherPosition.isNotBlank()) {
                    Text(
                        text = lessonInfo.teacherPosition,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.LocationOn,
                contentDescription = "Аудитория",
                tint = getBuildingColor(lessonInfo.building),
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = "${lessonInfo.building}, ${lessonInfo.classroom}",
                style = MaterialTheme.typography.bodyMedium,
                color = getBuildingColor(lessonInfo.building)
            )
        }

        if (lessonInfo.address.isNotBlank()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Place,
                    contentDescription = "Адрес",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = lessonInfo.address,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun getSubjectIcon(subject: String): ImageVector {
    return when {
        subject.contains("Математика", ignoreCase = true)-> Icons.Default.Functions

        subject.contains("Программирование", ignoreCase = true) ||
                subject.contains("Информатика", ignoreCase = true) -> Icons.Default.Code

        subject.contains("Английский язык", ignoreCase = true) ||
                subject.contains("Иностранный язык", ignoreCase = true) -> Icons.Default.Language

        subject.contains("Физика", ignoreCase = true) -> Icons.Default.Science

        subject.contains("История", ignoreCase = true) -> Icons.Default.History

        subject.contains("Физическая Культура", ignoreCase = true)  -> Icons.Default.DirectionsRun

        subject.contains("Экономика", ignoreCase = true) -> Icons.Default.ShowChart

        subject.contains("Графический дизайн", ignoreCase = true) -> Icons.Default.DesignServices

        subject.contains("Базы данных", ignoreCase = true) -> Icons.Default.Storage

        subject.contains("Химия", ignoreCase = true) -> Icons.Default.Science

        subject.contains("Биология", ignoreCase = true) -> Icons.Default.Park

        subject.contains("Алгоритмы", ignoreCase = true) -> Icons.Default.AltRoute

        else -> Icons.Default.School
    }
}

@Composable
private fun getBuildingColor(building: String): Color {
    return when {
        building.contains("Учебный", ignoreCase = true) -> Color(0xFF4BEC32)
        building.contains("Лабораторный", ignoreCase = true) -> Color(0xFF32DEEC)
        building.contains("Спрортивный", ignoreCase = true) -> Color(0xFF000FCC)
        building.contains("Библиотечный", ignoreCase = true) -> Color(	0xFF593315)
        building.contains("Главный", ignoreCase = true) -> Color(0xFFFF0000)
        building.contains("Институт", ignoreCase = true) -> Color(	0xFF4500CC)
        building.contains("Физический", ignoreCase = true) -> Color(0xFFFFF300)
        building.contains("Корпус инженерии", ignoreCase = true) -> Color(0xFFFF8B00)
        building.contains("Корпус Экономики", ignoreCase = true) -> Color(0xFFFF2E9C)
        building.contains("Корпус Прсихологии", ignoreCase = true) -> Color(0xFF7DE3CA)
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
}