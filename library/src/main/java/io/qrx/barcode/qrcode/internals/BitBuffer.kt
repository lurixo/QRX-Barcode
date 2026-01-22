package io.qrx.barcode.qrcode.internals

internal class BitBuffer {
    var buffer = IntArray(32)
        private set
    var lengthInBits = 0
        private set

    fun put(num: Int, length: Int) {
        for (i in 0 until length) {
            put(num ushr length - i - 1 and 1 == 1)
        }
    }

    fun put(bit: Boolean) {
        if (lengthInBits == buffer.size * 8) {
            buffer = buffer.copyOf(buffer.size + 32)
        }
        if (bit) {
            buffer[lengthInBits / 8] = buffer[lengthInBits / 8] or (0x80 ushr lengthInBits % 8)
        }
        lengthInBits++
    }
}
