package ru.ekbtrees.treemap.ui.draw

import android.graphics.*

/**
 * @param circleColor
 * */
class ClusterIconDrawer(
    private val circleColor: Int,
    private val width: Int = 100,
    private val height: Int = 100
) {
    fun draw(textToDraw: String): Bitmap {
        val radius = if (width > height) {
            height / 4f
        } else {
            width / 4f
        }
        val centerX = width / 2f
        val centerY = height / 2f
        val bitmap =
            Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply {
            color = circleColor
        }
        paint.apply {
            isAntiAlias = true
            color = circleColor
        }
        canvas.drawCircle(centerX, centerY, radius, paint)

        paint.apply {
            color = Color.BLACK
            textSize = 100f
        }
        paint.getTextBounds(textToDraw, 0, textToDraw.length, Rect())
        canvas.drawText(textToDraw, centerX - 20, centerY + 20, paint)

        return bitmap
    }
}