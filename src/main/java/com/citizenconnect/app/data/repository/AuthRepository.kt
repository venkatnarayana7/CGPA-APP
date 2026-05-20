package com.citizenconnect.app.data.repository

import android.content.Context
import com.citizenconnect.app.model.AppDatabase
import com.citizenconnect.app.model.User
import com.citizenconnect.app.utils.SessionManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class AuthRepository(private val context: Context) {
    private val dao = AppDatabase.getInstance(context).userDao()
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance()

    suspend fun login(email: String, password: String): Result<User> = try {
        val authResult = auth.signInWithEmailAndPassword(email.trim(), password).await()
        val firebaseUser = authResult.user ?: throw Exception("Login failed")
        
        var user = dao.getById(firebaseUser.uid)
        if (user == null) {
            user = User(uid = firebaseUser.uid, fullName = firebaseUser.displayName ?: "Citizen", email = firebaseUser.email ?: email)
            dao.insert(user)
        }
        
        SessionManager.save(context, user.uid, user.fullName, user.email)
        Result.success(user)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun signup(fullName: String, email: String, password: String, phone: String, city: String): Result<User> = try {
        val authResult = auth.createUserWithEmailAndPassword(email.trim(), password).await()
        val firebaseUser = authResult.user ?: throw Exception("Signup failed")
        
        val user = User(
            uid = firebaseUser.uid,
            fullName = fullName.trim(),
            email = email.trim().lowercase(),
            phone = phone.trim(),
            city = city.trim()
        )
        
        dao.insert(user)
        SessionManager.save(context, user.uid, user.fullName, user.email)
        Result.success(user)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun updateUser(user: User): Result<Unit> = try {
        // 1. Update Realtime Database
        db.getReference("users").child(user.uid).setValue(user).await()
        
        // 2. Update Local Room DB
        dao.insert(user)
        
        // 3. Update Session
        SessionManager.save(context, user.uid, user.fullName, user.email)
        
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun deleteAccount(): Result<Unit> = try {
        val user = auth.currentUser ?: throw Exception("No user logged in")
        val uid = user.uid

        db.getReference("complaints").orderByChild("userId").equalTo(uid)
            .get().await().children.forEach { it.ref.removeValue().await() }

        db.getReference("users").child(uid).removeValue().await()
        user.delete().await()
        dao.deleteById(uid)
        logout()
        
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getUser(uid: String): User? = dao.getById(uid)
    
    fun logout() {
        auth.signOut()
        SessionManager.logout(context)
    }
    
    fun currentUid(): String? = auth.currentUser?.uid ?: SessionManager.uid(context)
    fun isLoggedIn(): Boolean = auth.currentUser != null || SessionManager.isLoggedIn(context)
}
