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

    // 2. Crear usuario con correo y contraseña
    suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String
    ): AuthRes<FirebaseUser?> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            AuthRes.Success(result.user)
        } catch (e: Exception) {
            AuthRes.Error(e.message ?: "Error al registrar usuario")
        }
    }

    // 3. Iniciar sesión con correo y contraseña
    suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): AuthRes<FirebaseUser?> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            AuthRes.Success(result.user)
        } catch (e: Exception) {
            AuthRes.Error(e.message ?: "Error al iniciar sesión")
        }
    }

    // 4. Enviar correo para restablecer contraseña
    suspend fun resetPassword(email: String): AuthRes<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            AuthRes.Success(Unit)
        } catch (e: Exception) {
            AuthRes.Error(e.message ?: "Error al enviar el correo de recuperación")
        }
    }

    // 5. Cerrar sesión
    fun signOut() {
        auth.signOut()
    }

    // 6. Obtener usuario actual
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
}