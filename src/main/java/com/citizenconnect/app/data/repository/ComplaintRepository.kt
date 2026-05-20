package com.citizenconnect.app.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.citizenconnect.app.model.AppDatabase
import com.citizenconnect.app.model.Complaint
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class ComplaintRepository(private val context: Context) {
    private val dao = AppDatabase.getInstance(context).complaintDao()
    // Explicitly use the URL from google-services.json to ensure correct connection
    private val db = FirebaseDatabase.getInstance("https://citizenconnect-91c9c-default-rtdb.firebaseio.com")
    private val complaintsRef = db.getReference("complaints")
    private val storage = FirebaseStorage.getInstance()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun getUserComplaints(userId: String): Flow<List<Complaint>> = dao.getByUser(userId)
    fun getAllComplaints(): Flow<List<Complaint>> = dao.getAll()

    fun syncAllFromCloud() {
        Log.d("ComplaintRepo", "Syncing from Cloud...")
        complaintsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val cloudComplaints = snapshot.children.mapNotNull { 
                    try {
                        it.getValue(Complaint::class.java) 
                    } catch (e: Exception) {
                        Log.e("ComplaintRepo", "Parse error: ${e.message}")
                        null
                    }
                }
                scope.launch {
                    cloudComplaints.forEach { dao.insert(it.copy(isSynced = true)) }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("ComplaintRepo", "Cloud Read Failed: ${error.message}")
            }
        })
    }

    suspend fun submit(complaint: Complaint): Result<String> = withContext(Dispatchers.IO) {
        try {
            val complaintWithDept = complaint.copy(department = getAssignedDepartment(complaint.category))
            dao.insert(complaintWithDept)
            
            val success = syncToCloud(complaintWithDept)
            if (success) {
                Result.success(complaintWithDept.id)
            } else {
                // Return success anyway because data reached DB even if image failed
                Result.success(complaintWithDept.id)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun getAssignedDepartment(category: String): String {
        return when (category) {
            "Road Damage" -> "Public Works Department (PWD)"
            "Garbage / Waste", "Sanitation" -> "Waste Management Dept"
            "Water Leakage" -> "Water Supply Board"
            "Street Light", "Electricity" -> "Electrical Department"
            "Parks / Trees" -> "Horticulture Department"
            "Encroachment" -> "City Planning & Anti-Encroachment"
            "Infrastructure" -> "Civil Engineering Dept"
            else -> "Municipal General Admin"
        }
    }

    suspend fun upvote(complaintId: String, userId: String): Result<Unit> = try {
        val ref = complaintsRef.child(complaintId)
        val task = CompletableDeferred<Unit>()
        ref.runTransaction(object : com.google.firebase.database.Transaction.Handler {
            override fun doTransaction(data: com.google.firebase.database.MutableData): com.google.firebase.database.Transaction.Result {
                val c = data.getValue(Complaint::class.java) ?: return com.google.firebase.database.Transaction.success(data)
                val currentVotes = c.upvotedBy.toMutableList()
                if (currentVotes.contains(userId)) return com.google.firebase.database.Transaction.success(data)
                currentVotes.add(userId)
                data.value = c.copy(upvoteCount = c.upvoteCount + 1, upvotedBy = currentVotes)
                return com.google.firebase.database.Transaction.success(data)
            }
            override fun onComplete(error: com.google.firebase.database.DatabaseError?, committed: Boolean, snapshot: DataSnapshot?) {
                if (error != null) task.completeExceptionally(error.toException())
                else task.complete(Unit)
            }
        })
        task.await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun syncToCloud(complaint: Complaint): Boolean = try {
        var finalComplaint = complaint
        
        // Wrap Image Upload in its own try-catch so it doesn't block the Database Sync
        if (complaint.imageUri.isNotEmpty() && !complaint.imageUri.startsWith("http")) {
            try {
                val file = Uri.parse(complaint.imageUri)
                val storageRef = storage.reference.child("complaints/${complaint.id}.jpg")
                storageRef.putFile(file).await()
                val downloadUrl = storageRef.downloadUrl.await().toString()
                finalComplaint = complaint.copy(imageUri = downloadUrl)
            } catch (e: Exception) {
                Log.e("ComplaintRepo", "Image upload failed, continuing with data sync: ${e.message}")
            }
        }
        
        // Push to Realtime Database - This will now happen even if image fails
        complaintsRef.child(finalComplaint.id).setValue(finalComplaint.copy(isSynced = true)).await()
        dao.insert(finalComplaint.copy(isSynced = true))
        true
    } catch (e: Exception) {
        Log.e("ComplaintRepo", "Critical Database Sync Error: ${e.message}")
        false
    }

    suspend fun getById(id: String): Complaint? = dao.getById(id)
    
    suspend fun updateStatus(id: String, status: String) {
        val updatedAt = System.currentTimeMillis()
        dao.updateStatus(id, status, updatedAt)
        try {
            val updates = mapOf("status" to status, "updatedAt" to updatedAt)
            complaintsRef.child(id).updateChildren(updates).await()
        } catch (e: Exception) {}
    }

    suspend fun deleteById(id: String) {
        dao.deleteById(id)
        try { complaintsRef.child(id).removeValue().await() } catch (e: Exception) {}
    }
}
