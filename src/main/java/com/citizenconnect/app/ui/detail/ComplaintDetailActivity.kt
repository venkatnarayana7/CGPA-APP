package com.citizenconnect.app.ui.detail
import android.content.Intent; import android.net.Uri; import android.os.Bundle
import androidx.activity.viewModels; import androidx.appcompat.app.AlertDialog; import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide; import com.citizenconnect.app.R
import com.citizenconnect.app.databinding.ActivityComplaintDetailBinding
import com.citizenconnect.app.model.Complaint; import com.citizenconnect.app.model.ComplaintStatus
import com.citizenconnect.app.utils.*; import com.citizenconnect.app.viewmodel.ComplaintViewModel
class ComplaintDetailActivity : AppCompatActivity() {
    private lateinit var b: ActivityComplaintDetailBinding; private val vm: ComplaintViewModel by viewModels()
    private var complaintId: String? = null
    private var currentComplaint: Complaint? = null

    override fun onCreate(s: Bundle?) {
        super.onCreate(s); b = ActivityComplaintDetailBinding.inflate(layoutInflater); setContentView(b.root)
        b.toolbar.setNavigationOnClickListener { finish() }
        complaintId = intent.getStringExtra("complaint_id") ?: run { finish(); return }
        vm.loadById(complaintId!!)
        
        b.btnDelete.setOnClickListener { showDeleteConfirmation() }
        b.btnShare.setOnClickListener { shareComplaint() }
        
        vm.loading.observe(this) { if (it) b.progressBar.visible() else b.progressBar.gone() }
        vm.selectedComplaint.observe(this) { c ->
            c ?: return@observe; currentComplaint = c; val s=c.getStatusEnum()
            b.tvStatusValue.text=s.displayName(); b.tvStatusValue.setTextColor(getColor(s.colorRes())); b.tvStatusLabel.setTextColor(getColor(s.colorRes())); b.statusBanner.setBackgroundResource(s.bgRes())
            b.tvCategory.text=c.category; b.tvTitle.text=c.title; b.tvDescription.text=c.description
            b.tvAddress.text=c.address.ifEmpty{"Location not recorded"}; b.tvDate.text=c.createdAt.toFormattedDateTime(); b.tvSubmittedDate.text=c.createdAt.toFormattedDateTime()
            if (c.imageUri.isNotEmpty()) { b.cardPhoto.visible(); Glide.with(this).load(Uri.parse(c.imageUri)).placeholder(R.drawable.bg_photo_placeholder).into(b.ivPhoto) }
            
            if (s != ComplaintStatus.RESOLVED) {
                b.btnResolve.visible()
                b.btnResolve.setOnClickListener { vm.updateStatus(c.id, ComplaintStatus.RESOLVED) }
            } else { b.btnResolve.gone() }
        }
    }

    private fun shareComplaint() {
        val c = currentComplaint ?: return
        val shareText = """
            📢 *Citizen Issue Reported*
            
            *Title:* ${c.title}
            *Category:* ${c.category}
            *Status:* ${c.status}
            *Location:* ${c.address.ifEmpty { "Location shared in app" }}
            
            *Description:* ${c.description}
            
            _Reported via CitizenConnect App_
        """.trimIndent()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        startActivity(Intent.createChooser(intent, "Share via"))
    }

    private fun showDeleteConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Delete Complaint")
            .setMessage("Are you sure you want to delete this complaint? This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                complaintId?.let { vm.deleteById(it); finish() }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
