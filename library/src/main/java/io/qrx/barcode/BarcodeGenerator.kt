package io.qrx.barcode

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import java.io.ByteArrayOutputStream

object BarcodeGenerator {

    @JvmStatic
    @JvmOverloads
    fun generateBitmap(
        content: String,
        type: BarcodeType,
        width: Int = 400,
        height: Int = 150,
        barColor: Int = Color.BLACK,
        backgroundColor: Int = Color.WHITE
    ): Bitmap {
        val code = type.encoder.encode(content)

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply { isAntiAlias = false; style = Paint.Style.FILL }

        paint.color = backgroundColor
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

        val barWidth = width.toFloat() / code.size
        paint.color = barColor
        for (i in code.indices) {
            if (code[i]) {
                val left = i * barWidth
                canvas.drawRect(left, 0f, left + barWidth, height.toFloat(), paint)
            }
        }

        return bitmap
    }

    @JvmStatic
    @JvmOverloads
    fun generatePng(
        content: String,
        type: BarcodeType,
        width: Int = 400,
        height: Int = 150,
        quality: Int = 100,
        barColor: Int = Color.BLACK,
        backgroundColor: Int = Color.WHITE
    ): ByteArray {
        val bitmap = generateBitmap(content, type, width, height, barColor, backgroundColor)
        return bitmap.toByteArray(Bitmap.CompressFormat.PNG, quality)
    }

    @JvmStatic
    @JvmOverloads
    fun generateJpeg(
        content: String,
        type: BarcodeType,
        width: Int = 400,
        height: Int = 150,
        quality: Int = 90,
        barColor: Int = Color.BLACK,
        backgroundColor: Int = Color.WHITE
    ): ByteArray {
        val bitmap = generateBitmap(content, type, width, height, barColor, backgroundColor)
        return bitmap.toByteArray(Bitmap.CompressFormat.JPEG, quality)
    }

    @JvmStatic
    fun generateRaw(content: String, type: BarcodeType): BooleanArray {
        return type.encoder.encode(content)
    }
}

private fun Bitmap.toByteArray(format: Bitmap.CompressFormat, quality: Int): ByteArray {
    val stream = ByteArrayOutputStream()
    compress(format, quality, stream)
    return stream.toByteArray()
}
