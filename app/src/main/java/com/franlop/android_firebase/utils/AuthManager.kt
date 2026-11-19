package com.franlop.android_firebase.utils

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.franlop.android_firebase.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

sealed class AuthRes<out T> {
    data class Success<T>(val data: T) : AuthRes<T>()
    data class Error(val errorMessage: String) : AuthRes<Nothing>()
}

class AuthManager(private val context: Context) {
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    private val signInClient: GoogleSignInClient by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso)
    }

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

    // 5. Lanzar selector de cuentas de Google
    fun signInWithGoogle(googleSignInLauncher: ActivityResultLauncher<Intent>) {
        val signInIntent = signInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    // 6. Procesar resultado del selector de Google
    fun handleSignInResult(task: Task<GoogleSignInAccount>): AuthRes<GoogleSignInAccount>? {
        return try {
            val account = task.getResult(ApiException::class.java)
            AuthRes.Success(account)
        } catch (e: ApiException) {
            AuthRes.Error(e.message ?: "Google sign-in failed.")
        }
    }

    // 7. Autenticar credencial de Google en Firebase
    suspend fun signInWithGoogleCredential(credential: AuthCredential): AuthRes<FirebaseUser?> {
        return try {
            val firebaseUser = auth.signInWithCredential(credential).await()
            firebaseUser.user?.let {
                AuthRes.Success(it)
            } ?: throw Exception("Sign in with Google failed.")
        } catch (e: Exception) {
            AuthRes.Error(e.message ?: "Sign in with Google failed.")
        }
    }

    // 8. Cerrar sesión (Firebase + Google)
    fun signOut() {
        auth.signOut()
        signInClient.signOut()
    }

    // 9. Obtener usuario actual
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
}