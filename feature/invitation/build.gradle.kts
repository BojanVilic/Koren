plugins {
    alias(libs.plugins.koren.feature)
}

android {
    namespace = "com.koren.invitation"
}

dependencies {
    implementation(projects.core.data)

    implementation(libs.qrcode.kotlin)
}