package com.compose.myimageclassification.presentation.main

import android.Manifest
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.compose.myimageclassification.R
import com.compose.myimageclassification.presentation.common.PermissionHandler
import com.compose.myimageclassification.presentation.common.ImageFromLocalUri
import com.compose.myimageclassification.utils.getImageUri
import com.compose.myimageclassification.presentation.common.launchGallery
import com.compose.myimageclassification.presentation.common.launchCamera


@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    state: MainState,
    onEvent: (MainEvent) -> Unit,
    onNavigateToCamera: (Boolean) -> Unit,
    onNavigateToResult: (Uri) -> Unit
) {
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(false)
    }

    PermissionHandler(
        permission = Manifest.permission.CAMERA
    ) { isGranted ->
        hasPermission = isGranted
    }

    val cameraLauncher = launchCamera {
        onEvent(MainEvent.ShowImage(true))
    }

    val galleryLauncher = launchGallery(context) { uri ->
        onEvent(MainEvent.GetUri(uri))
        onEvent(MainEvent.ShowImage(true))
    }

    if (state.isLoading) {
        // Progress Bar
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(8.dp)
        ) {
            if (state.showImage) {
                ImageFromLocalUri(uri = state.imageUri)
            } else {
                ImageFromLocalUri(uri = null)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    onEvent(MainEvent.ShowImage(false))
                    galleryLauncher.invoke()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(text = stringResource(id = R.string.gallery))
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    onEvent(MainEvent.ShowImage(false))

                    val newUri = getImageUri(context)
                    onEvent(MainEvent.GetUri(newUri))

                    cameraLauncher.invoke(newUri)
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(text = stringResource(id = R.string.camera))
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    onNavigateToCamera(true)
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(text = stringResource(id = R.string.classify_image))
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    onNavigateToCamera(false)
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(text = stringResource(id = R.string.detect_object))
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Upload Button
            Button(
                onClick = {
                    onEvent(MainEvent.UploadImage(context, state.imageUri))
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(text = stringResource(id = R.string.upload))
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Analyze Button
            Button(
                onClick = {
                    state.imageUri?.let {
                        onNavigateToResult(it)
                    } ?: onEvent(MainEvent.AddSideEffect("Please select an image"))
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(text = stringResource(id = R.string.analyze))
            }
        }

        Spacer(modifier = Modifier.height(3.dp))

        // Result TextView
        Text(
            text = state.classificationResponse,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .fillMaxWidth()
        )
    }
}
