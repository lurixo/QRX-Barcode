# QRX Barcode

[![Android CI](https://github.com/lurixo/QRX-Barcode/actions/workflows/android.yml/badge.svg)](https://github.com/lurixo/QRX-Barcode/actions/workflows/android.yml)

[简体中文](README_zh-CN.md)

Pure Android barcode generation library, zero external dependencies.

## Origin

This project is based on [qrose](https://github.com/alexzhirkevich/qrose) with the following changes:

**Removed:**
- Compose UI dependency (`androidx.compose.*`)
- Compose Multiplatform support (iOS, Desktop, Web)
- `QrCodePainter`, `BarcodePainter` (Compose Painter classes)
- `rememberQrCodePainter`, `rememberBarcodePainter` (Composable functions)
- Style system (`QrShapes`, `QrColors`, `QrBrush`, `QrLogo`, etc.)
- DSL builders
- `CachedPainter`, `ImageBitmap` converters

**Rewritten:**
- `QrCodeGenerator` - New API using Android `Canvas` + `Bitmap`
- `BarcodeGenerator` - New API using Android `Canvas` + `Bitmap`
- `QRCodeSetup` - Simplified, removed `QRCodeSquare` wrapper

**Kept (with simplification):**
- QR code encoding algorithm (`QRCode`, `QRUtil`, `QRMath`, `RSBlock`, `Polynomial`, `BitBuffer`, `QRData`)
- 1D barcode encoders (`Code128`, `Code39`, `Code93`, `EAN13`, `EAN8`, `UPCA`, `UPCE`, `ITF`, `Codabar`)

## Usage

```kotlin
// QR Code
val bitmap = QrCodeGenerator.generateBitmap("https://example.com", 512, ErrorCorrectionLevel.High)
val bytes = QrCodeGenerator.generatePng("Hello", 256)

// Barcode
val barcode = BarcodeGenerator.generateBitmap("ABC123", BarcodeType.Code128, 400, 150)
```

## Supported Formats

- **QR Code**: L/M/Q/H error correction levels
- **1D Barcode**: Code128, Code39, Code93, EAN13, EAN8, UPCA, UPCE, ITF, Codabar

## Dependencies

None. This library uses only Android SDK built-in APIs (`android.graphics.*`) with no third-party dependencies.

## Requirements

- **Kotlin**: 2.3.0
- **Java**: 25
- **Android Gradle Plugin**: 9.0.0
- **Gradle**: 9.3.1
- **Min SDK**: 34 (Android 14)
- **Compile SDK**: 36

## Installation

### Local Module

```kotlin
// settings.gradle.kts
include(":library")

// app/build.gradle.kts
implementation(project(":library"))
```

### JitPack (Coming Soon)

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        maven { url = uri("https://jitpack.io") }
    }
}

// app/build.gradle.kts
implementation("com.github.lurixo:QRX-Barcode:1.0.0")
```

## License

[MIT](LICENSE)
