package com.compose.myimageclassification.domain.ml

import org.tensorflow.lite.task.gms.vision.classifier.Classifications

interface ClassifierListener {
    fun onError(error: String)
    fun onResults(
        results: List<Classifications>?,
        inferenceTime: Long
    )
}
