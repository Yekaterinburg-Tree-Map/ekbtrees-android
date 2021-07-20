package ru.ekbtrees.treemap.ui.utils

import android.graphics.*

/**
 * Класс для рисования круга
 * @param circleColor Цвет
 * @param width Ширина
 * @param height Высота
 * */
class ClusterIconDrawer(
    private val circleColor: Int,
    private val width: Int,
    private val height: Int = width
) {
    fun draw(textToDraw: String): Bitmap {
        val radius = if (width > height) {
            height / 4f
        } else {
            width / 4f
        }
        val centerX = width / 2f
        val centerY = height / 2f
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint()
        paint.apply {
            isAntiAlias = true
            color = circleColor
        }
        canvas.drawCircle(centerX, centerY, radius, paint)

        paint.apply {
            color = Color.WHITE
            textSize = 100f
        }
        paint.getTextBounds(textToDraw, 0, textToDraw.length, Rect())
        canvas.drawText(textToDraw, centerX - 20, centerY + 20, paint)

        return bitmap
    }
}