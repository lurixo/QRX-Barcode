plugins {
    alias(libs.plugins.android.library)
    id("maven-publish")
}

android {
    namespace = "io.qrx.barcode"
    compileSdk = 36

    defaultConfig {
        minSdk = 34
        targetSdk = 36

        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_25
        targetCompatibility = JavaVersion.VERSION_25
    }
}

publishing {
    publications {
        register<MavenPublication>("release") {
            afterEvaluate {
                from(components["release"])
            }

            pom {
                name.set("QRX Barcode")
                description.set("Pure Android barcode generation library, zero external dependencies")
                url.set("https://github.com/lurixo/QRX-Barcode")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
            }
        }
    }
}

android.publishing {
    singleVariant("release") {
        withSourcesJar()
    }
}
