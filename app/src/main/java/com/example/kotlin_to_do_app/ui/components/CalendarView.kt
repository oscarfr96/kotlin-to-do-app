package com.example.kotlin_to_do_app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlin_to_do_app.model.Task
import com.example.kotlin_to_do_app.model.TaskPriority
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar as JavaCalendar

@Composable
fun CalendarView(
    allTasks: List<Task>,  // Lista completa para marcar los días
    filteredTasks: List<Task>,  // Lista filtrada para mostrar en detalle
    onDateSelected: (Date) -> Unit,
    onTaskClick: (Task) -> Unit,
    isDarkTheme: Boolean = false
) {
    // Usar remember con mutableStateOf para que Compose observe los cambios
    var currentYear by remember { mutableStateOf(JavaCalendar.getInstance().get(JavaCalendar.YEAR)) }
    var currentMonthValue by remember { mutableStateOf(JavaCalendar.getInstance().get(JavaCalendar.MONTH)) }
    val selectedDate = remember { mutableStateOf<Date?>(null) }

    // Recrear el calendario cada vez que cambie el año o mes
    val calendar = remember(currentYear, currentMonthValue) {
        val cal = JavaCalendar.getInstance()
        cal.set(JavaCalendar.YEAR, currentYear)
        cal.set(JavaCalendar.MONTH, currentMonthValue)
        cal.set(JavaCalendar.DAY_OF_MONTH, 1)
        cal
    }

    // Formatear fechas
    val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    val dayFormat = SimpleDateFormat("d", Locale.getDefault())
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    Column(modifier = Modifier.fillMaxSize()) {
        // Encabezado del calendario
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                // Navegar al mes anterior
                if (currentMonthValue == JavaCalendar.JANUARY) {
                    currentMonthValue = JavaCalendar.DECEMBER
                    currentYear--
                } else {
                    currentMonthValue--
                }
            }) {
                Icon(Icons.Default.ChevronLeft, contentDescription = "Mes anterior")
            }

            Text(
                text = monthYearFormat.format(calendar.time).uppercase(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            IconButton(onClick = {
                // Navegar al mes siguiente
                if (currentMonthValue == JavaCalendar.DECEMBER) {
                    currentMonthValue = JavaCalendar.JANUARY
                    currentYear++
                } else {
                    currentMonthValue++
                }
            }) {
                Icon(Icons.Default.ChevronRight, contentDescription = "Mes siguiente")
            }
        }

        // Días de la semana
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val daysOfWeek = listOf("L", "M", "X", "J", "V", "S", "D")
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Cuadrícula del calendario
        val daysInMonth by remember(currentYear, currentMonthValue) {
            mutableStateOf(getDaysInMonth(calendar))
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .padding(4.dp)
        ) {
            items(
                items = daysInMonth,
                key = { "${currentYear}-${currentMonthValue}-${it.time}" }
            ) { day ->
                DayCell(
                    day = day,
                    isSelected = selectedDate.value?.let {
                        dateFormat.format(it) == dateFormat.format(day)
                    } ?: false,
                    // IMPORTANTE: Usamos allTasks para determinar si un día tiene tareas
                    hasTask = allTasks.any { task ->
                        task.dueDate?.let { dateFormat.format(it) == dateFormat.format(day) } ?: false
                    },
                    isCurrentMonth = isSameMonth(day, calendar),
                    onDateSelected = {
                        selectedDate.value = day
                        onDateSelected(day)
                    },
                    isDarkTheme = isDarkTheme
                )
            }
        }

        Divider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))

        // Lista de tareas del día seleccionado
        selectedDate.value?.let { date ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Tareas para el ${dateFormat.format(date)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (filteredTasks.isEmpty()) {
                    Text(
                        text = "Sin tareas",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                } else {
                    filteredTasks.forEach { task ->
                        CalendarTaskItem(
                            task = task,
                            onClick = { onTaskClick(task) },
                            isDarkTheme = isDarkTheme
                        )
                    }
                }
            }
        }
    }
}

// Función para verificar si una fecha pertenece al mes actual
private fun isSameMonth(date: Date, calendar: JavaCalendar): Boolean {
    val dateCalendar = JavaCalendar.getInstance()
    dateCalendar.time = date
    return dateCalendar.get(JavaCalendar.MONTH) == calendar.get(JavaCalendar.MONTH) &&
            dateCalendar.get(JavaCalendar.YEAR) == calendar.get(JavaCalendar.YEAR)
}

