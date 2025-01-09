plugins {
    alias(libs.plugins.koren.feature)
}

android {
    namespace = "com.koren.invitation"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.domain)

    implementation(libs.qrcode.kotlin)

    implementation(libs.lottie.compose)
}