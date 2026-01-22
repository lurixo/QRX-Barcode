package io.qrx.barcode.oned

internal object CodeUPCAEncoder: BarcodeEncoder {
    override fun encode(contents : String) = CodeEAN13Encoder.encode("0$contents")
}