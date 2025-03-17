package com.example.kotlin_to_do_app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kotlin_to_do_app.model.TaskPriority
import com.example.kotlin_to_do_app.viewmodel.TaskSortOrder
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskSearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text("Buscar tareas...") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Buscar"
            )
        },
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { onSearchQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Limpiar búsqueda"
                    )
                }
            }
        },
        singleLine = true
    )
}

@Composable
fun PriorityFilterChips(
    selectedPriority: TaskPriority?,
    onPrioritySelected: (TaskPriority?) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedPriority == null,
            onClick = { onPrioritySelected(null) },
            label = { Text("Todas") }
        )

        TaskPriority.values().forEach { priority ->
            FilterChip(
                selected = selectedPriority == priority,
                onClick = { onPrioritySelected(priority) },
                label = {
                    Text(
                        when (priority) {
                            TaskPriority.HIGH -> "Alta (P1)"
                            TaskPriority.MEDIUM -> "Media (P2)"
                            TaskPriority.LOW -> "Baja (P3)"
                        }
                    )
                }
            )
        }
    }
}

@Composable
fun SortOrderDropdown(
    currentSortOrder: TaskSortOrder,
    onSortOrderSelected: (TaskSortOrder) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = "Ordenar"
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Fecha (próxima primero)") },
                onClick = {
                    onSortOrderSelected(TaskSortOrder.DATE_ASC)
                    expanded = false
                },
                leadingIcon = {
                    if (currentSortOrder == TaskSortOrder.DATE_ASC) {
                        Icon(Icons.Default.Check, contentDescription = null)
                    }
                }
            )

            DropdownMenuItem(
                text = { Text("Fecha (lejana primero)") },
                onClick = {
                    onSortOrderSelected(TaskSortOrder.DATE_DESC)
                    expanded = false
                },
                leadingIcon = {
                    if (currentSortOrder == TaskSortOrder.DATE_DESC) {
                        Icon(Icons.Default.Check, contentDescription = null)
                    }
                }
            )

            DropdownMenuItem(
                text = { Text("Prioridad (alta primero)") },
                onClick = {
                    onSortOrderSelected(TaskSortOrder.PRIORITY_HIGH_FIRST)
                    expanded = false
                },
                leadingIcon = {
                    if (currentSortOrder == TaskSortOrder.PRIORITY_HIGH_FIRST) {
                        Icon(Icons.Default.Check, contentDescription = null)
                    }
                }
            )

            DropdownMenuItem(
                text = { Text("Prioridad (baja primero)") },
                onClick = {
                    onSortOrderSelected(TaskSortOrder.PRIORITY_LOW_FIRST)
                    expanded = false
                },
                leadingIcon = {
                    if (currentSortOrder == TaskSortOrder.PRIORITY_LOW_FIRST) {
                        Icon(Icons.Default.Check, contentDescription = null)
                    }
                }
            )

            DropdownMenuItem(
                text = { Text("Estado de completitud") },
                onClick = {
                    onSortOrderSelected(TaskSortOrder.COMPLETION_STATUS)
                    expanded = false
                },
                leadingIcon = {
                    if (currentSortOrder == TaskSortOrder.COMPLETION_STATUS) {
                        Icon(Icons.Default.Check, contentDescription = null)
                    }
                }
            )
        }
    }
}