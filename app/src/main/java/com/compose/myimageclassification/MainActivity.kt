package com.compose.myimageclassification

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.compose.myimageclassification.presentation.camera.CameraScreen
import com.compose.myimageclassification.presentation.main.MainEvent
import com.compose.myimageclassification.presentation.main.MainScreen
import com.compose.myimageclassification.presentation.main.MainViewModel
import com.compose.myimageclassification.presentation.result.ResultScreen
import com.compose.myimageclassification.presentation.result.ResultViewModel
import com.compose.myimageclassification.ui.theme.MyImageClassificationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyImageClassificationTheme {
                val mainViewModel = MainViewModel()
                val resultViewModel = ResultViewModel()

                MainContent(mainViewModel, resultViewModel)
            }
        }
    }
}

sealed class Screen {
    data object Main : Screen()
    data object Camera : Screen()
    data class Result(val uri: Uri) : Screen()
}

@Composable
fun MainContent(
    homeViewModel: MainViewModel,
    resultViewModel: ResultViewModel
) {
    val context = LocalContext.current

    var currentScreen by remember {
        mutableStateOf<Screen>(Screen.Main)
    }

    val mainState by homeViewModel.classification.collectAsState()
    val sideEffect by homeViewModel.sideEffect.collectAsState()

    val resultState by resultViewModel.classification.collectAsState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .windowInsetsPadding(insets = WindowInsets.safeDrawing)
    ) { innerPadding ->
        sideEffect?.let {
            LaunchedEffect(it) {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                homeViewModel.onEvent(MainEvent.RemoveSideEffect)
            }
        }

        when (currentScreen) {
            is Screen.Main -> {
                MainScreen(
                    Modifier.padding(innerPadding),
                    mainState,
                    homeViewModel::onEvent,
                    onNavigateToCamera = {
                        currentScreen = Screen.Camera
                    }
                ) { uri ->
                    currentScreen = Screen.Result(uri)
                }
            }
            is Screen.Result -> {
                val result = currentScreen as Screen.Result
                ResultScreen(
                    result.uri,
                    resultState,
                    resultViewModel::onEvent
                ) {
                    currentScreen = Screen.Main
                }
            }
            is Screen.Camera -> {
                CameraScreen {
                    currentScreen = Screen.Main
                }
            }
        }
    }
}
