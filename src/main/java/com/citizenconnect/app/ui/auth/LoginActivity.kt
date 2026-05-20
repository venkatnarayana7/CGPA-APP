package com.citizenconnect.app.ui.auth
import android.content.Intent; import android.os.Bundle; import android.util.Patterns
import androidx.activity.viewModels; import androidx.appcompat.app.AppCompatActivity
import com.citizenconnect.app.databinding.ActivityLoginBinding
import com.citizenconnect.app.ui.home.HomeActivity
import com.citizenconnect.app.utils.*; import com.citizenconnect.app.viewmodel.AuthViewModel

class LoginActivity : AppCompatActivity() {
    private lateinit var b: ActivityLoginBinding; private val vm: AuthViewModel by viewModels()

    override fun onCreate(s: Bundle?) {
        super.onCreate(s); b = ActivityLoginBinding.inflate(layoutInflater); setContentView(b.root)
        
        b.cardForm.apply { 
            alpha = 0f; translationY = 60f
            animate().alpha(1f).translationY(0f).setDuration(600).setStartDelay(150).start() 
        }

        b.btnLogin.setOnClickListener {
            val email = b.etEmail.text.toString().trim()
            val pass = b.etPassword.text.toString().trim()
            var ok = true

            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                b.tilEmail.error = "Valid email required"; ok = false
            } else b.tilEmail.error = null

            if (pass.isEmpty()) {
                b.tilPassword.error = "Required"; ok = false
            } else b.tilPassword.error = null

            if (ok) {
                b.root.hideKeyboard()
                vm.login(email, pass)
            }
        }

        b.tvSignup.setOnClickListener { startActivity(Intent(this, SignupActivity::class.java)) }

        vm.loading.observe(this) { isLoading ->
            b.btnLogin.isEnabled = !isLoading
            if (isLoading) b.progressBar.visible() else b.progressBar.gone()
        }

        vm.loginResult.observe(this) { result ->
            result.onSuccess { 
                startActivity(Intent(this, HomeActivity::class.java).apply { 
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK 
                }) 
            }
            result.onFailure { b.root.showSnackbar(it.message ?: "Login failed") }
        }
    }
}
