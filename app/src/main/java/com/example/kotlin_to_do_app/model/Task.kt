package com.example.kotlin_to_do_app.model

import com.google.firebase.Timestamp
import java.util.Date
import java.util.UUID

enum class TaskPriority(val value: String, val displayName: String) {
    HIGH("P1", "Alta"),
    MEDIUM("P2", "Media"),
    LOW("P3", "Baja")
}

data class Task(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val description: String = "",
    val dueDate: Date? = null,
    val isDone: Boolean = false,
    val priority: TaskPriority = TaskPriority.MEDIUM
) {
    // Convertir a HashMap para Firestore
    fun toMap(): Map<String, Any?> {
        return hashMapOf(
            "id" to id,
            "title" to title,
            "description" to description,
            "dueDate" to dueDate?.let { Timestamp(it) }, // Convertir Date a Timestamp
            "isDone" to isDone,
            "priority" to priority.value
        )
    }

    companion object {
        // Crear desde un documento de Firestore
        fun fromMap(data: Map<String, Any>): Task {
            // Manejar la fecha correctamente según cómo esté almacenada
            val dueDate = when (val dueDateValue = data["dueDate"]) {
                is Timestamp -> dueDateValue.toDate()  // Convertir Timestamp a Date
                is Date -> dueDateValue                // Ya es Date
                else -> null                           // No hay fecha
            }

            return Task(
                id = data["id"] as? String ?: UUID.randomUUID().toString(),
                title = data["title"] as? String ?: "",
                description = data["description"] as? String ?: "",
                dueDate = dueDate,
                isDone = data["isDone"] as? Boolean ?: false,
                priority = when(data["priority"] as? String) {
                    TaskPriority.HIGH.value -> TaskPriority.HIGH
                    TaskPriority.LOW.value -> TaskPriority.LOW
                    else -> TaskPriority.MEDIUM
                }
            )
        }
    }
}