@Composable
fun DayCell(
    day: Date,
    isSelected: Boolean,
    hasTask: Boolean,
    isCurrentMonth: Boolean,
    onDateSelected: () -> Unit,
    isDarkTheme: Boolean
) {
    val dayFormat = SimpleDateFormat("d", Locale.getDefault())
    val today = JavaCalendar.getInstance().time
    val isToday = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(today) ==
            SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(day)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(2.dp)
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(
                when {
                    isSelected -> MaterialTheme.colorScheme.primary
                    isToday -> MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    else -> Color.Transparent
                }
            )
            // Siempre mostrar borde para días con tareas, incluso si están seleccionados
            .border(
                width = if (hasTask && isCurrentMonth) 2.dp else 1.dp,
                color = when {
                    hasTask && isCurrentMonth -> MaterialTheme.colorScheme.secondary
                    isSelected -> MaterialTheme.colorScheme.primary
                    else -> Color.Transparent
                },
                shape = CircleShape
            )
            .clickable(onClick = onDateSelected),
    ) {
        // Número del día
        Text(
            text = dayFormat.format(day),
            color = when {
                isSelected -> MaterialTheme.colorScheme.onPrimary
                !isCurrentMonth -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                else -> MaterialTheme.colorScheme.onSurface
            },
            fontWeight = if (hasTask || isToday) FontWeight.Bold else FontWeight.Normal,
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 4.dp)
        )

        // Indicador visual de tarea - punto debajo del número
        // SIEMPRE visible para días con tareas, incluso cuando están seleccionados
        if (hasTask && isCurrentMonth) {
            Box(
                modifier = Modifier
                    .padding(top = 2.dp)
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(
                        color = if (isSelected)
                            MaterialTheme.colorScheme.onPrimary
                        else
                            MaterialTheme.colorScheme.secondary
                    )
            )
        } else {
            // Espacio para mantener la alineación
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun CalendarTaskItem(
    task: Task,
    onClick: () -> Unit,
    isDarkTheme: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (task.priority) {
                TaskPriority.HIGH -> Color.Red.copy(alpha = 0.1f)
                TaskPriority.MEDIUM -> Color(0xFFFFA500).copy(alpha = 0.1f)
                TaskPriority.LOW -> Color.Green.copy(alpha = 0.1f)
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (task.isDone) {
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(
                            when (task.priority) {
                                TaskPriority.HIGH -> Color.Red
                                TaskPriority.MEDIUM -> Color(0xFFFFA500)
                                TaskPriority.LOW -> Color.Green
                            }
                        )
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

// Función auxiliar para obtener los días del mes actual
private fun getDaysInMonth(calendar: JavaCalendar): List<Date> {
    val days = mutableListOf<Date>()
    val monthCalendar = calendar.clone() as JavaCalendar

    // Ajustar para que la semana comience en lunes (2 en Java Calendar es lunes)
    val firstDayOfWeek = monthCalendar.get(JavaCalendar.DAY_OF_WEEK)
    val offset = if (firstDayOfWeek == JavaCalendar.SUNDAY) 6 else firstDayOfWeek - 2

    // Añadir días del mes anterior
    if (offset > 0) {
        val prevMonth = monthCalendar.clone() as JavaCalendar
        prevMonth.add(JavaCalendar.MONTH, -1)
        val daysInPrevMonth = prevMonth.getActualMaximum(JavaCalendar.DAY_OF_MONTH)
        for (i in (daysInPrevMonth - offset + 1)..daysInPrevMonth) {
            prevMonth.set(JavaCalendar.DAY_OF_MONTH, i)
            days.add(prevMonth.time)
        }
    }

    // Añadir días del mes actual
    val daysInMonth = monthCalendar.getActualMaximum(JavaCalendar.DAY_OF_MONTH)
    for (i in 1..daysInMonth) {
        monthCalendar.set(JavaCalendar.DAY_OF_MONTH, i)
        days.add(monthCalendar.time)
    }

    // Completar la última semana con días del mes siguiente si es necesario
    val remainingCells = 42 - days.size // 6 semanas × 7 días = 42 celdas en total
    if (remainingCells > 0) {
        val nextMonth = monthCalendar.clone() as JavaCalendar
        nextMonth.add(JavaCalendar.MONTH, 1)
        nextMonth.set(JavaCalendar.DAY_OF_MONTH, 1)
        for (i in 1..remainingCells) {
            nextMonth.set(JavaCalendar.DAY_OF_MONTH, i)
            days.add(nextMonth.time)
        }
    }

    return days
}