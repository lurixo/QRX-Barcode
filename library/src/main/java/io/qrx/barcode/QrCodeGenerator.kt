package io.qrx.barcode

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import io.qrx.barcode.qrcode.InternalErrorCorrectionLevel
import io.qrx.barcode.qrcode.QRCode
import java.io.ByteArrayOutputStream

object QrCodeGenerator {

    @JvmStatic
    @JvmOverloads
    fun generateBitmap(
        content: String,
        size: Int = 512,
        errorCorrectionLevel: ErrorCorrectionLevel = ErrorCorrectionLevel.High,
        darkColor: Int = Color.BLACK,
        lightColor: Int = Color.WHITE
    ): Bitmap {
        val internalLevel = when (errorCorrectionLevel) {
            ErrorCorrectionLevel.Low -> InternalErrorCorrectionLevel.L
            ErrorCorrectionLevel.Medium -> InternalErrorCorrectionLevel.M
            ErrorCorrectionLevel.MediumHigh -> InternalErrorCorrectionLevel.Q
            ErrorCorrectionLevel.High -> InternalErrorCorrectionLevel.H
        }

        val matrix = QRCode(content, internalLevel).encode()
        val moduleCount = matrix.size

        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply { isAntiAlias = false; style = Paint.Style.FILL }

        paint.color = lightColor
        canvas.drawRect(0f, 0f, size.toFloat(), size.toFloat(), paint)

        val moduleSize = size.toFloat() / moduleCount
        paint.color = darkColor
        for (row in 0 until moduleCount) {
            for (col in 0 until moduleCount) {
                if (matrix[row][col]) {
                    val left = col * moduleSize
                    val top = row * moduleSize
                    canvas.drawRect(left, top, left + moduleSize, top + moduleSize, paint)
                }
            }
        }

        return bitmap
    }

    @JvmStatic
    @JvmOverloads
    fun generatePng(
        content: String,
        size: Int = 512,
        errorCorrectionLevel: ErrorCorrectionLevel = ErrorCorrectionLevel.High,
        quality: Int = 100,
        darkColor: Int = Color.BLACK,
        lightColor: Int = Color.WHITE
    ): ByteArray {
        val bitmap = generateBitmap(content, size, errorCorrectionLevel, darkColor, lightColor)
        return bitmap.toByteArray(Bitmap.CompressFormat.PNG, quality)
    }

    @JvmStatic
    @JvmOverloads
    fun generateJpeg(
        content: String,
        size: Int = 512,
        errorCorrectionLevel: ErrorCorrectionLevel = ErrorCorrectionLevel.High,
        quality: Int = 90,
        darkColor: Int = Color.BLACK,
        lightColor: Int = Color.WHITE
    ): ByteArray {
        val bitmap = generateBitmap(content, size, errorCorrectionLevel, darkColor, lightColor)
        return bitmap.toByteArray(Bitmap.CompressFormat.JPEG, quality)
    }

    @JvmStatic
    @JvmOverloads
    fun generateMatrix(
        content: String,
        errorCorrectionLevel: ErrorCorrectionLevel = ErrorCorrectionLevel.High
    ): Array<BooleanArray> {
        val internalLevel = when (errorCorrectionLevel) {
            ErrorCorrectionLevel.Low -> InternalErrorCorrectionLevel.L
            ErrorCorrectionLevel.Medium -> InternalErrorCorrectionLevel.M
            ErrorCorrectionLevel.MediumHigh -> InternalErrorCorrectionLevel.Q
            ErrorCorrectionLevel.High -> InternalErrorCorrectionLevel.H
        }
        return QRCode(content, internalLevel).encode()
    }
}

private fun Bitmap.toByteArray(format: Bitmap.CompressFormat, quality: Int): ByteArray {
    val stream = ByteArrayOutputStream()
    compress(format, quality, stream)
    return stream.toByteArray()
}
