package com.compose.myimageclassification.presentation.result

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ResultViewModel : ViewModel() {
    private val _classification = MutableStateFlow(ResultState())
    val classification: StateFlow<ResultState> = _classification.asStateFlow()

    fun onEvent(event: ResultEvent) {
        when (event) {
            is ResultEvent.AnalyzeImage -> {
                _classification.value = classification.value.copy(isLoading = true)
                analyzeImage(event.context, event.uri)
            }
        }
    }

    private fun analyzeImage(context: Context, uri: Uri) {
        val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val inputImage = InputImage.fromFilePath(context, uri)
        textRecognizer.process(inputImage)
            .addOnSuccessListener { text ->
                val detectedText = text.text
                if (detectedText.isNotBlank()) {
                    _classification.value = classification.value.copy(
                        analyzeResponse = text.text
                    )
                } else {
                    _classification.value = classification.value.copy(
                        analyzeResponse = "No text detected"
                    )
                }
                _classification.value = classification.value.copy(isLoading = false)
            }
            .addOnFailureListener { e ->
                _classification.value = classification.value.copy(
                    analyzeResponse = e.message.toString()
                )
                _classification.value = classification.value.copy(isLoading = false)
            }
    }
}