package com.example.pc02quiliche22200144.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object FirebaseAuthManager {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    fun mapAuthError(e: Throwable): String {
        return when (e) {
            is FirebaseAuthInvalidUserException,
            is FirebaseAuthInvalidCredentialsException -> "El usuario no existe o la contraseña es inválida."
            is FirebaseAuthUserCollisionException -> "Ese correo ya está registrado."
            is FirebaseAuthWeakPasswordException -> "La contraseña debe tener al menos 6 caracteres."
            else -> e.message ?: "Ocurrió un error inesperado."
        }
    }

    suspend fun registerUser(name: String, email: String, password: String): Result<Unit> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user ?: throw Exception("No se pudo crear el usuario")

            try {
                firestore.collection("users")
                    .document(user.uid)
                    .set(mapOf("name" to name, "email" to email))
                    .await()
            } catch (firestoreError: Exception) {
                // Rollback: si no se pudo subir el perfil, no dejar una cuenta a medio crear.
                user.delete().await()
                throw firestoreError
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginUser(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
    }
}
