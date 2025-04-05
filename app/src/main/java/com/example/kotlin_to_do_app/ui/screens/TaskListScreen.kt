package com.example.kotlin_to_do_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlin_to_do_app.model.Task
import com.example.kotlin_to_do_app.model.TaskPriority
import com.example.kotlin_to_do_app.ui.components.PriorityFilterChips
import com.example.kotlin_to_do_app.ui.components.SortOrderDropdown
import com.example.kotlin_to_do_app.ui.components.TaskSearchBar
import com.example.kotlin_to_do_app.ui.components.SwipeableTaskItem
import com.example.kotlin_to_do_app.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*
import com.example.kotlin_to_do_app.ui.components.ThemeSwitch
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ViewList
import com.example.kotlin_to_do_app.ui.components.CalendarView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    onLogout: () -> Unit,
    taskViewModel: TaskViewModel = viewModel(),
    isDarkTheme: Boolean = false,
    onToggleTheme: () -> Unit = {}
) {
    val tasks by taskViewModel.filteredTasks.collectAsState()
    val isLoading by taskViewModel.isLoading.collectAsState()
    val error by taskViewModel.error.collectAsState()
    val searchQuery by taskViewModel.searchQuery.collectAsState()
    val selectedPriority by taskViewModel.selectedPriority.collectAsState()
    val sortOrder by taskViewModel.sortOrder.collectAsState()
    val selectedDate by taskViewModel.selectedDate.collectAsState()
    val isCalendarView by taskViewModel.isCalendarView.collectAsState()

    var showAddTaskDialog by remember { mutableStateOf(false) }
    var selectedTask by remember { mutableStateOf<Task?>(null) }
    var showFilters by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Mis Tareas") },
                    actions = {
                        // Botón para alternar entre vista de lista y calendario
                        IconButton(onClick = {
                            // Si estamos pasando de calendario a lista, limpiar el filtro de fecha
                            if (isCalendarView) {
                                taskViewModel.setSelectedDate(null)
                            }
                            taskViewModel.toggleCalendarView()
                        }) {
                            Icon(
                                imageVector = if (isCalendarView)
                                    Icons.Default.ViewList
                                else
                                    Icons.Default.CalendarMonth,
                                contentDescription = if (isCalendarView)
                                    "Ver como lista"
                                else
                                    "Ver como calendario",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Selector de tema
                        ThemeSwitch(
                            isDarkTheme = isDarkTheme,
                            onToggleTheme = onToggleTheme
                        )

                        SortOrderDropdown(
                            currentSortOrder = sortOrder,
                            onSortOrderSelected = { taskViewModel.setSortOrder(it) }
                        )

                        // Botón de filtros
                        IconButton(onClick = { showFilters = !showFilters }) {
                            Icon(
                                imageVector = Icons.Outlined.FilterAlt,
                                contentDescription = "Filtros",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Botón de cerrar sesión
                        IconButton(onClick = onLogout) {
                            Icon(
                                imageVector = Icons.Outlined.Logout,
                                contentDescription = "Cerrar sesión",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                )

                if (showFilters) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        TaskSearchBar(
                            searchQuery = searchQuery,
                            onSearchQueryChange = { taskViewModel.setSearchQuery(it) }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        PriorityFilterChips(
                            selectedPriority = selectedPriority,
                            onPrioritySelected = { taskViewModel.setPriorityFilter(it) }
                        )

                        // Si hay una fecha seleccionada, mostrar un chip para limpiarla
                        if (selectedDate != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Fecha: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate!!)}")
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(onClick = { taskViewModel.setSelectedDate(null) }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Limpiar filtro de fecha"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddTaskDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Rounded.AddCircle,
                    contentDescription = "Añadir tarea",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (tasks.isEmpty() && !isCalendarView) {
                // Solo mostrar la vista de "no hay tareas" en modo lista
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Assignment,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    val messageText = if (searchQuery.isNotBlank() || selectedPriority != null) {
                        // Solo considerar búsqueda y prioridad para el mensaje de filtros
                        "No se encontraron tareas con los filtros aplicados"
                    } else {
                        "No hay tareas pendientes"
                    }

                    Text(
                        messageText,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )

                    if (searchQuery.isNotBlank() || selectedPriority != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = {
                            taskViewModel.setSearchQuery("")
                            taskViewModel.setPriorityFilter(null)
                        }) {
                            Icon(
                                imageVector = Icons.Outlined.FilterListOff,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Limpiar filtros")
                        }
                    } else {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Pulsa el botón + para añadir una nueva tarea",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            } else {
                if (isCalendarView) {
                    // Vista de calendario
                    val allTasksList by taskViewModel.allTasks.collectAsState()

                    CalendarView(
                        allTasks = allTasksList,  // Lista completa sin filtrar
                        filteredTasks = tasks,    // Lista filtrada solo con tareas del día seleccionado
                        onDateSelected = { date ->
                            taskViewModel.setSelectedDate(date)
                        },
                        onTaskClick = { task ->
                            selectedTask = task
                        },
                        isDarkTheme = isDarkTheme
                    )
                } else {
                    // Vista de lista con secciones temporales
                    val today = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }.time

                    // Agrupar tareas por tiempo
                    val todayTasks = tasks.filter { task ->
                        task.dueDate != null &&
                                SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(task.dueDate) ==
                                SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(today)
                    }

                    val futureTasks = tasks.filter { task ->
                        task.dueDate != null && task.dueDate.after(today) &&
                                SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(task.dueDate) !=
                                SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(today)
                    }

                    val pastTasks = tasks.filter { task ->
                        task.dueDate != null && task.dueDate.before(today)
                    }

                    val noDateTasks = tasks.filter { task -> task.dueDate == null }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        contentPadding = PaddingValues(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Tareas para hoy
                        if (todayTasks.isNotEmpty()) {
                            item {
                                SectionHeader(title = "PARA HOY", color = MaterialTheme.colorScheme.primary)
                            }

                            items(todayTasks) { task ->
                                SwipeableTaskItem(
                                    task = task,
                                    onTaskClick = { selectedTask = task },
                                    onToggleStatus = { taskViewModel.toggleTaskStatus(task) },
                                    onDeleteTask = { taskViewModel.deleteTask(task.id) },
                                    isDarkTheme = isDarkTheme
                                )
                            }
                        }

                        // Tareas futuras
                        if (futureTasks.isNotEmpty()) {
                            item {
                                SectionHeader(title = "TAREAS FUTURAS", color = MaterialTheme.colorScheme.secondary)
                            }

                            items(futureTasks) { task ->
                                SwipeableTaskItem(
                                    task = task,
                                    onTaskClick = { selectedTask = task },
                                    onToggleStatus = { taskViewModel.toggleTaskStatus(task) },
                                    onDeleteTask = { taskViewModel.deleteTask(task.id) },
                                    isDarkTheme = isDarkTheme
                                )
                            }
                        }

                        // Tareas pasadas
                        if (pastTasks.isNotEmpty()) {
                            item {
                                SectionHeader(
                                    title = "TAREAS PASADAS",
                                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                                )
                            }

                            items(pastTasks) { task ->
                                SwipeableTaskItem(
                                    task = task,
                                    onTaskClick = { selectedTask = task },
                                    onToggleStatus = { taskViewModel.toggleTaskStatus(task) },
                                    onDeleteTask = { taskViewModel.deleteTask(task.id) },
                                    isDarkTheme = isDarkTheme
                                )
                            }
                        }

                        // Tareas sin fecha
                        if (noDateTasks.isNotEmpty()) {
                            item {
                                SectionHeader(
                                    title = "SIN FECHA ASIGNADA",
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }

                            items(noDateTasks) { task ->
                                SwipeableTaskItem(
                                    task = task,
                                    onTaskClick = { selectedTask = task },
                                    onToggleStatus = { taskViewModel.toggleTaskStatus(task) },
                                    onDeleteTask = { taskViewModel.deleteTask(task.id) },
                                    isDarkTheme = isDarkTheme
                                )
                            }
                        }
                    }
                }
            }

            if (error != null) {
                Snackbar(
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    Text(error ?: "Error desconocido")
                }
            }
        }
    }

    if (showAddTaskDialog) {
        TaskDialog(
            task = null,
            onDismiss = { showAddTaskDialog = false },
            onTaskSaved = { task ->
                taskViewModel.addTask(task)
                showAddTaskDialog = false
            }
        )
    }

    if (selectedTask != null) {
        TaskDialog(
            task = selectedTask,
            onDismiss = { selectedTask = null },
            onTaskSaved = { task ->
                taskViewModel.updateTask(task)
                selectedTask = null
            },
            onTaskDeleted = { taskId ->
                taskViewModel.deleteTask(taskId)
                selectedTask = null
            }
        )
    }
}

@Composable
fun SectionHeader(title: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .height(24.dp)
                .width(4.dp)
                .background(color, shape = RoundedCornerShape(2.dp))
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )

        Divider(
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(1f),
            color = color.copy(alpha = 0.3f)
        )
    }
}