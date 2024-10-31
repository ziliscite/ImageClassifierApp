package com.compose.myimageclassification.presentation.result

data class ResultState(
    var isLoading: Boolean = true,
    var analyzeResponse: String = "",
)
