# QRX Barcode

[![Android CI](https://github.com/lurixo/qrx-barcode/actions/workflows/android.yml/badge.svg)](https://github.com/lurixo/qrx-barcode/actions/workflows/android.yml)

[English](README.md)

纯 Android 条码生成库，零 Compose 依赖。

## 来源

本项目基于 [qrose](https://github.com/alexzhirkevich/qrose) 修改，具体变更如下：

**删除：**
- Compose UI 依赖 (`androidx.compose.*`)
- Compose Multiplatform 多平台支持 (iOS, Desktop, Web)
- `QrCodePainter`, `BarcodePainter` (Compose Painter 类)
- `rememberQrCodePainter`, `rememberBarcodePainter` (Composable 函数)
- 样式系统 (`QrShapes`, `QrColors`, `QrBrush`, `QrLogo` 等)
- DSL 构建器
- `CachedPainter`, `ImageBitmap` 转换器

**重写：**
- `QrCodeGenerator` - 使用 Android `Canvas` + `Bitmap` 的新 API
- `BarcodeGenerator` - 使用 Android `Canvas` + `Bitmap` 的新 API
- `QRCodeSetup` - 简化实现，移除 `QRCodeSquare` 包装类

**保留（已精简）：**
- QR码编码算法 (`QRCode`, `QRUtil`, `QRMath`, `RSBlock`, `Polynomial`, `BitBuffer`, `QRData`)
- 一维码编码器 (`Code128`, `Code39`, `Code93`, `EAN13`, `EAN8`, `UPCA`, `UPCE`, `ITF`, `Codabar`)

## 使用方法

```kotlin
// 二维码
val bitmap = QrCodeGenerator.generateBitmap("https://example.com", 512, ErrorCorrectionLevel.High)
val bytes = QrCodeGenerator.generatePng("Hello", 256)

// 一维码
val barcode = BarcodeGenerator.generateBitmap("ABC123", BarcodeType.Code128, 400, 150)
```

## 支持的格式

- **二维码**: L/M/Q/H 四种纠错级别
- **一维码**: Code128, Code39, Code93, EAN13, EAN8, UPCA, UPCE, ITF, Codabar

## 环境要求

- **Java**: 21
- **Android Gradle Plugin**: 9.0.0
- **Gradle**: 9.3.0
- **最低 SDK**: 34 (Android 14)
- **编译 SDK**: 36

## 安装

### 本地模块

```kotlin
// settings.gradle.kts
include(":library")

// app/build.gradle.kts
implementation(project(":library"))
```

### JitPack (即将支持)

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        maven { url = uri("https://jitpack.io") }
    }
}

// app/build.gradle.kts
implementation("com.github.lurixo:qrx-barcode:1.0.0")
```

## 许可证

[MIT](LICENSE)
