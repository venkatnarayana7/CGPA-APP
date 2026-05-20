package com.citizenconnect.app.data.db
import androidx.room.*
import com.citizenconnect.app.model.Complaint
import kotlinx.coroutines.flow.Flow

@Dao interface ComplaintDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(complaint: Complaint)
    
    @Query("SELECT * FROM complaints WHERE userId = :userId ORDER BY createdAt DESC") 
    fun getByUser(userId: String): Flow<List<Complaint>>
    
    @Query("SELECT * FROM complaints ORDER BY createdAt DESC") 
    fun getAll(): Flow<List<Complaint>>
    
    @Query("SELECT * FROM complaints WHERE id = :id LIMIT 1") suspend fun getById(id: String): Complaint?
    @Query("UPDATE complaints SET status = :status, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateStatus(id: String, status: String, updatedAt: Long)
    @Query("DELETE FROM complaints WHERE id = :id") suspend fun deleteById(id: String)
}
