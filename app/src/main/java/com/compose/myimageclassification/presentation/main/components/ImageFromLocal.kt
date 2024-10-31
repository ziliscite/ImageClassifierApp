package com.compose.myimageclassification.presentation.main.components

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Scale
import com.compose.myimageclassification.R

@Composable
fun ImageFromLocalUri(modifier: Modifier = Modifier, uri: Uri? = null) {
    Image(
        painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(uri)
                .placeholder(R.drawable.ic_place_holder)
                .error(R.drawable.ic_place_holder)
                .crossfade(true)
                .scale(Scale.FILL)
                .build(),
            contentScale = ContentScale.Crop,
            filterQuality = FilterQuality.High
        ),
        contentDescription = null,
        modifier = modifier
            .fillMaxSize()
            .aspectRatio(1f)
    )
}