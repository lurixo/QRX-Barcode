package io.qrx.barcode.oned

internal object UpcEan {
    val NUMSYS_AND_CHECK_DIGIT_PATTERNS = arrayOf(
        intArrayOf(0x38, 0x34, 0x32, 0x31, 0x2C, 0x26, 0x23, 0x2A, 0x29, 0x25),
        intArrayOf(0x07, 0x0B, 0x0D, 0x0E, 0x13, 0x19, 0x1C, 0x15, 0x16, 0x1A)
    )
    val START_END_PATTERN = intArrayOf(1, 1, 1)
    val MIDDLE_PATTERN = intArrayOf(1, 1, 1, 1, 1)
    val END_PATTERN = intArrayOf(1, 1, 1, 1, 1, 1)
    val L_PATTERNS = arrayOf(
        intArrayOf(3, 2, 1, 1), intArrayOf(2, 2, 2, 1), intArrayOf(2, 1, 2, 2), intArrayOf(1, 4, 1, 1), intArrayOf(1, 1, 3, 2),
        intArrayOf(1, 2, 3, 1), intArrayOf(1, 1, 1, 4), intArrayOf(1, 3, 1, 2), intArrayOf(1, 2, 1, 3), intArrayOf(3, 1, 1, 2)
    )
    val L_AND_G_PATTERNS by lazy {
        buildList(20) {
            addAll(L_PATTERNS)
            for (i in 10..19) {
                val widths = L_PATTERNS[i - 10]
                add(IntArray(widths.size) { widths[widths.size - it - 1] })
            }
        }
    }
    val FIRST_DIGIT_ENCODINGS = intArrayOf(0x00, 0x0B, 0x0D, 0xE, 0x13, 0x19, 0x1C, 0x15, 0x16, 0x1A)
}

internal fun String.requireNumeric() = require(all { it.isDigit() }) { "Input should only contain digits 0-9" }

internal fun convertUPCEtoUPCA(upce: String): String {
    val upceChars = upce.substring(1, 7).toCharArray()
    val result = StringBuilder(12).append(upce[0])
    when (val lastChar = upceChars[5]) {
        '0', '1', '2' -> result.append(upceChars, 0, 2).append(lastChar).append("0000").append(upceChars, 2, 3)
        '3' -> result.append(upceChars, 0, 3).append("00000").append(upceChars, 3, 2)
        '4' -> result.append(upceChars, 0, 4).append("00000").append(upceChars[4])
        else -> result.append(upceChars, 0, 5).append("0000").append(lastChar)
    }
    if (upce.length >= 8) result.append(upce[7])
    return result.toString()
}

internal fun getStandardUPCEANChecksum(s: CharSequence): Int {
    var sum = 0
    var i = s.length - 1
    while (i >= 0) { sum += s[i].code - '0'.code; i -= 2 }
    sum *= 3
    i = s.length - 2
    while (i >= 0) { sum += s[i].code - '0'.code; i -= 2 }
    return (1000 - sum) % 10
}

internal fun checkStandardUPCEANChecksum(s: CharSequence): Boolean {
    val length = s.length
    if (length == 0) return false
    val check = s[length - 1].code - '0'.code
    return getStandardUPCEANChecksum(s.subSequence(0, length - 1)) == check
}
