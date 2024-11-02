package com.compose.myimageclassification.domain.ml

import android.content.Context
import android.graphics.Bitmap
import androidx.camera.core.ImageProxy
import com.google.android.gms.tflite.client.TfLiteInitializationOptions
import com.google.android.gms.tflite.gpu.support.TfLiteGpu
import org.tensorflow.lite.task.gms.vision.TfLiteVision

fun initializeTfLite(context: Context, onSuccess: () -> Unit, onError: () -> Unit) {
    TfLiteGpu.isGpuDelegateAvailable(context).onSuccessTask { gpuAvailable ->
        val optionsBuilder = TfLiteInitializationOptions.builder()
        if (gpuAvailable) {
            optionsBuilder.setEnableGpuDelegateSupport(true)
        }
        TfLiteVision.initialize(context, optionsBuilder.build())
    }.addOnSuccessListener {
        onSuccess()
    }.addOnFailureListener {
        onError()
    }
}

fun toBitmap(image: ImageProxy): Bitmap {
    val bitmapBuffer = Bitmap.createBitmap(
        image.width,
        image.height,
        Bitmap.Config.ARGB_8888
    )
    image.use { bitmapBuffer.copyPixelsFromBuffer(image.planes[0].buffer) }
    image.close()
    return bitmapBuffer
}