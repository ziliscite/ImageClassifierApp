package com.compose.myimageclassification.presentation.camera

import android.app.Activity
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import java.util.concurrent.Executors
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.LocalLifecycleOwner
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.compose.myimageclassification.domain.ml.ImageClassifierHelper
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.text.NumberFormat

@Composable
fun CameraScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    var resultText by remember { mutableStateOf("") }
    var inferenceTimeText by remember { mutableStateOf("") }

    val imageClassifierHelper = remember {
        ImageClassifierHelper(
            context = context,
            classifierListener = object : ImageClassifierHelper.ClassifierListener {
                override fun onError(error: String) {
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                }

                override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
                    results?.let { classifications ->
                        if (classifications.isNotEmpty() && classifications[0].categories.isNotEmpty()) {
                            val sortedCategories = classifications[0].categories.sortedByDescending { it.score }
                            val displayResult = sortedCategories.joinToString("\n") {
                                "${it.label} " + NumberFormat.getPercentInstance().format(it.score).trim()
                            }
                            resultText = displayResult
                            inferenceTimeText = "$inferenceTime ms"
                        } else {
                            resultText = ""
                            inferenceTimeText = ""
                        }
                    }
                }
            }
        )
    }

    BackHandler {
        onNavigateBack()
    }

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val executor = ContextCompat.getMainExecutor(ctx)

                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.surfaceProvider = previewView.surfaceProvider
                    }

                    val resolutionSelector = ResolutionSelector.Builder()
                        .setAspectRatioStrategy(AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY)
                        .build()

                    val imageAnalyzer = ImageAnalysis.Builder()
                        .setResolutionSelector(resolutionSelector)
                        .setTargetRotation(previewView.display.rotation)
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                        .build()

                    imageAnalyzer.setAnalyzer(Executors.newSingleThreadExecutor()) { image ->
                        imageClassifierHelper.classifyImage(image)
                    }

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            preview,
                            imageAnalyzer
                        )
                    } catch (exc: Exception) {
                        Toast.makeText(ctx, "Failed to open camera.", Toast.LENGTH_SHORT).show()
                        Log.e("CameraScreen", "startCamera: ${exc.message}")
                    }
                }, executor)

                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        Text(
            text = resultText,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xBFFFFFFF))
                .padding(16.dp)
                .align(Alignment.BottomCenter),
            fontSize = 20.sp,
            color = Color.Black,
            maxLines = 3
        )

        Text(
            text = inferenceTimeText,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopCenter),
            fontSize = 20.sp,
            color = Color.White,
            textAlign = TextAlign.End
        )
    }

    LaunchedEffect(Unit) {
        hideSystemUI(context)
    }
}

private fun hideSystemUI(context: Context) {
    val window = (context as? Activity)?.window ?: return
    WindowCompat.setDecorFitsSystemWindows(window, false)
    WindowInsetsControllerCompat(window, window.decorView).let { controller ->
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
fun CameraScreenPreview() {
    CameraScreen() {}
}
