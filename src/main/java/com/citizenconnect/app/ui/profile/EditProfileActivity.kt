package com.citizenconnect.app.ui.profile

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.citizenconnect.app.databinding.ActivityEditProfileBinding
import com.citizenconnect.app.utils.SessionManager
import com.citizenconnect.app.utils.visible
import com.citizenconnect.app.utils.gone
import com.citizenconnect.app.viewmodel.AuthViewModel

class EditProfileActivity : AppCompatActivity() {
    private lateinit var b: ActivityEditProfileBinding
    private val vm: AuthViewModel by viewModels()

    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        b = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.toolbar.setNavigationOnClickListener { finish() }

        vm.loadUser()
        vm.user.observe(this) { u ->
            u?.let {
                b.etFullName.setText(it.fullName)
                b.etPhone.setText(it.phone)
                b.etCity.setText(it.city)
            }
        }

        b.btnSave.setOnClickListener {
            val name = b.etFullName.text.toString().trim()
            val phone = b.etPhone.text.toString().trim()
            val city = b.etCity.text.toString().trim()

            if (name.isEmpty()) {
                b.etFullName.error = "Name required"
                return@setOnClickListener
            }

            val user = vm.user.value?.copy(
                fullName = name,
                phone = phone,
                city = city
            )
            user?.let { vm.updateProfile(it) }
        }

        vm.loading.observe(this) { if (it) b.btnSave.isEnabled = false else b.btnSave.isEnabled = true }
        
        vm.updateResult.observe(this) { res ->
            res.onSuccess {
                Toast.makeText(this, "Profile Updated!", Toast.LENGTH_SHORT).show()
                finish()
            }.onFailure {
                Toast.makeText(this, "Update Failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
