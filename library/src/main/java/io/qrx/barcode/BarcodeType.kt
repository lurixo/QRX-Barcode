package io.qrx.barcode

import io.qrx.barcode.oned.*

enum class BarcodeType(internal val encoder: BarcodeEncoder) {
    Codabar(CodabarEncoder),
    Code39(Code39Encoder),
    Code93(Code93Encoder),
    Code128(Code128Encoder),
    EAN8(CodeEAN8Encoder),
    EAN13(CodeEAN13Encoder),
    ITF(CodeITFEncoder),
    UPCA(CodeUPCAEncoder),
    UPCE(CodeUPCEEncoder)
}
