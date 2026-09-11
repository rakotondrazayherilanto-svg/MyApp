package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.MainAppScreen
import com.example.ui.SchoolViewModel
import com.example.ui.SchoolViewModelFactory
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  private val schoolViewModel: SchoolViewModel by viewModels {
    val app = application as LP3FApplication
    SchoolViewModelFactory(app.repository, app.settingsManager, app.licenseManager)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val settings by schoolViewModel.appSettings.collectAsState()
      MyApplicationTheme(
        themeMode = settings.themeMode,
        colorTheme = settings.colorTheme,
        fontScale = settings.fontScale
      ) {
        Surface(modifier = Modifier.fillMaxSize()) {
          MainAppScreen(viewModel = schoolViewModel)
        }
      }
    }
  }
}
