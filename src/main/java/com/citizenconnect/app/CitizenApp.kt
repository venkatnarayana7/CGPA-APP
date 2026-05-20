package com.citizenconnect.app
import android.app.Application
import com.citizenconnect.app.model.AppDatabase
import com.citizenconnect.app.utils.SessionManager

class CitizenApp : Application() {
    val database by lazy { AppDatabase.getInstance(this) }
    
    override fun onCreate() {
        super.onCreate()
        SessionManager.applyLanguage(this)
    }
}
