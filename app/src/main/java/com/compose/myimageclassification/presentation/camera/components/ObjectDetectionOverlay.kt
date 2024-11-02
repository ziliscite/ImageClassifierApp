package com.compose.myimageclassification.presentation.camera.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.compose.myimageclassification.R
import org.tensorflow.lite.task.gms.vision.detector.Detection
import kotlin.math.max
import java.text.NumberFormat

@Composable
fun ObjectDetectionOverlay(
    results: List<Detection>,
    imageWidth: Int,
    imageHeight: Int,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val context = LocalContext.current

    Canvas(modifier = modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        val scaleFactor = max(canvasWidth / imageWidth, canvasHeight / imageHeight)

        val boxColor = Color(ContextCompat.getColor(context, R.color.bounding_box_color))
        val boxStrokeWidth = 8f

        val textSize = with(density) { 16.sp.toPx() }

        for (result in results) {
            val boundingBox = result.boundingBox

            val left = boundingBox.left * scaleFactor
            val top = boundingBox.top * scaleFactor
            val right = boundingBox.right * scaleFactor
            val bottom = boundingBox.bottom * scaleFactor

            // Draw bounding box
            drawRect(
                color = boxColor,
                topLeft = Offset(left, top),
                size = Size(right - left, bottom - top),
                style = Stroke(width = boxStrokeWidth)
            )

            // Prepare text
            val drawableText = "${result.categories[0].label} " +
                    NumberFormat.getPercentInstance().format(result.categories[0].score)

            val textPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.WHITE
                textAlign = android.graphics.Paint.Align.LEFT
                setTextSize(textSize)
            }

            val textBounds = android.graphics.Rect()
            textPaint.getTextBounds(drawableText, 0, drawableText.length, textBounds)

            val textWidth = textBounds.width().toFloat()
            val textHeight = textBounds.height().toFloat()
            val padding = 8f

            // Draw text background
            drawRect(
                color = Color.Black,
                topLeft = Offset(left, top),
                size = Size(textWidth + 2 * padding, textHeight + 2 * padding)
            )

            // Draw text
            drawContext.canvas.nativeCanvas.drawText(
                drawableText,
                left + padding,
                top + textHeight + padding,
                textPaint
            )
        }
    }
}