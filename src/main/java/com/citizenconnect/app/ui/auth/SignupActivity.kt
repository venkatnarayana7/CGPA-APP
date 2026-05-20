package com.citizenconnect.app.ui.auth
import android.content.Intent; import android.net.Uri; import android.os.Bundle; import android.util.Patterns
import androidx.activity.viewModels; import androidx.appcompat.app.AppCompatActivity
import com.citizenconnect.app.databinding.ActivitySignupBinding
import com.citizenconnect.app.ui.home.HomeActivity
import com.citizenconnect.app.utils.*; import com.citizenconnect.app.viewmodel.AuthViewModel

class SignupActivity : AppCompatActivity() {
    private lateinit var b: ActivitySignupBinding; private val vm: AuthViewModel by viewModels()

    override fun onCreate(s: Bundle?) {
        super.onCreate(s); b = ActivitySignupBinding.inflate(layoutInflater); setContentView(b.root)
        
        b.cardForm.apply { 
            alpha = 0f; translationY = 60f
            animate().alpha(1f).translationY(0f).setDuration(600).setStartDelay(150).start() 
        }

        b.tvPrivacyPolicy.setOnClickListener {
            val url = "https://sites.google.com/view/citizenconnectsih/home"
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }

        b.btnSignup.setOnClickListener {
            val n = b.etFullName.text.toString().trim()
            val e = b.etEmail.text.toString().trim()
            val ph = b.etPhone.text.toString().trim()
            val c = b.etCity.text.toString().trim()
            val p = b.etPassword.text.toString().trim()
            val cp = b.etConfirmPassword.text.toString().trim()
            var ok = true

            if (!b.cbPrivacy.isChecked) {
                b.root.showSnackbar("Please agree to the Privacy Policy")
                ok = false
            }

            if (n.isEmpty()) { b.tilFullName.error = "Required"; ok = false } else b.tilFullName.error = null
            
            if (e.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(e).matches()) {
                b.tilEmail.error = "Valid email required"; ok = false
            } else b.tilEmail.error = null

            if (ph.isEmpty() || ph.length < 10) { 
                b.tilPhone.error = "Valid phone required"; ok = false 
            } else b.tilPhone.error = null

            if (p.length < 6) { b.tilPassword.error = "Min 6 characters"; ok = false } else b.tilPassword.error = null
            
            if (p != cp) { b.tilConfirmPassword.error = "Passwords don't match"; ok = false } else b.tilConfirmPassword.error = null

            if (ok) {
                b.root.hideKeyboard()
                vm.signup(n, e, p, ph, c)
            }
        }

        b.tvLogin.setOnClickListener { finish() }

        vm.loading.observe(this) { isLoading ->
            b.btnSignup.isEnabled = !isLoading
            if (isLoading) b.progressBar.visible() else b.progressBar.gone()
        }

        vm.signupResult.observe(this) { result ->
            result.onSuccess { 
                startActivity(Intent(this, HomeActivity::class.java).apply { 
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK 
                }) 
            }
            result.onFailure { b.root.showSnackbar(it.message ?: "Signup failed") }
        }
    }
}
