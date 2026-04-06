package com.example.gemmachat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gemmachat.data.download.HfDownloadRepository
import com.example.gemmachat.ui.chat.ChatScreen
import com.example.gemmachat.ui.chat.ChatViewModel
import com.example.gemmachat.ui.onboarding.OnboardingScreen
import com.example.gemmachat.ui.onboarding.OnboardingViewModel
import com.example.gemmachat.ui.settings.SettingsScreen
import com.example.gemmachat.ui.settings.SettingsViewModel
import com.example.gemmachat.ui.theme.GemmaChatTheme

private object Routes {
    const val ONBOARDING = "onboarding"
    const val CHAT = "chat"
    const val SETTINGS = "settings"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GemmaChatTheme {
                GemmaChatNavHost()
            }
        }
    }
}

@Composable
private fun GemmaChatNavHost() {
    val context = LocalContext.current
    val start =
        if (HfDownloadRepository.modelFile(context).exists()) Routes.CHAT else Routes.ONBOARDING
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = start) {
        composable(Routes.ONBOARDING) {
            val vm: OnboardingViewModel = viewModel(
                factory = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.getInstance(
                    context.applicationContext as android.app.Application,
                ),
            )
            OnboardingScreen(
                viewModel = vm,
                onFinished = {
                    navController.navigate(Routes.CHAT) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }
        composable(Routes.CHAT) {
            val vm: ChatViewModel = viewModel(
                factory = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.getInstance(
                    context.applicationContext as android.app.Application,
                ),
            )
            ChatScreen(
                viewModel = vm,
                onOpenSettings = { navController.navigate(Routes.SETTINGS) },
                onNeedModel = {
                    navController.navigate(Routes.ONBOARDING) {
                        popUpTo(Routes.CHAT) { inclusive = true }
                    }
                },
            )
        }
        composable(Routes.SETTINGS) {
            val vm: SettingsViewModel = viewModel(
                factory = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.getInstance(
                    context.applicationContext as android.app.Application,
                ),
            )
            SettingsScreen(
                viewModel = vm,
                onBack = { navController.popBackStack() },
            )
        }
    }
}
