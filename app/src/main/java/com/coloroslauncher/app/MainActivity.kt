package com.coloroslauncher.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.coloroslauncher.core.fold.FoldStateManager
import com.coloroslauncher.core.ui.AppDrawerScreen
import com.coloroslauncher.core.ui.HomeScreen
import com.coloroslauncher.core.viewmodel.AppDrawerViewModel
import com.coloroslauncher.core.viewmodel.HomeViewModel
import com.coloroslauncher.theme.ColorOSTheme

private const val ROUTE_HOME = "home"
private const val ROUTE_DRAWER = "drawer"
private const val SWIPE_UP_THRESHOLD_PX = -280f

class MainActivity : ComponentActivity() {

    private val foldStateManager by lazy { FoldStateManager(this) }

    private val serviceLocator by lazy { (application as LauncherApplication).serviceLocator }

    private val homeViewModel: HomeViewModel by viewModels {
        HomeViewModel.Factory(
            appRepository = serviceLocator.appRepository,
            layoutRepository = serviceLocator.layoutRepository,
            wallpaperRepository = serviceLocator.wallpaperRepository,
            settingsRepository = serviceLocator.settingsRepository,
            foldStateFlow = foldStateManager.foldStateFlow(),
        )
    }

    private val appDrawerViewModel: AppDrawerViewModel by viewModels {
        AppDrawerViewModel.Factory(serviceLocator.appRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Home apps must never pop back to whatever launched them; back press is a no-op on Home.
        onBackPressedDispatcher.addCallback(this) { /* intentionally absorbed */ }

        setContent {
            val homeUiState by homeViewModel.uiState.collectAsStateWithLifecycle()
            ColorOSTheme(themeMode = homeUiState.themeMode) {
                val navController = rememberNavController()
                val dragAccumulator = remember { mutableFloatStateOf(0f) }

                NavHost(navController = navController, startDestination = ROUTE_HOME) {
                    composable(
                        ROUTE_HOME,
                        exitTransition = { slideOutVertically { -it } },
                        popEnterTransition = { slideInVertically { -it } },
                    ) {
                        HomeScreen(
                            viewModel = homeViewModel,
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(Unit) {
                                    detectVerticalDragGestures(
                                        onDragStart = { dragAccumulator.floatValue = 0f },
                                        onVerticalDrag = { change, dragAmount ->
                                            change.consume()
                                            dragAccumulator.floatValue += dragAmount
                                        },
                                        onDragEnd = {
                                            if (dragAccumulator.floatValue < SWIPE_UP_THRESHOLD_PX) {
                                                navController.navigate(ROUTE_DRAWER)
                                            }
                                        },
                                    )
                                },
                        )
                    }
                    composable(
                        ROUTE_DRAWER,
                        enterTransition = { slideInVertically { it } },
                        popExitTransition = { slideOutVertically { it } },
                    ) {
                        val drawerState by appDrawerViewModel.uiState.collectAsState()
                        AppDrawerScreen(
                            uiState = drawerState,
                            columns = homeUiState.gridDimensions.columns,
                            onQueryChange = appDrawerViewModel::onQueryChange,
                            onLaunch = { app ->
                                appDrawerViewModel.launchApp(app)
                                navController.popBackStack()
                            },
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        serviceLocator.widgetHostManager.startListening()
    }

    override fun onStop() {
        serviceLocator.widgetHostManager.stopListening()
        super.onStop()
    }
}
