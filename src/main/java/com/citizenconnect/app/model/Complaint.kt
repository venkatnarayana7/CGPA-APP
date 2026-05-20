package com.citizenconnect.app.model
import androidx.room.Entity; import androidx.room.PrimaryKey; import com.citizenconnect.app.R
@Entity(tableName = "complaints")
data class Complaint(
    @PrimaryKey val id: String = java.util.UUID.randomUUID().toString(),
    val userId: String = "", val userName: String = "",
    val title: String = "", val description: String = "",
    val category: String = "", val imageUri: String = "",
    val latitude: Double = 0.0, val longitude: Double = 0.0, val address: String = "",
    val status: String = "SUBMITTED",
    val department: String = "General",
    val upvoteCount: Int = 0,
    val upvotedBy: List<String> = emptyList(),
    val isSynced: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun getStatusEnum(): ComplaintStatus =
        runCatching { ComplaintStatus.valueOf(status) }.getOrDefault(ComplaintStatus.SUBMITTED)
}
enum class ComplaintStatus {
    SUBMITTED, ACKNOWLEDGED, IN_PROGRESS, RESOLVED;
    fun displayName() = when(this) {
        SUBMITTED->"Submitted"; ACKNOWLEDGED->"Acknowledged"; IN_PROGRESS->"In Progress"; RESOLVED->"Resolved"
    }
    fun colorRes() = when(this) {
        SUBMITTED->R.color.status_submitted; ACKNOWLEDGED->R.color.status_acknowledged
        IN_PROGRESS->R.color.status_in_progress; RESOLVED->R.color.status_resolved
    }
    fun bgRes() = when(this) {
        SUBMITTED->R.drawable.bg_chip_submitted; ACKNOWLEDGED->R.drawable.bg_chip_acknowledged
        IN_PROGRESS->R.drawable.bg_chip_in_progress; RESOLVED->R.drawable.bg_chip_resolved
    }
}
