package com.compose.myimageclassification.presentation.main

import android.content.Context
import android.net.Uri

sealed class MainEvent {
    class UploadImage(val context: Context, val currentImageUri: Uri?) : MainEvent()
    class ShowImage(val showImage: Boolean) : MainEvent()
    class ShowLoading(val showLoading: Boolean) : MainEvent()
    class GetUri(val uri: Uri?) : MainEvent()
    data class AddSideEffect(val sideEffect: String) : MainEvent()
    data object RemoveSideEffect: MainEvent()
}
