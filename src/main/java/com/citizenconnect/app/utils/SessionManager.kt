package com.citizenconnect.app.utils
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

object SessionManager {
    private fun prefs(ctx: Context) = ctx.getSharedPreferences("cc_session", Context.MODE_PRIVATE)
    
    fun save(ctx: Context, uid: String, name: String, email: String) =
        prefs(ctx).edit().putString("uid",uid).putString("name",name).putString("email",email).apply()
        
    fun uid(ctx: Context): String?  = prefs(ctx).getString("uid", null)
    fun name(ctx: Context): String  = prefs(ctx).getString("name", "Citizen") ?: "Citizen"
    fun email(ctx: Context): String = prefs(ctx).getString("email", "") ?: ""
    fun isLoggedIn(ctx: Context) = uid(ctx) != null
    fun logout(ctx: Context) = prefs(ctx).edit().clear().apply()

    fun setLanguage(lang: String) {
        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(lang)
        AppCompatDelegate.setApplicationLocales(appLocale)
    }
    
    fun getLanguage(): String {
        return AppCompatDelegate.getApplicationLocales()[0]?.language ?: "en"
    }

    // No longer needed with AppCompatDelegate, but kept for compatibility if called elsewhere
    fun applyLanguage(ctx: Context) {}
}
