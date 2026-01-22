package io.qrx.barcode.oned

internal object CodabarEncoder : BarcodeEncoder {
    private val START_END_CHARS = charArrayOf('A', 'B', 'C', 'D')
    private val ALT_START_END_CHARS = charArrayOf('T', 'N', '*', 'E')
    private val TEN_LENGTH_CHARS = charArrayOf('/', ':', '+', '.')
    private const val ALPHABET_STRING = "0123456789-\$:/.+ABCD"
    private val ALPHABET = ALPHABET_STRING.toCharArray()
    private val CHARACTER_ENCODINGS = intArrayOf(
        0x003, 0x006, 0x009, 0x060, 0x012, 0x042, 0x021, 0x024, 0x030, 0x048,
        0x00c, 0x018, 0x045, 0x051, 0x054, 0x015, 0x01A, 0x029, 0x00B, 0x00E
    )

    override fun encode(data: String): BooleanArray {
        var contents = data
        if (contents.length < 2) {
            contents = "A${contents}A"
        } else {
            val first = contents[0].uppercaseChar()
            val last = contents[contents.length - 1].uppercaseChar()
            val startsNormal = first in START_END_CHARS
            val endsNormal = last in START_END_CHARS
            val startsAlt = first in ALT_START_END_CHARS
            val endsAlt = last in ALT_START_END_CHARS
            if (startsNormal && !endsNormal || startsAlt && !endsAlt || !startsNormal && !startsAlt && (endsNormal || endsAlt))
                throw IllegalArgumentException("Invalid start/end guards: $contents")
            if (!startsNormal && !startsAlt) contents = "A${contents}A"
        }

        var resultLength = 20
        for (i in 1 until contents.length - 1) {
            resultLength += when {
                contents[i].isDigit() || contents[i] == '-' || contents[i] == '$' -> 9
                contents[i] in TEN_LENGTH_CHARS -> 10
                else -> throw IllegalArgumentException("Cannot encode: '${contents[i]}'")
            }
        }
        resultLength += contents.length - 1

        val result = BooleanArray(resultLength)
        var position = 0
        for (index in contents.indices) {
            var c = contents[index].uppercaseChar()
            if (index == 0 || index == contents.length - 1) {
                c = when (c) { 'T' -> 'A'; 'N' -> 'B'; '*' -> 'C'; 'E' -> 'D'; else -> c }
            }
            val code = CHARACTER_ENCODINGS[ALPHABET.indexOf(c)]
            var color = true; var counter = 0; var bit = 0
            while (bit < 7) {
                result[position++] = color
                if (code shr (6 - bit) and 1 == 0 || counter == 1) { color = !color; bit++; counter = 0 } else counter++
            }
            if (index < contents.length - 1) result[position++] = false
        }
        return result
    }
}
