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
import com.example.kotlin_to_do_app.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    onLogout: () -> Unit,
    taskViewModel: TaskViewModel = viewModel()
) {
    val tasks by taskViewModel.tasks.collectAsState()
    val isLoading by taskViewModel.isLoading.collectAsState()
    val error by taskViewModel.error.collectAsState()

    var showAddTaskDialog by remember { mutableStateOf(false) }
    var selectedTask by remember { mutableStateOf<Task?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Tareas") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Cerrar sesión"
                        )
                    }
                }
            )
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
                    Text(
                        "No hay tareas pendientes",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Pulsa el botón + para añadir una nueva tarea",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
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