package io.qrx.barcode.qrcode.internals

internal class Polynomial(num: IntArray, shift: Int = 0) {
    private val data: IntArray

    init {
        val offset = num.indexOfFirst { it != 0 }.coerceAtLeast(0)
        data = IntArray(num.size - offset + shift) { 0 }
        for (i in 0 until num.size - offset) data[i] = num[offset + i]
    }

    operator fun get(i: Int) = data[i]
    fun len() = data.size

    fun multiply(other: Polynomial): Polynomial {
        val result = IntArray(len() + other.len() - 1) { 0 }
        for (i in 0 until len()) {
            for (j in 0 until other.len()) {
                result[i + j] = result[i + j] xor QRMath.gexp(QRMath.glog(this[i]) + QRMath.glog(other[j]))
            }
        }
        return Polynomial(result)
    }

    fun mod(other: Polynomial): Polynomial {
        if (len() - other.len() < 0) return this
        val ratio = QRMath.glog(this[0]) - QRMath.glog(other[0])
        val result = data.copyOf()
        for (i in other.data.indices) {
            result[i] = result[i] xor QRMath.gexp(QRMath.glog(other.data[i]) + ratio)
        }
        return Polynomial(result).mod(other)
    }
}
