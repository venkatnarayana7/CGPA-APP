package com.citizenconnect.app.model
import android.content.Context
import androidx.room.Database
import androidx.room.Entity; import androidx.room.PrimaryKey
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.citizenconnect.app.data.db.ComplaintDao
import com.citizenconnect.app.data.db.Converters
import com.citizenconnect.app.data.db.UserDao

@Entity(tableName = "users")
data class User(
    @PrimaryKey val uid: String,
    val fullName: String = "", val email: String = "",
    val phone: String = "", val city: String = "", val passwordHash: String = ""
)

@Database(entities = [User::class, Complaint::class], version = 4, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun complaintDao(): ComplaintDao
    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        fun getInstance(ctx: Context): AppDatabase = INSTANCE ?: synchronized(this) {
            Room.databaseBuilder(ctx.applicationContext, AppDatabase::class.java, "citizenconnect.db")
                .fallbackToDestructiveMigration().build().also { INSTANCE = it }
        }
    }
}