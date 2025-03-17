package com.example.kotlin_to_do_app.repository

import com.example.kotlin_to_do_app.model.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class TaskRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val tasksCollection
        get() = firestore.collection("users")
            .document(auth.currentUser?.uid ?: "")
            .collection("tasks")

    // Obtener todas las tareas del usuario
    fun getTasks(): Flow<List<Task>> = callbackFlow {
        val listenerRegistration = tasksCollection
            .orderBy("dueDate", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val tasks = snapshot?.documents?.mapNotNull { document ->
                    val data = document.data
                    if (data != null) {
                        Task.fromMap(data)
                    } else null
                } ?: emptyList()

                trySend(tasks)
            }

        awaitClose { listenerRegistration.remove() }
    }

    // Añadir una nueva tarea
    suspend fun addTask(task: Task): String {
        val document = tasksCollection.document(task.id)
        document.set(task.toMap()).await()
        return task.id
    }

    // Actualizar una tarea existente
    suspend fun updateTask(task: Task) {
        tasksCollection.document(task.id).set(task.toMap()).await()
    }

    // Eliminar una tarea
    suspend fun deleteTask(taskId: String) {
        tasksCollection.document(taskId).delete().await()
    }

    // Obtener una tarea específica
    suspend fun getTask(taskId: String): Task? {
        val document = tasksCollection.document(taskId).get().await()
        val data = document.data
        return if (data != null) {
            Task.fromMap(data)
        } else null
    }
}