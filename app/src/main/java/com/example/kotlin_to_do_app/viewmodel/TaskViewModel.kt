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
import kotlin.comparisons.nullsLast
import java.text.SimpleDateFormat
import java.util.*

enum class TaskSortOrder {
    DATE_ASC, DATE_DESC, PRIORITY_HIGH_FIRST, PRIORITY_LOW_FIRST, COMPLETION_STATUS
}

class TaskViewModel : ViewModel() {
    private val repository = TaskRepository()

    // Fuente de datos original
    private val _allTasks = MutableStateFlow<List<Task>>(emptyList())
    val allTasks: StateFlow<List<Task>> = _allTasks

    // Estados para filtros y ordenación
    private val _selectedPriority = MutableStateFlow<TaskPriority?>(null)
    val selectedPriority: StateFlow<TaskPriority?> = _selectedPriority

    private val _sortOrder = MutableStateFlow(TaskSortOrder.DATE_ASC)
    val sortOrder: StateFlow<TaskSortOrder> = _sortOrder

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    // Nuevo estado para fecha seleccionada
    private val _selectedDate = MutableStateFlow<Date?>(null)
    val selectedDate: StateFlow<Date?> = _selectedDate

    // Lista filtrada y ordenada que se muestra al usuario
    private val _filteredTasks = MutableStateFlow<List<Task>>(emptyList())
    val filteredTasks: StateFlow<List<Task>> = _filteredTasks

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // Estado para la vista actual (lista o calendario)
    private val _isCalendarView = MutableStateFlow(false)
    val isCalendarView: StateFlow<Boolean> = _isCalendarView

    init {
        loadTasks()

        // Combinar estados para aplicar filtros y ordenación
        viewModelScope.launch {
            combine(
                _allTasks,
                _selectedPriority,
                _sortOrder,
                _searchQuery,
                _selectedDate
            ) { tasks, priority, order, query, date ->
                applyFiltersAndSort(tasks, priority, order, query, date)
            }.collect { filtered ->
                _filteredTasks.value = filtered
            }
        }
    }

    private fun applyFiltersAndSort(
        tasks: List<Task>,
        priority: TaskPriority?,
        order: TaskSortOrder,
        query: String,
        date: Date?
    ): List<Task> {
        // Paso 1: Aplicar filtro de prioridad
        var result = if (priority != null) {
            tasks.filter { it.priority == priority }
        } else {
            tasks
        }

        // Paso 2: Aplicar filtro de fecha
        if (date != null) {
            val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
            result = result.filter { task ->
                task.dueDate?.let { dateFormat.format(it) == dateFormat.format(date) } ?: false
            }
        }

        // Paso 3: Aplicar búsqueda de texto
        if (query.isNotBlank()) {
            val lowercaseQuery = query.lowercase()
            result = result.filter {
                it.title.lowercase().contains(lowercaseQuery) ||
                        it.description.lowercase().contains(lowercaseQuery)
            }
        }

        // Paso 4: Aplicar ordenación
        result = when (order) {
            TaskSortOrder.DATE_ASC -> result.sortedWith(compareBy(nullsLast()) { it.dueDate })
            TaskSortOrder.DATE_DESC -> result.sortedWith(compareByDescending(nullsLast()) { it.dueDate })
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

    // Nuevo método para establecer la fecha seleccionada
    fun setSelectedDate(date: Date?) {
        _selectedDate.value = date
    }

    // Método para alternar la vista
    fun toggleCalendarView() {
        val newValue = !_isCalendarView.value
        _isCalendarView.value = newValue

        // Si estamos cambiando a vista de lista, limpiar el filtro de fecha
        if (!newValue) {
            setSelectedDate(null)
        }
    }

    // Métodos para operaciones CRUD existentes...
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