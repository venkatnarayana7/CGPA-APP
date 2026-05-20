package com.citizenconnect.app.ui.report

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.citizenconnect.app.databinding.ActivityReportIssueBinding
import com.citizenconnect.app.model.Complaint
import com.citizenconnect.app.utils.*
import com.citizenconnect.app.viewmodel.ComplaintViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ReportIssueActivity : AppCompatActivity() {
    private lateinit var b: ActivityReportIssueBinding
    private val vm: ComplaintViewModel by viewModels()
    private val loc by lazy { LocationHelper(this) }
    private var imageUri: Uri? = null
    private var photoFile: File? = null
    private var lat = 0.0
    private var lng = 0.0
    private var address = ""

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            photoFile?.let { f ->
                lifecycleScope.launch {
                    val uri = Uri.fromFile(f)
                    val compressedUri = withContext(Dispatchers.IO) {
                        ImageUtils.compressImage(this@ReportIssueActivity, uri)
                    }
                    imageUri = compressedUri ?: uri
                    b.ivPhoto.gone()
                    b.ivPhotoPreview.visible()
                    b.ivPhotoPreview.setImageURI(imageUri)
                }
            }
        }
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            lifecycleScope.launch {
                val compressedUri = withContext(Dispatchers.IO) {
                    ImageUtils.compressImage(this@ReportIssueActivity, it)
                }
                imageUri = compressedUri ?: it
                b.ivPhoto.gone()
                b.ivPhotoPreview.visible()
                b.ivPhotoPreview.setImageURI(imageUri)
            }
        }
    }

    private val permLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { p ->
        if (p[Manifest.permission.CAMERA] == true) launchCamera()
        if (p[Manifest.permission.ACCESS_FINE_LOCATION] == true) fetchLocation()
    }

    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        b = ActivityReportIssueBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.toolbar.setNavigationOnClickListener { finish() }
        b.actvCategory.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, resources.getStringArray(com.citizenconnect.app.R.array.issue_categories)))
        b.actvCategory.threshold = 0

        b.btnCamera.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                launchCamera()
            } else {
                permLauncher.launch(arrayOf(Manifest.permission.CAMERA))
            }
        }

        b.btnGallery.setOnClickListener { galleryLauncher.launch("image/*") }
        b.btnRefreshLocation.setOnClickListener { fetchLocation() }
        b.btnSubmit.setOnClickListener { submit() }

        fetchLocation()

        vm.loading.observe(this) { if (it) b.submitProgress.visible() else b.submitProgress.gone() }
        vm.submitResult.observe(this) { r ->
            r.onSuccess {
                b.root.showSnackbar("✅ Complaint submitted!")
                finish()
            }
            r.onFailure { b.root.showSnackbar(it.message ?: "Failed") }
        }
    }

    private fun launchCamera() = try {
        val ts = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        photoFile = File.createTempFile("CC_${ts}_", ".jpg", getExternalFilesDir(Environment.DIRECTORY_PICTURES))
        cameraLauncher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(this, "${packageName}.fileprovider", photoFile!!)))
    } catch (e: Exception) {
        b.root.showSnackbar("Camera not available")
    }

    private fun fetchLocation() {
        b.locationProgress.visible()
        b.tvLocation.text = "Fetching location…"
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
            return
        }
        loc.getCurrentLocation(
            onResult = { la, ln, addr ->
                lat = la
                lng = ln
                address = addr
                b.tvLocation.text = addr
                b.locationProgress.gone()
            },
            onError = {
                b.tvLocation.text = "Could not get location. Tap Refresh."
                b.locationProgress.gone()
            }
        )
    }

    private fun submit() {
        val title = b.etTitle.text.toString().trim()
        val cat = b.actvCategory.text.toString().trim()
        val desc = b.etDescription.text.toString().trim()
        var ok = true
        if (title.isEmpty()) {
            b.tilTitle.error = "Required"
            ok = false
        } else b.tilTitle.error = null
        if (cat.isEmpty()) {
            b.tilCategory.error = "Required"
            ok = false
        } else b.tilCategory.error = null
        if (desc.isEmpty()) {
            b.tilDescription.error = "Required"
            ok = false
        } else b.tilDescription.error = null
        if (!ok) return
        val uid = SessionManager.uid(this) ?: run {
            b.root.showSnackbar("Please log in first")
            return
        }
        vm.submit(Complaint(userId = uid, userName = SessionManager.name(this), title = title, description = desc, category = cat, imageUri = imageUri?.toString() ?: "", latitude = lat, longitude = lng, address = address))
    }
}
