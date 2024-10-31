package com.compose.myimageclassification.presentation.result

import android.content.Context
import android.net.Uri

sealed class ResultEvent {
    class AnalyzeImage(val context: Context, val uri: Uri) : ResultEvent()
    data object Translate : ResultEvent()
}
