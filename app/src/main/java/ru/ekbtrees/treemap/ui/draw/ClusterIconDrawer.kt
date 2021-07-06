package ru.ekbtrees.treemap.ui.draw

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

/**
 * @param radius
 * @param circleColor
 * @param canvasColor
 * */
class ClusterIconDrawer(
    private val radius: Float,
    private val circleColor: Int,
    private val canvasColor: Int
) {
    fun draw(textToDraw: String): Bitmap? {
        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap).apply {
            drawColor(canvasColor)
        }
        val paint = Paint().apply {
            color = circleColor
        }
        val textPaint = Paint().apply {
            color = Color.BLACK
            textAlign = Paint.Align.CENTER
            textSize = 12f
        }
        val textHeight = textPaint.descent() - textPaint.ascent()
        val textOffset = (textHeight / 2) - textPaint.descent()
        val cx = canvas.width / 2F + 250
        val cy = canvas.height / 2F + 35
        canvas.drawCircle(
            cx, cy, radius, paint
        )
        val margin = 20f
        var text = textToDraw
        val numOfChairs = textPaint.breakText(
            text,
            true,
            radius * 2 - margin,
            null
        )
        if (text.length > numOfChairs) {
            text = text.substring(0, numOfChairs) + "..."
        }
        canvas.drawText(text, cx, cy + textOffset, textPaint)

        return bitmap
    }
}