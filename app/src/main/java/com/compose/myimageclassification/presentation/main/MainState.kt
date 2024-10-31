package com.compose.myimageclassification.presentation.main

import android.net.Uri

data class MainState(
    var isLoading: Boolean = false,
    var response: String = "",
    var imageUri: Uri? = null,
    var showImage: Boolean = false
)