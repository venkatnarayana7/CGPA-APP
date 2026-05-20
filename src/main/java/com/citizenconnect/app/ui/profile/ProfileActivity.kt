package com.citizenconnect.app.ui.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.citizenconnect.app.databinding.ActivityProfileBinding
import com.citizenconnect.app.model.ComplaintStatus
import com.citizenconnect.app.ui.auth.LoginActivity
import com.citizenconnect.app.ui.complaints.MyComplaintsActivity
import com.citizenconnect.app.ui.home.HomeActivity
import com.citizenconnect.app.utils.*
import com.citizenconnect.app.viewmodel.AuthViewModel
import com.citizenconnect.app.viewmodel.ComplaintViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {
    private lateinit var b: ActivityProfileBinding
    private val authVm: AuthViewModel by viewModels()
    private val vm: ComplaintViewModel by viewModels()

    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        b = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(b.root)

        val uid = SessionManager.uid(this) ?: run { finish(); return }
        b.tvName.text = SessionManager.name(this)
        b.tvEmail.text = SessionManager.email(this)
        
        updateLanguageText()

        authVm.loadUser()
        vm.load(uid)

        b.itemEditProfile.setOnClickListener { 
            startActivity(Intent(this, EditProfileActivity::class.java)) 
        }
        
        b.itemMyComplaints.setOnClickListener { 
            startActivity(Intent(this, MyComplaintsActivity::class.java)) 
        }

        b.itemLanguage.setOnClickListener { showLanguageDialog() }
        
        b.btnDeleteAccount.setOnClickListener { showDeleteAccountConfirmation() }

        b.btnLogout.setOnClickListener {
            MaterialAlertDialogBuilder(this).setTitle("Logout").setMessage("Are you sure?")
                .setPositiveButton("Logout") { _, _ -> 
                    authVm.logout()
                    startActivity(Intent(this, LoginActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK }) 
                }
                .setNegativeButton("Cancel", null).show()
        }

        authVm.user.observe(this) { u -> 
            b.tvName.text = u?.fullName?.ifEmpty { SessionManager.name(this) } ?: SessionManager.name(this)
            b.tvEmail.text = u?.email ?: SessionManager.email(this)
            b.tvCity.text = if (u?.city?.isNotEmpty() == true) "📍 ${u.city}" else "📍 Not set" 
        }

        vm.complaints.observe(this) { list -> 
            b.tvStatTotal.text = list.size.toString()
            b.tvStatResolved.text = list.count { it.status == ComplaintStatus.RESOLVED.name }.toString()
            b.tvStatPending.text = list.count { it.status != ComplaintStatus.RESOLVED.name }.toString() 
        }
    }

    private fun updateLanguageText() {
        val lang = SessionManager.getLanguage()
        b.tvCurrentLanguage.text = if (lang == "hi") "हिन्दी" else "English"
    }

    private fun showLanguageDialog() {
        val langs = arrayOf("English", "हिन्दी")
        val currentLang = if (SessionManager.getLanguage() == "hi") 1 else 0
        
        MaterialAlertDialogBuilder(this)
            .setTitle("Select Language")
            .setSingleChoiceItems(langs, currentLang) { dialog, which ->
                val selected = if (which == 1) "hi" else "en"
                SessionManager.setLanguage(selected)
                dialog.dismiss()
            }
            .show()
    }

    private fun showDeleteAccountConfirmation() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Delete My Account")
            .setMessage("Are you sure you want to permanently delete your account and all your data? This action cannot be undone.")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("PERMANENTLY DELETE") { _, _ ->
                performDeleteAccount()
            }
            .show()
    }

    private fun performDeleteAccount() {
        lifecycleScope.launch {
            val result = authVm.deleteAccount()
            if (result.isSuccess) {
                Toast.makeText(this@ProfileActivity, "Account Deleted Permanently", Toast.LENGTH_LONG).show()
                startActivity(Intent(this@ProfileActivity, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
                finish()
            } else {
                Toast.makeText(this@ProfileActivity, "Failed to delete: ${result.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
