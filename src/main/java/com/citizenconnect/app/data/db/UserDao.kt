package com.citizenconnect.app.data.db
import androidx.room.*; import com.citizenconnect.app.model.User
@Dao interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(user: User)
    @Query("SELECT * FROM users WHERE uid = :uid LIMIT 1") suspend fun getById(uid: String): User?
    @Query("SELECT * FROM users WHERE email = :email AND passwordHash = :hash LIMIT 1") suspend fun login(email: String, hash: String): User?
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1") suspend fun getByEmail(email: String): User?
    @Query("DELETE FROM users WHERE uid = :uid") suspend fun deleteById(uid: String)
}
