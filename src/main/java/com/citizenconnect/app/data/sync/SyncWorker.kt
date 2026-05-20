package com.citizenconnect.app.data.sync

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.ListenableWorker.Result
import com.citizenconnect.app.model.AppDatabase
import kotlinx.coroutines.flow.first

class SyncWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val db = AppDatabase.getInstance(applicationContext)
        
        return try {
            Log.d("SyncWorker", "Starting background sync...")
            
            // 1. Fetch all complaints (for simulation, we fetch all and filter)
            // In a real app, you'd add a DAO method like getUnsyncedComplaints()
            val allComplaints = db.complaintDao().getByUser("all_users_placeholder").first() 
            val unsynced = allComplaints.filter { !it.isSynced }
            
            if (unsynced.isEmpty()) {
                Log.d("SyncWorker", "Nothing to sync.")
                return Result.success()
            }

            Log.d("SyncWorker", "Syncing ${unsynced.size} complaints...")
            
            unsynced.forEach { complaint ->
                // SIMULATION: In a real app, you would upload to server here via Retrofit
                // val response = apiService.upload(complaint)
                // if (response.isSuccessful) { ... }
                
                // 2. Mark as synced in local DB
                db.complaintDao().insert(complaint.copy(isSynced = true))
                Log.d("SyncWorker", "Synced: ${complaint.title}")
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("SyncWorker", "Sync failed", e)
            Result.retry()
        }
    }
}
