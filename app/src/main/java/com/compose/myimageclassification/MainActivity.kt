package com.compose.myimageclassification

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.compose.myimageclassification.presentation.main.MainEvent
import com.compose.myimageclassification.presentation.main.MainScreen
import com.compose.myimageclassification.presentation.main.MainViewModel
import com.compose.myimageclassification.ui.theme.MyImageClassificationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyImageClassificationTheme {
                val viewModel = MainViewModel()
                MainContent(viewModel)
            }
        }
    }
}

@Composable
fun MainContent(viewModel: MainViewModel) {
    val context = LocalContext.current
    val state by viewModel.classification.collectAsState()
    val sideEffect by viewModel.sideEffect.collectAsState()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        sideEffect?.let {
            LaunchedEffect(it) {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                viewModel.onEvent(MainEvent.RemoveSideEffect)
            }
        }

        MainScreen(
            modifier = Modifier.padding(innerPadding),
            state = state,
            onEvent = viewModel::onEvent
        )
    }
}
