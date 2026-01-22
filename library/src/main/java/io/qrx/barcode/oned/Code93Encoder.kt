package io.qrx.barcode.oned

internal object Code93Encoder : BarcodeEncoder {

    private const val ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. $/+%"

    private val CHARACTER_ENCODINGS = intArrayOf(
        0x114, 0x148, 0x144, 0x142, 0x128, 0x124, 0x122, 0x150, 0x112, 0x10A,
        0x1A8, 0x1A4, 0x1A2, 0x194, 0x192, 0x18A, 0x168, 0x164, 0x162, 0x134,
        0x11A, 0x158, 0x14C, 0x146, 0x12C, 0x116, 0x1B4, 0x1B2, 0x1AC, 0x1A6,
        0x196, 0x19A, 0x16C, 0x166, 0x136, 0x13A,
        0x12E, 0x1D4, 0x1D2, 0x1CA, 0x16E, 0x176, 0x1AE,
        0x126, 0x1DA, 0x1D6, 0x132, 0x15E
    )

    private const val ASTERISK_ENCODING = 0x15E

    override fun encode(data: String): BooleanArray {
        val extendedContent = toExtendedMode(data)
        if (extendedContent.size > 80) {
            throw IllegalArgumentException("Content too long: ${extendedContent.size}")
        }

        val checkC = computeChecksum(extendedContent, 20)
        val withCheckC = extendedContent + checkC
        val checkK = computeChecksum(withCheckC, 15)
        val finalContent = withCheckC + checkK

        val codeWidth = 9 + finalContent.size * 9 + 9 + 1
        val result = BooleanArray(codeWidth)

        var pos = appendPattern(result, 0, ASTERISK_ENCODING)
        for (idx in finalContent) {
            pos = appendPattern(result, pos, CHARACTER_ENCODINGS[idx])
        }
        pos = appendPattern(result, pos, ASTERISK_ENCODING)
        result[pos] = true

        return result
    }

    private fun appendPattern(result: BooleanArray, start: Int, pattern: Int): Int {
        var pos = start
        for (i in 0 until 9) {
            val bit = (pattern shr (8 - i)) and 1
            result[pos++] = bit == 1
        }
        return pos
    }

    private fun computeChecksum(content: IntArray, maxWeight: Int): Int {
        var weight = 1
        var sum = 0
        for (i in content.size - 1 downTo 0) {
            sum += content[i] * weight
            weight++
            if (weight > maxWeight) {
                weight = 1
            }
        }
        return sum % 47
    }

    private fun toExtendedMode(data: String): IntArray {
        val result = mutableListOf<Int>()
        for (c in data) {
            val idx = ALPHABET.indexOf(c)
            if (idx >= 0) {
                result.add(idx)
            } else {
                when {
                    c.code == 0 -> { result.add(45); result.add(30) }
                    c.code in 1..26 -> { result.add(43); result.add(c.code - 1 + 10) }
                    c.code in 27..31 -> { result.add(45); result.add(c.code - 27 + 10) }
                    c == '!' -> { result.add(44); result.add(10) }
                    c == '"' -> { result.add(44); result.add(11) }
                    c == '#' -> { result.add(44); result.add(12) }
                    c == '&' -> { result.add(44); result.add(15) }
                    c == '\'' -> { result.add(44); result.add(16) }
                    c == '(' -> { result.add(44); result.add(17) }
                    c == ')' -> { result.add(44); result.add(18) }
                    c == '*' -> { result.add(44); result.add(19) }
                    c == ',' -> { result.add(44); result.add(21) }
                    c == ':' -> { result.add(44); result.add(35) }
                    c == ';' -> { result.add(45); result.add(15) }
                    c == '<' -> { result.add(45); result.add(16) }
                    c == '=' -> { result.add(45); result.add(17) }
                    c == '>' -> { result.add(45); result.add(18) }
                    c == '?' -> { result.add(45); result.add(19) }
                    c == '@' -> { result.add(45); result.add(31) }
                    c == '[' -> { result.add(45); result.add(20) }
                    c == '\\' -> { result.add(45); result.add(21) }
                    c == ']' -> { result.add(45); result.add(22) }
                    c == '^' -> { result.add(45); result.add(23) }
                    c == '_' -> { result.add(45); result.add(24) }
                    c == '`' -> { result.add(45); result.add(32) }
                    c in 'a'..'z' -> { result.add(46); result.add(c - 'a' + 10) }
                    c == '{' -> { result.add(45); result.add(25) }
                    c == '|' -> { result.add(45); result.add(26) }
                    c == '}' -> { result.add(45); result.add(27) }
                    c == '~' -> { result.add(45); result.add(28) }
                    c.code == 127 -> { result.add(45); result.add(29) }
                    else -> throw IllegalArgumentException("Non-encodable character: '$c'")
                }
            }
        }
        return result.toIntArray()
    }
}
