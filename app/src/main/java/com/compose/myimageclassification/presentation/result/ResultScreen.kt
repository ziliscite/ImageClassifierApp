package com.compose.myimageclassification.presentation.result

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.compose.myimageclassification.presentation.common.ImageFromLocalUri

@Composable
fun ResultScreen(
    imageUri: Uri,
    resultState: ResultState,
    onEvent: (ResultEvent) -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(imageUri) {
        onEvent(ResultEvent.AnalyzeImage(context, imageUri))
    }

    BackHandler {
        onNavigateBack()
    }

    if (resultState.isProgressLoading) {
        // Progress Bar
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
        )
    }

    if (resultState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)
        ) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f) // This will make the box square-shaped
                    .padding(8.dp)
            ) {
                ImageFromLocalUri(uri = imageUri)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = resultState.analyzeResponse,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (!resultState.analyzeResponse.startsWith("Error")) {
                Button(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp),
                    onClick = { onEvent(ResultEvent.Translate) }
                ) {
                    Text(text = "Translate")
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = resultState.translatedResponse,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }
        }
    }
}
