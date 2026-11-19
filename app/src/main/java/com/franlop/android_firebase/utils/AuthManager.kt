package com.franlop.android_firebase.utils

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

sealed class AuthRes<out T> {
    data class Success<T>(val data: T) : AuthRes<T>()
    data class Error(val errorMessage: String) : AuthRes<Nothing>()
}

class AuthManager(private val context: Context) {
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    // 1. Autenticación anónima (Invitado)
    suspend fun signInAnonymously(): AuthRes<FirebaseUser?> {
        return try {
            val result = auth.signInAnonymously().await()
            AuthRes.Success(result.user)
        } catch (e: Exception) {
            AuthRes.Error(e.message ?: "Error al iniciar sesión como invitado")
        }
    }

    // 2. Cerrar sesión
    fun signOut() {
        auth.signOut()
    }

    // 3. Obtener usuario actual (para persistencia de sesión y mostrar datos en Home)
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
}