package com.jayesh.jnotes

import android.os.Bundle
import android.util.Log
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.jayesh.jnotes.ui.JnotesApp
import com.jayesh.jnotes.ui.theme.JnotesTheme
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            // Update the system bars to be translucent
            val systemUiController = rememberSystemUiController()
            val useDarkIcons = MaterialTheme.colors.isLight

            SideEffect {
                Log.e(TAG, "onCreate: SideEffect called")
                systemUiController.setSystemBarsColor(color = Color.Transparent, isNavigationBarContrastEnforced = false, darkIcons = useDarkIcons)
                changeSystemBarsIconsColor(window, useDarkIcons)
            }

            JnotesTheme {
                ProvideWindowInsets {
                    JnotesApp()
                }
            }
        }
    }

    private fun changeStatusBarIconsColor(window: Window, darkIcons: Boolean) {
        val statusBar = WindowCompat.getInsetsController(window, window.decorView)
        statusBar?.isAppearanceLightStatusBars = darkIcons
    }

    private fun changeSystemBarsIconsColor(window: Window, darkIcons: Boolean) {
        val windowController = ViewCompat.getWindowInsetsController(window.decorView)
        windowController?.isAppearanceLightStatusBars = darkIcons
        windowController?.isAppearanceLightNavigationBars = darkIcons
    }
}