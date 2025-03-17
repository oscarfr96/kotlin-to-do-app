package com.example.kotlin_to_do_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlin_to_do_app.model.Task
import com.example.kotlin_to_do_app.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class TaskViewModel : ViewModel() {
    private val repository = TaskRepository()

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadTasks()
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
                        _tasks.value = taskList
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }

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