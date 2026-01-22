package io.qrx.barcode.qrcode

import io.qrx.barcode.qrcode.internals.*

internal class QRCode(
    private val data: String,
    private val errorCorrectionLevel: InternalErrorCorrectionLevel = InternalErrorCorrectionLevel.M,
    private val dataType: QRCodeDataType = QRUtil.getDataType(data)
) {
    private val qrCodeData: QRData = when (dataType) {
        QRCodeDataType.NUMBERS -> QRNumber(data)
        QRCodeDataType.UPPER_ALPHA_NUM -> QRAlphaNum(data)
        QRCodeDataType.DEFAULT -> QR8BitByte(data)
    }

    fun encode(
        type: Int = typeForDataAndECL(data, errorCorrectionLevel),
        maskPattern: MaskPattern = MaskPattern.PATTERN000
    ): Array<BooleanArray> {
        val moduleCount = type * 4 + 17
        val modules: Array<Array<Boolean?>> = Array(moduleCount) { Array(moduleCount) { null } }

        QRCodeSetup.setupTopLeftPositionProbePattern(modules)
        QRCodeSetup.setupTopRightPositionProbePattern(modules)
        QRCodeSetup.setupBottomLeftPositionProbePattern(modules)
        QRCodeSetup.setupPositionAdjustPattern(type, modules)
        QRCodeSetup.setupTimingPattern(moduleCount, modules)
        QRCodeSetup.setupTypeInfo(errorCorrectionLevel, maskPattern, moduleCount, modules)

        if (type >= 7) {
            QRCodeSetup.setupTypeNumber(type, moduleCount, modules)
        }

        val encodedData = createData(type)
        QRCodeSetup.applyMaskPattern(encodedData, maskPattern, moduleCount, modules)

        return Array(moduleCount) { row ->
            BooleanArray(moduleCount) { col -> modules[row][col] == true }
        }
    }

    private fun createData(type: Int): IntArray {
        val rsBlocks = RSBlock.getRSBlocks(type, errorCorrectionLevel)
        val buffer = BitBuffer()

        buffer.put(qrCodeData.dataType.value, 4)
        buffer.put(qrCodeData.length(), qrCodeData.getLengthInBits(type))
        qrCodeData.write(buffer)

        val totalDataCount = rsBlocks.sumOf { it.dataCount } * 8

        if (buffer.lengthInBits > totalDataCount) {
            throw IllegalArgumentException("Code length overflow (${buffer.lengthInBits} > $totalDataCount)")
        }

        if (buffer.lengthInBits + 4 <= totalDataCount) buffer.put(0, 4)
        while (buffer.lengthInBits % 8 != 0) buffer.put(false)

        while (true) {
            if (buffer.lengthInBits >= totalDataCount) break
            buffer.put(PAD0, 8)
            if (buffer.lengthInBits >= totalDataCount) break
            buffer.put(PAD1, 8)
        }

        return createBytes(buffer, rsBlocks)
    }

    private fun createBytes(buffer: BitBuffer, rsBlocks: Array<RSBlock>): IntArray {
        var offset = 0
        var maxDcCount = 0
        var maxEcCount = 0
        var totalCodeCount = 0
        val dcData = Array(rsBlocks.size) { IntArray(0) }
        val ecData = Array(rsBlocks.size) { IntArray(0) }

        rsBlocks.forEachIndexed { i, it ->
            val dcCount = it.dataCount
            val ecCount = it.totalCount - dcCount

            totalCodeCount += it.totalCount
            maxDcCount = maxDcCount.coerceAtLeast(dcCount)
            maxEcCount = maxEcCount.coerceAtLeast(ecCount)

            dcData[i] = IntArray(dcCount) { idx -> 0xff and buffer.buffer[idx + offset] }
            offset += dcCount

            val rsPoly = QRUtil.getErrorCorrectPolynomial(ecCount)
            val rawPoly = Polynomial(dcData[i], rsPoly.len() - 1)
            val modPoly = rawPoly.mod(rsPoly)
            val ecDataSize = rsPoly.len() - 1

            ecData[i] = IntArray(ecDataSize) { idx ->
                val modIndex = idx + modPoly.len() - ecDataSize
                if (modIndex >= 0) modPoly[modIndex] else 0
            }
        }

        var index = 0
        val result = IntArray(totalCodeCount)

        for (i in 0 until maxDcCount) {
            for (r in rsBlocks.indices) {
                if (i < dcData[r].size) result[index++] = dcData[r][i]
            }
        }

        for (i in 0 until maxEcCount) {
            for (r in rsBlocks.indices) {
                if (i < ecData[r].size) result[index++] = ecData[r][i]
            }
        }

        return result
    }

    companion object {
        private const val PAD0 = 0xEC
        private const val PAD1 = 0x11

        fun typeForDataAndECL(
            data: String,
            errorCorrectionLevel: InternalErrorCorrectionLevel,
            dataType: QRCodeDataType = QRUtil.getDataType(data)
        ): Int {
            val qrCodeData = when (dataType) {
                QRCodeDataType.NUMBERS -> QRNumber(data)
                QRCodeDataType.UPPER_ALPHA_NUM -> QRAlphaNum(data)
                QRCodeDataType.DEFAULT -> QR8BitByte(data)
            }
            val dataLength = qrCodeData.length()

            for (typeNum in 1 until errorCorrectionLevel.maxTypeNum) {
                if (dataLength <= QRUtil.getMaxLength(typeNum, dataType, errorCorrectionLevel)) {
                    return typeNum
                }
            }
            return 40
        }
    }
}
