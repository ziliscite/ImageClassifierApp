package com.compose.myimageclassification.presentation.main

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.compose.myimageclassification.data.remote.retrofit.ApiConfig
import com.compose.myimageclassification.utils.reduceFileImage
import com.compose.myimageclassification.utils.uriToFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.util.Locale

class MainViewModel : ViewModel() {
    private val apiService = ApiConfig.getApiService()

    private val _classification = MutableStateFlow(MainState())
    val classification: StateFlow<MainState> = _classification.asStateFlow()

    private val _sideEffect = MutableStateFlow<String?>(null)
    val sideEffect: StateFlow<String?> = _sideEffect.asStateFlow()

    fun onEvent(event: MainEvent) {
        when (event) {
            is MainEvent.UploadImage -> {
                uploadImage(event.context, event.currentImageUri)
            }
            is MainEvent.GetUri -> {
                _classification.update { it.copy(imageUri = event.uri) }
            }
            is MainEvent.ShowLoading -> {
                _classification.update { it.copy(isLoading = event.showLoading) }
            }
            is MainEvent.ShowImage -> {
                _classification.update { it.copy(showImage = event.showImage) }
            }
            is MainEvent.AddSideEffect -> {
                _sideEffect.value = event.sideEffect
            }
            is MainEvent.RemoveSideEffect -> {
                _sideEffect.value = null
            }
        }
    }

    private fun uploadImage(context: Context, currentImageUri: Uri?) {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, context).reduceFileImage()

            _classification.update { it.copy(isLoading = true) }

            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )

            getResponse(multipartBody)
        } ?: run {
            _sideEffect.value = "Empty image"
        }
    }

    private fun getResponse(multipartBody: MultipartBody.Part) {
        viewModelScope.launch {
            try {
                val successResponse = apiService.uploadImage(multipartBody)
                with(successResponse.data) {
                    _classification.update { state ->
                        state.copy(
                            classificationResponse = if (isAboveThreshold == true) {
                                _sideEffect.value = successResponse.message.toString()
                                String.format(locale = Locale.US, "Model is predicted successfully and the confidence score is %.2f%%", confidenceScore)
                            } else {
                                _sideEffect.value = "Model is predicted successfully but under threshold."
                                String.format(locale = Locale.US,"Please use the correct picture because the confidence score is %.2f%%", confidenceScore)
                            },
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _sideEffect.value = "Failed to upload image"
                _classification.update { it.copy(isLoading = false) }
            }
        }
    }
}