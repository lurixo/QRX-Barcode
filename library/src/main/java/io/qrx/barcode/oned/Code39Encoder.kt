package io.qrx.barcode.oned

internal object Code39Encoder : BarcodeEncoder {
    const val ALPHABET_STRING = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. \$/+%"
    val CHARACTER_ENCODINGS = intArrayOf(
        0x034, 0x121, 0x061, 0x160, 0x031, 0x130, 0x070, 0x025, 0x124, 0x064,
        0x109, 0x049, 0x148, 0x019, 0x118, 0x058, 0x00D, 0x10C, 0x04C, 0x01C,
        0x103, 0x043, 0x142, 0x013, 0x112, 0x052, 0x007, 0x106, 0x046, 0x016,
        0x181, 0x0C1, 0x1C0, 0x091, 0x190, 0x0D0, 0x085, 0x184, 0x0C4, 0x0A8,
        0x0A2, 0x08A, 0x02A
    )
    val ASTERISK_ENCODING = 0x094

    override fun encode(data: String): BooleanArray {
        var content = data
        if (content.length > 80) throw IllegalArgumentException("Content too long: ${content.length}")
        for (c in content) if (ALPHABET_STRING.indexOf(c) < 0) { content = tryExtendedMode(content); break }
        if (content.length > 80) throw IllegalArgumentException("Content too long after extended mode: ${content.length}")

        val widths = IntArray(9)
        val result = BooleanArray(24 + 1 + 13 * content.length)
        toIntArray(ASTERISK_ENCODING, widths)
        var pos = appendPattern(result, 0, widths, true)
        pos += appendPattern(result, pos, intArrayOf(1), false)
        for (c in content) {
            toIntArray(CHARACTER_ENCODINGS[ALPHABET_STRING.indexOf(c)], widths)
            pos += appendPattern(result, pos, widths, true)
            pos += appendPattern(result, pos, intArrayOf(1), false)
        }
        toIntArray(ASTERISK_ENCODING, widths)
        appendPattern(result, pos, widths, true)
        return result
    }

    private fun toIntArray(a: Int, arr: IntArray) { for (i in 0..8) arr[i] = if (a and (1 shl (8 - i)) == 0) 1 else 2 }

    private fun tryExtendedMode(contents: String) = buildString {
        for (c in contents) {
            when {
                c == '\u0000' -> append("%U")
                c in ALPHABET_STRING -> append(c)
                c == '@' -> append("%V")
                c == '`' -> append("%W")
                c.code in 1..26 -> append('$').append(('A'.code + c.code - 1).toChar())
                c.code in 27..31 -> append('%').append(('A'.code + c.code - 27).toChar())
                c == '!' -> append("/A")
                c == '"' -> append("/B")
                c == '#' -> append("/C")
                c == '&' -> append("/F")
                c == '\'' -> append("/G")
                c == '(' -> append("/H")
                c == ')' -> append("/I")
                c == '*' -> append("/J")
                c == ',' -> append("/L")
                c == ':' -> append("/Z")
                c.code in 59..63 -> append('%').append(('F'.code + c.code - 59).toChar())
                c.code in 91..95 -> append('%').append(('K'.code + c.code - 91).toChar())
                c in 'a'..'z' -> append('+').append(('A'.code + c.code - 97).toChar())
                c.code in 123..127 -> append('%').append(('P'.code + c.code - 123).toChar())
                else -> throw IllegalArgumentException("Non-encodable character: '$c'")
            }
        }
    }
}
