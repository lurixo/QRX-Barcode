buildscript {
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.3.0")
    }
}

plugins {
    alias(libs.plugins.android.library) apply false
}
