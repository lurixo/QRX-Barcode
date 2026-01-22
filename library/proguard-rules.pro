# QRX Barcode Library ProGuard Rules

# 保留公开 API
-keep class io.qrx.barcode.QrCodeGenerator { *; }
-keep class io.qrx.barcode.BarcodeGenerator { *; }
-keep class io.qrx.barcode.BarcodeType { *; }
-keep class io.qrx.barcode.ErrorCorrectionLevel { *; }

# 保留枚举
-keepclassmembers enum io.qrx.barcode.** { *; }
