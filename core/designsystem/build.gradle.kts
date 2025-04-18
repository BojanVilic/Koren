plugins {
    alias(libs.plugins.koren.core)
    alias(libs.plugins.koren.compose.library)
}

android {
    namespace = "com.koren.designsystem"
}

dependencies {
    implementation(projects.core.common)
    implementation(libs.lottie.compose)

    implementation(libs.qrcode.kotlin)
    implementation(libs.androidx.ui.text.google.fonts)
}