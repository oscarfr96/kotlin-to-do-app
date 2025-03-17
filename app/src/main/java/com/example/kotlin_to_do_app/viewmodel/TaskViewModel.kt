package com.example.kotlin_to_do_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlin_to_do_app.model.Task
import com.example.kotlin_to_do_app.model.TaskPriority
import com.example.kotlin_to_do_app.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

enum class TaskSortOrder {
    DATE_ASC, DATE_DESC, PRIORITY_HIGH_FIRST, PRIORITY_LOW_FIRST, COMPLETION_STATUS
}

class TaskViewModel : ViewModel() {
    private val repository = TaskRepository()

    // Fuente de datos original
    private val _allTasks = MutableStateFlow<List<Task>>(emptyList())

    // Estados para filtros y ordenación
    private val _selectedPriority = MutableStateFlow<TaskPriority?>(null)
    val selectedPriority: StateFlow<TaskPriority?> = _selectedPriority

    private val _sortOrder = MutableStateFlow(TaskSortOrder.DATE_ASC)
    val sortOrder: StateFlow<TaskSortOrder> = _sortOrder

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    // Lista filtrada y ordenada que se muestra al usuario
    private val _filteredTasks = MutableStateFlow<List<Task>>(emptyList())
    val filteredTasks: StateFlow<List<Task>> = _filteredTasks

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadTasks()

        // Combinar estados para aplicar filtros y ordenación cuando cambie cualquiera de ellos
        viewModelScope.launch {
            combine(
                _allTasks,
                _selectedPriority,
                _sortOrder,
                _searchQuery
            ) { tasks, priority, order, query ->
                applyFiltersAndSort(tasks, priority, order, query)
            }.collect { filtered ->
                _filteredTasks.value = filtered
            }
        }
    }

    private fun applyFiltersAndSort(
        tasks: List<Task>,
        priority: TaskPriority?,
        order: TaskSortOrder,
        query: String
    ): List<Task> {
        // Paso 1: Aplicar filtro de prioridad
        var result = if (priority != null) {
            tasks.filter { it.priority == priority }
        } else {
            tasks
        }

        // Paso 2: Aplicar búsqueda de texto
        if (query.isNotBlank()) {
            val lowercaseQuery = query.lowercase()
            result = result.filter {
                it.title.lowercase().contains(lowercaseQuery) ||
                        it.description.lowercase().contains(lowercaseQuery)
            }
        }

        // Paso 3: Aplicar ordenación
        result = when (order) {
            TaskSortOrder.DATE_ASC -> result.sortedBy { it.dueDate }
            TaskSortOrder.DATE_DESC -> result.sortedByDescending { it.dueDate }
            TaskSortOrder.PRIORITY_HIGH_FIRST -> result.sortedBy { it.priority.ordinal }
            TaskSortOrder.PRIORITY_LOW_FIRST -> result.sortedByDescending { it.priority.ordinal }
            TaskSortOrder.COMPLETION_STATUS -> result.sortedBy { it.isDone }
        }

        return result
    }

    private fun loadTasks() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getTasks()
                    .catch { e ->
                        _error.value = e.message
                        _isLoading.value = false
                    }
                    .collect { taskList ->
                        _allTasks.value = taskList
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }

    // Métodos para actualizar filtros
    fun setPriorityFilter(priority: TaskPriority?) {
        _selectedPriority.value = priority
    }

    fun setSortOrder(order: TaskSortOrder) {
        _sortOrder.value = order
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Métodos para operaciones CRUD (mantener los que ya tenías)
    fun addTask(task: Task) {
        viewModelScope.launch {
            try {
                repository.addTask(task)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            try {
                repository.updateTask(task)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            try {
                repository.deleteTask(taskId)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun toggleTaskStatus(task: Task) {
        viewModelScope.launch {
            try {
                val updatedTask = task.copy(isDone = !task.isDone)
                repository.updateTask(updatedTask)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
}