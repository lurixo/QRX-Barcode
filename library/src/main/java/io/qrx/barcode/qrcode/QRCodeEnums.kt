package io.qrx.barcode.qrcode

internal enum class InternalErrorCorrectionLevel(val value: Int, val maxTypeNum: Int) {
    L(1, 41), M(0, 41), Q(3, 41), H(2, 41)
}

internal enum class MaskPattern {
    PATTERN000, PATTERN001, PATTERN010, PATTERN011,
    PATTERN100, PATTERN101, PATTERN110, PATTERN111
}

internal enum class QRCodeDataType(val value: Int) {
    NUMBERS(1 shl 0),
    UPPER_ALPHA_NUM(1 shl 1),
    DEFAULT(1 shl 2)
}
