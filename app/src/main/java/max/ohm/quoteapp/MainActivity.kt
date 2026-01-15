package max.ohm.quoteapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import max.ohm.quoteapp.presentation.navigation.MainNavigation
import max.ohm.quoteapp.presentation.navigation.Screen
import max.ohm.quoteapp.presentation.settings.SettingsViewModel
import max.ohm.quoteapp.ui.theme.QuoteAppTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var supabaseClient: SupabaseClient
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val settings by settingsViewModel.settings.collectAsState()
            
            // Track authentication state
            var isLoading by remember { mutableStateOf(true) }
            var isAuthenticated by remember { mutableStateOf(false) }
            
            // Check authentication status on app start
            LaunchedEffect(Unit) {
                // Wait for session to be restored from storage
                val sessionStatus = supabaseClient.auth.sessionStatus.first { status ->
                    status !is SessionStatus.Initializing
                }
                isAuthenticated = sessionStatus is SessionStatus.Authenticated
                isLoading = false
            }
            
            QuoteAppTheme(
                themeMode = settings.themeMode,
                accentColor = settings.accentColor
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Show loading indicator while checking auth status
                    AnimatedVisibility(
                        visible = isLoading,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.background),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    
                    // Show main navigation after auth check
                    AnimatedVisibility(
                        visible = !isLoading,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        MainNavigation(
                            startDestination = if (isAuthenticated) Screen.Home else Screen.Login
                        )
                    }
                }
            }
        }
    }
}