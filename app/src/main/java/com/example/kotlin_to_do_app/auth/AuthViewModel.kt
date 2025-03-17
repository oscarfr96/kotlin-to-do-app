package com.example.kotlin_to_do_app.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Initial)
    val loginState: StateFlow<LoginState> = _loginState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                _loginState.value = LoginState.Loading

                if (email.isBlank() || password.isBlank()) {
                    _loginState.value = LoginState.Error("Email y contraseña son requeridos")
                    return@launch
                }

                auth.signInWithEmailAndPassword(email, password).await()
                _loginState.value = LoginState.Success
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.message ?: "Error de autenticación")
            }
        }
    }

    fun register(email: String, password: String) {
        viewModelScope.launch {
            try {
                _loginState.value = LoginState.Loading

                if (email.isBlank() || password.isBlank()) {
                    _loginState.value = LoginState.Error("Email y contraseña son requeridos")
                    return@launch
                }

                if (password.length < 6) {
                    _loginState.value = LoginState.Error("La contraseña debe tener al menos 6 caracteres")
                    return@launch
                }

                auth.createUserWithEmailAndPassword(email, password).await()
                _loginState.value = LoginState.Success
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.message ?: "Error al registrarse")
            }
        }
    }

    fun logout() {
        auth.signOut()
        _loginState.value = LoginState.Initial
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }
}

sealed class LoginState {
    object Initial : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}