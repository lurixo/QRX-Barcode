package io.qrx.barcode.oned


internal object CodeUPCEEncoder : BarcodeEncoder  {

    private const val CODE_WIDTH = 3 + 7 * 6 +  // bars
            6 // end guard

    override fun encode(contents: String): BooleanArray {
        var contents = contents
        val length = contents.length
        if (length != 7 && length != 8) {
            throw IllegalArgumentException(
                "Requested contents should be 7 or 8 digits long, but got $length"
            )
        }
        contents.requireNumeric()
        when (length) {
            7 -> {
                val check: Int = getStandardUPCEANChecksum(convertUPCEtoUPCA(contents))
                contents += check
            }
            8 -> if (!checkStandardUPCEANChecksum(convertUPCEtoUPCA(contents))) {
                throw IllegalArgumentException("Contents do not pass checksum")
            }
        }
        val firstDigit = contents[0].digitToIntOrNull() ?: -1
        if (firstDigit != 0 && firstDigit != 1) {
            throw IllegalArgumentException("Number system must be 0 or 1")
        }
        val checkDigit = contents[7].digitToIntOrNull() ?: -1
        val parities: Int =
            UpcEan.NUMSYS_AND_CHECK_DIGIT_PATTERNS.get(firstDigit).get(checkDigit)
        val result = BooleanArray(CODE_WIDTH)
        var pos = appendPattern(result, 0, UpcEan.START_END_PATTERN, true)
        for (i in 1..6) {
            var digit = contents[i].digitToIntOrNull() ?: -1
            if (parities shr 6 - i and 1 == 1) {
                digit += 10
            }
            pos += appendPattern(result, pos, UpcEan.L_AND_G_PATTERNS.get(digit), false)
        }
        appendPattern(result, pos, UpcEan.END_PATTERN, true)
        return result
    }

}