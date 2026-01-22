package io.qrx.barcode.qrcode.internals

import io.qrx.barcode.qrcode.QRCodeDataType

internal abstract class QRData(val dataType: QRCodeDataType, val data: String) {
    abstract fun length(): Int
    abstract fun write(buffer: BitBuffer)

    fun getLengthInBits(type: Int): Int = when (type) {
        in 1..9 -> when (dataType) {
            QRCodeDataType.NUMBERS -> 10
            QRCodeDataType.UPPER_ALPHA_NUM -> 9
            QRCodeDataType.DEFAULT -> 8
        }
        in 10..26 -> when (dataType) {
            QRCodeDataType.NUMBERS -> 12
            QRCodeDataType.UPPER_ALPHA_NUM -> 11
            QRCodeDataType.DEFAULT -> 16
        }
        else -> when (dataType) {
            QRCodeDataType.NUMBERS -> 14
            QRCodeDataType.UPPER_ALPHA_NUM -> 13
            QRCodeDataType.DEFAULT -> 16
        }
    }
}

internal class QR8BitByte(data: String) : QRData(QRCodeDataType.DEFAULT, data) {
    private val dataBytes = data.encodeToByteArray()
    override fun length() = dataBytes.size
    override fun write(buffer: BitBuffer) {
        for (b in dataBytes) buffer.put(b.toInt() and 0xFF, 8)
    }
}

internal class QRAlphaNum(data: String) : QRData(QRCodeDataType.UPPER_ALPHA_NUM, data) {
    override fun length() = data.length
    override fun write(buffer: BitBuffer) {
        var i = 0
        while (i + 1 < data.length) {
            buffer.put(charCode(data[i]) * 45 + charCode(data[i + 1]), 11)
            i += 2
        }
        if (i < data.length) buffer.put(charCode(data[i]), 6)
    }

    private fun charCode(c: Char) = when (c) {
        in '0'..'9' -> c - '0'
        in 'A'..'Z' -> c - 'A' + 10
        ' ' -> 36; '$' -> 37; '%' -> 38; '*' -> 39; '+' -> 40
        '-' -> 41; '.' -> 42; '/' -> 43; ':' -> 44
        else -> throw IllegalArgumentException("Illegal character: $c")
    }
}

internal class QRNumber(data: String) : QRData(QRCodeDataType.NUMBERS, data) {
    override fun length() = data.length
    override fun write(buffer: BitBuffer) {
        var i = 0
        while (i + 2 < data.length) {
            buffer.put(data.substring(i, i + 3).toInt(), 10)
            i += 3
        }
        if (data.length - i == 1) buffer.put(data.substring(i, i + 1).toInt(), 4)
        else if (data.length - i == 2) buffer.put(data.substring(i, i + 2).toInt(), 7)
    }
}
