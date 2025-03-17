package com.example.kotlin_to_do_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlin_to_do_app.auth.AuthViewModel
import com.example.kotlin_to_do_app.ui.AppNavigation
import com.example.kotlin_to_do_app.ui.theme.ToDoAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ToDoAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val authViewModel: AuthViewModel = viewModel()
                    AppNavigation(authViewModel = authViewModel)
                }
            }
        }
    }
}