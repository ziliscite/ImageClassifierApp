package com.compose.myimageclassification.presentation.camera

import android.content.Context
import androidx.camera.core.ImageProxy
import com.compose.myimageclassification.domain.ml.ClassifierListener
import com.compose.myimageclassification.domain.ml.DetectorListener
import com.compose.myimageclassification.domain.ml.ImageClassifierHelper
import com.compose.myimageclassification.domain.ml.ObjectDetectorHelper
import org.tensorflow.lite.task.gms.vision.classifier.Classifications
import org.tensorflow.lite.task.gms.vision.detector.Detection

interface MLHelper {
    fun processImage(image: ImageProxy)
}

class ImageClassifierWrapper(
    private val helper: ImageClassifierHelper
) : MLHelper {
    override fun processImage(image: ImageProxy) {
        helper.classifyImage(image)
    }
}

class ObjectDetectorWrapper(
    private val helper: ObjectDetectorHelper
) : MLHelper {
    override fun processImage(image: ImageProxy) {
        helper.detectObject(image)
    }
}

// Factory function for ImageClassifierHelper
fun createImageClassifierHelper(
    context: Context,
    onError: (String) -> Unit,
    onResults: (List<Classifications>, Long) -> Unit
): ImageClassifierHelper {
    return ImageClassifierHelper(
        context = context,
        classifierListener = object : ClassifierListener {
            override fun onError(error: String) {
                onError(error)
            }

            override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
                results?.let { classifications ->
                    onResults(classifications, inferenceTime)
                }
            }
        }
    )
}

// Factory function for ObjectDetectorHelper
fun createObjectDetectorHelper(
    context: Context,
    onError: (String) -> Unit,
    onResults: (List<Detection>, Long, Int, Int) -> Unit
): ObjectDetectorHelper {
    return ObjectDetectorHelper(
        context = context,
        detectorListener = object : DetectorListener {
            override fun onError(error: String) {
                onError(error)
            }

            override fun onResults(
                results: MutableList<Detection>?,
                inferenceTime: Long,
                imageHeight: Int,
                imageWidth: Int
            ) {
                results?.let {
                    onResults(it, inferenceTime, imageHeight, imageWidth)
                }
            }
        }
    )
}
