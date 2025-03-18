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

    var showAddTaskDialog by remember { mutableStateOf(false) }
    var selectedTask by remember { mutableStateOf<Task?>(null) }
    var showFilters by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Mis Tareas") },
                    actions = {
                        // Selector de tema
                        ThemeSwitch(
                            isDarkTheme = isDarkTheme,
                            onToggleTheme = onToggleTheme
                        )

                        SortOrderDropdown(
                            currentSortOrder = sortOrder,
                            onSortOrderSelected = { taskViewModel.setSortOrder(it) }
                        )

                        // Botón de filtros - Ícono mejorado
                        IconButton(onClick = { showFilters = !showFilters }) {
                            Icon(
                                imageVector = Icons.Outlined.FilterAlt,
                                contentDescription = "Filtros",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Botón de cerrar sesión - Ícono mejorado
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
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddTaskDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                // Ícono mejorado para el FAB
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
            } else if (tasks.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Ícono mejorado para lista vacía
                    Icon(
                        imageVector = Icons.Outlined.Assignment,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    val messageText = if (searchQuery.isNotBlank() || selectedPriority != null) {
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
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(tasks) { task ->
                        // Usar el componente SwipeableTaskItem en lugar de TaskItem directamente
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
fun TaskItem(
    task: Task,
    onTaskClick: () -> Unit,
    onToggleStatus: () -> Unit,
    isDarkTheme: Boolean = false
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .border(
                width = 1.5.dp,
                color = Color.Black.copy(alpha = 0.2f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onTaskClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme)
                Color(0xFF1B1B1B)  // Gris oscuro para tema oscuro
            else
                Color(0xFFF5F5F5)  // Gris claro para tema claro
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Priority indicator actualizado a un ícono de círculo
            Icon(
                imageVector = Icons.Filled.Circle,
                contentDescription = "Prioridad ${task.priority.name}",
                tint = when (task.priority) {
                    TaskPriority.HIGH -> Color.Red
                    TaskPriority.MEDIUM -> Color(0xFFFFA500)
                    TaskPriority.LOW -> Color.Green
                },
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Checkbox
            Checkbox(
                checked = task.isDone,
                onCheckedChange = { onToggleStatus() }
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Task info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (task.isDone) TextDecoration.LineThrough else TextDecoration.None,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Fecha de vencimiento con icono moderno
                if (task.dueDate != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Event,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = dateFormat.format(task.dueDate),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                if (task.description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        textDecoration = if (task.isDone) TextDecoration.LineThrough else TextDecoration.None
                    )
                }
            }
        }
    }
}