package com.compose.myimageclassification.presentation.common

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable

@Composable
fun launchCamera(
    callback: () -> Unit
): (Uri) -> Unit {
    val launcherIntentCamera = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            callback()
        }
    }

    return {
        launcherIntentCamera.launch(it)
    }
}

@Composable
fun launchGallery(
    context: Context,
    callback: (Uri?) -> Unit
): () -> Unit {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            Toast.makeText(context, "Image selected", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "No image selected", Toast.LENGTH_SHORT).show()
        }
        callback(uri)
    }

    return {
        launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
}