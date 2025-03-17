package com.example.kotlin_to_do_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.kotlin_to_do_app.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    onLogout: () -> Unit,
    taskViewModel: TaskViewModel = viewModel()
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
                        SortOrderDropdown(
                            currentSortOrder = sortOrder,
                            onSortOrderSelected = { taskViewModel.setSortOrder(it) }
                        )

                        IconButton(onClick = { showFilters = !showFilters }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Filtros"
                            )
                        }

                        IconButton(onClick = onLogout) {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = "Cerrar sesión"
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
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Añadir tarea"
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
                    Icon(
                        imageVector = Icons.Default.List,
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
                        .padding(horizontal = 16.dp)
                ) {
                    items(tasks) { task ->
                        TaskItem(
                            task = task,
                            onTaskClick = { selectedTask = task },
                            onToggleStatus = { taskViewModel.toggleTaskStatus(task) }
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

// Mantén el resto del código (TaskItem, etc.) igual

@Composable
fun TaskItem(
    task: Task,
    onTaskClick: () -> Unit,
    onToggleStatus: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onTaskClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Priority indicator
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(
                        when (task.priority) {
                            TaskPriority.HIGH -> Color.Red
                            TaskPriority.MEDIUM -> Color(0xFFFFA500) // Orange
                            TaskPriority.LOW -> Color.Green
                        }
                    )
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

                if (task.description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textDecoration = if (task.isDone) TextDecoration.LineThrough else TextDecoration.None
                    )
                }

                if (task.dueDate != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = dateFormat.format(task.dueDate),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }

        }
    }
}