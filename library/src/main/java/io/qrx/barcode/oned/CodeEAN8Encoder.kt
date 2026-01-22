package io.qrx.barcode.oned

internal object CodeEAN8Encoder : BarcodeEncoder  {
    private val CODE_WIDTH = 3 + 7 * 4 +  // left bars
            5 + 7 * 4 +  // right bars
            3 // end guard


    override fun encode(contents: String): BooleanArray {
        var actualContents = contents
        val length = actualContents.length
        if (length != 7 && length != 8) {
            throw IllegalArgumentException(
                "Requested contents should be 7 or 8 digits long, but got $length"
            )
        }
        actualContents.requireNumeric()
        when (length) {
            7 -> {
                val check: Int = getStandardUPCEANChecksum(actualContents)
                actualContents += check
            }
            8 -> if (!checkStandardUPCEANChecksum(actualContents)) {
                throw IllegalArgumentException("Contents do not pass checksum")
            }
        }
        val result = BooleanArray(CODE_WIDTH)
        var pos = 0
        pos += appendPattern(result, pos, UpcEan.START_END_PATTERN, true)
        for (i in 0..3) {
            val digit = actualContents[i].digitToIntOrNull() ?: -1
            pos += appendPattern(
                result,
                pos,
                UpcEan.L_PATTERNS[digit],
                false
            )
        }
        pos += appendPattern(
            result,
            pos,
            UpcEan.MIDDLE_PATTERN,
            false
        )
        for (i in 4..7) {
            val digit = actualContents[i].digitToIntOrNull() ?: -1
            pos += appendPattern(result, pos, UpcEan.L_PATTERNS[digit], true)
        }
        appendPattern(result, pos, UpcEan.START_END_PATTERN, true)
        return result
    }

}

