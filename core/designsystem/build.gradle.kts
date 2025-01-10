plugins {
    alias(libs.plugins.koren.core)
    alias(libs.plugins.koren.compose.library)
}

android {
    namespace = "com.koren.designsystem"
}

dependencies {
    implementation(libs.lottie.compose)

    implementation(libs.qrcode.kotlin)
}