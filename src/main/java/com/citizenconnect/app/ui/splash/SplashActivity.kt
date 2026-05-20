package com.citizenconnect.app.ui.splash
import android.annotation.SuppressLint; import android.content.Intent; import android.os.Bundle; import android.view.View
import androidx.appcompat.app.AppCompatActivity; import androidx.lifecycle.lifecycleScope
import com.citizenconnect.app.databinding.ActivitySplashBinding
import com.citizenconnect.app.ui.auth.LoginActivity; import com.citizenconnect.app.ui.home.HomeActivity
import com.citizenconnect.app.utils.SessionManager; import kotlinx.coroutines.delay; import kotlinx.coroutines.launch
@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private lateinit var b: ActivitySplashBinding
    override fun onCreate(s: Bundle?) {
        super.onCreate(s); b = ActivitySplashBinding.inflate(layoutInflater); setContentView(b.root)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        b.cardLogo.apply { alpha=0f; scaleX=0.5f; scaleY=0.5f; animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(700).setStartDelay(100).setInterpolator(android.view.animation.OvershootInterpolator(1.4f)).start() }
        b.tvAppName.apply { alpha=0f; translationY=50f; animate().alpha(1f).translationY(0f).setDuration(600).setStartDelay(550).start() }
        b.tvTagline.apply { alpha=0f; animate().alpha(1f).setDuration(500).setStartDelay(850).start() }
        b.progressContainer.apply { alpha=0f; animate().alpha(1f).setDuration(400).setStartDelay(1100).start() }
        lifecycleScope.launch {
            delay(2600L)
            val dest = if (SessionManager.isLoggedIn(this@SplashActivity)) HomeActivity::class.java else LoginActivity::class.java
            startActivity(Intent(this@SplashActivity, dest).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK })
        }
    }
}
