package com.example.kotlin_to_do_app.ui.components

import androidx.compose.runtime.Composable
import com.example.kotlin_to_do_app.model.Task
import com.example.kotlin_to_do_app.ui.screens.TaskItem

@Composable
fun SwipeableTaskItem(
    task: Task,
    onTaskClick: () -> Unit,
    onToggleStatus: () -> Unit,
    onDeleteTask: () -> Unit,
    isDarkTheme: Boolean = false
) {

    TaskItem(
        task = task,
        onTaskClick = onTaskClick,
        onToggleStatus = onToggleStatus,
        isDarkTheme = isDarkTheme
    )
}