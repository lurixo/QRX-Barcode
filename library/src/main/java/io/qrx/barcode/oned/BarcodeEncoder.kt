package io.qrx.barcode.oned

interface BarcodeEncoder {
    fun encode(data: String): BooleanArray
}
