package com.example.kotlin_to_do_app.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ThemeSwitch(
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    IconButton(onClick = onToggleTheme) {
        Icon(
            imageVector = if (isDarkTheme)
                Icons.Filled.LightMode  // Icono de sol para cambiar a modo claro
            else
                Icons.Filled.DarkMode,  // Icono de luna para cambiar a modo oscuro
            contentDescription = if (isDarkTheme)
                "Cambiar a tema claro"
            else
                "Cambiar a tema oscuro",
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(24.dp)
        )
    }
}