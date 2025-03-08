package io.nebula.xenogithub

import android.app.ComponentCaller
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import io.nebula.xenogithub.biz.KVStorageImpl
import io.nebula.xenogithub.ui.theme.XenoGithubTheme
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dealWithDeeplink(intent)
        enableEdgeToEdge()
        setContent {
            XenoGithubTheme {
                val windowSize = calculateWindowSizeClass(this)
                XenoGithubApp(windowSize)
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent == null) {
            return
        }
        dealWithDeeplink(intent)
    }

    override fun onNewIntent(intent: Intent, caller: ComponentCaller) {
        super.onNewIntent(intent, caller)
        dealWithDeeplink(intent)
    }

    private fun dealWithDeeplink(intent: Intent) {
        intent.data?.let { uri ->
            if (uri.scheme == "xenode" && uri.host == "auth") {
                val code = uri.getQueryParameter("code") ?: ""
                lifecycleScope.launch {
                    KVStorageImpl.saveAccessCode(this@MainActivity, code)
                }
            }
        }
    }
}