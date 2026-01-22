package io.qrx.barcode.qrcode.internals

import io.qrx.barcode.qrcode.InternalErrorCorrectionLevel
import io.qrx.barcode.qrcode.MaskPattern

internal object QRCodeSetup {
    fun setupTopLeftPositionProbePattern(modules: Array<Array<Boolean?>>) = setupPositionProbePattern(0, 0, modules)
    fun setupTopRightPositionProbePattern(modules: Array<Array<Boolean?>>) = setupPositionProbePattern(0, modules.size - 7, modules)
    fun setupBottomLeftPositionProbePattern(modules: Array<Array<Boolean?>>) = setupPositionProbePattern(modules.size - 7, 0, modules)

    private fun setupPositionProbePattern(rowOffset: Int, colOffset: Int, modules: Array<Array<Boolean?>>) {
        val size = modules.size
        for (row in -1..7) {
            for (col in -1..7) {
                val r = row + rowOffset; val c = col + colOffset
                if (r !in 0 until size || c !in 0 until size) continue
                modules[r][c] = (col in 0..6 && (row == 0 || row == 6)) ||
                        (row in 0..6 && (col == 0 || col == 6)) ||
                        (row in 2..4 && col in 2..4)
            }
        }
    }

    fun setupPositionAdjustPattern(type: Int, modules: Array<Array<Boolean?>>) {
        val pos = QRUtil.getPatternPosition(type)
        for (i in pos.indices) {
            for (j in pos.indices) {
                val row = pos[i]; val col = pos[j]
                if (modules[row][col] != null) continue
                for (r in -2..2) for (c in -2..2) {
                    modules[row + r][col + c] = r == -2 || r == 2 || c == -2 || c == 2 || (r == 0 && c == 0)
                }
            }
        }
    }

    fun setupTimingPattern(moduleCount: Int, modules: Array<Array<Boolean?>>) {
        for (i in 8 until moduleCount - 8) {
            if (modules[i][6] == null) modules[i][6] = i % 2 == 0
            if (modules[6][i] == null) modules[6][i] = i % 2 == 0
        }
    }

    fun setupTypeInfo(ecl: InternalErrorCorrectionLevel, maskPattern: MaskPattern, moduleCount: Int, modules: Array<Array<Boolean?>>) {
        val bits = QRUtil.getBCHTypeInfo(ecl.value shl 3 or maskPattern.ordinal)
        for (i in 0..14) {
            val mod = bits shr i and 1 == 1
            when {
                i < 6 -> modules[i][8] = mod
                i < 8 -> modules[i + 1][8] = mod
                else -> modules[moduleCount - 15 + i][8] = mod
            }
        }
        for (i in 0..14) {
            val mod = bits shr i and 1 == 1
            when {
                i < 8 -> modules[8][moduleCount - i - 1] = mod
                i < 9 -> modules[8][15 - i] = mod
                else -> modules[8][15 - i - 1] = mod
            }
        }
        modules[moduleCount - 8][8] = true
    }

    fun setupTypeNumber(type: Int, moduleCount: Int, modules: Array<Array<Boolean?>>) {
        val bits = QRUtil.getBCHTypeNumber(type)
        for (i in 0..17) {
            val mod = bits shr i and 1 == 1
            modules[i / 3][i % 3 + moduleCount - 8 - 3] = mod
            modules[i % 3 + moduleCount - 8 - 3][i / 3] = mod
        }
    }

    fun applyMaskPattern(data: IntArray, maskPattern: MaskPattern, moduleCount: Int, modules: Array<Array<Boolean?>>) {
        var inc = -1; var bitIndex = 7; var byteIndex = 0
        var row = moduleCount - 1; var col = moduleCount - 1
        while (col > 0) {
            if (col == 6) col--
            while (true) {
                for (c in 0..1) {
                    if (modules[row][col - c] == null) {
                        var dark = byteIndex < data.size && (data[byteIndex] ushr bitIndex and 1 == 1)
                        if (QRUtil.getMask(maskPattern, row, col - c)) dark = !dark
                        modules[row][col - c] = dark
                        if (--bitIndex == -1) { byteIndex++; bitIndex = 7 }
                    }
                }
                row += inc
                if (row < 0 || moduleCount <= row) { row -= inc; inc = -inc; break }
            }
            col -= 2
        }
    }
}
