package com.compose.myimageclassification.presentation.result

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
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

            ResultEvent.Translate -> {
                _classification.value = classification.value.copy(isProgressLoading = true)
                translateResponse()
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
                        analyzeResponse = "Error: no text detected"
                    )
                }
                _classification.value = classification.value.copy(isLoading = false)
            }
            .addOnFailureListener { e ->
                _classification.value = classification.value.copy(
                    analyzeResponse = "Error: ${e.message.toString()}"
                )
                _classification.value = classification.value.copy(isLoading = false)
            }
    }

    private fun translateResponse() {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.INDONESIAN)
            .build()
        val indonesianEnglishTranslator = Translation.getClient(options)
//        val conditions = DownloadConditions.Builder()
//            .requireWifi()
//            .build()

        indonesianEnglishTranslator.downloadModelIfNeeded()
            .addOnSuccessListener {
                indonesianEnglishTranslator.translate(classification.value.analyzeResponse)
                    .addOnSuccessListener { translatedText ->
                        _classification.value = classification.value.copy(
                            translatedResponse = translatedText
                        )
                        indonesianEnglishTranslator.close()
                        _classification.value = classification.value.copy(isProgressLoading = false)
                    }
                    .addOnFailureListener { e ->
                        _classification.value = classification.value.copy(
                            translatedResponse = "Error: ${e.message.toString()}"
                        )
                        indonesianEnglishTranslator.close()
                        _classification.value = classification.value.copy(isProgressLoading = false)
                    }
            }
            .addOnFailureListener { e ->
                _classification.value = classification.value.copy(
                    translatedResponse = "Error: ${e.message.toString()}"
                )
                _classification.value = classification.value.copy(isProgressLoading = false)
            }
    }
}
