package com.compose.myimageclassification.presentation.result

data class ResultState(
    var isLoading: Boolean = true,
    var isProgressLoading: Boolean = false,
    var analyzeResponse: String = "",
    var translatedResponse: String = "",
)
