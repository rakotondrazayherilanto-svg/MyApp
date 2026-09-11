package com.example

import android.app.Application
import com.example.data.AppDatabase
import com.example.data.SchoolRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LP3FApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    val database by lazy { AppDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { SchoolRepository(database.schoolDao()) }
    val settingsManager by lazy { com.example.data.AppSettingsManager(this) }
    val licenseManager by lazy { com.example.utils.ProductLicenseManager(this) }

    override fun onCreate() {
        super.onCreate()
        applicationScope.launch {
            try {
                val existingSubjects = repository.allSubjects.first()
                if (existingSubjects.isEmpty()) {
                    repository.seedIfEmpty()
                }
            } catch (e: Exception) {
                // Ignore
            }
        }
    }
}
