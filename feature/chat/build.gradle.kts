plugins {
    alias(libs.plugins.koren.feature)
}

android {
    namespace = "com.koren.chat"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.data)
    implementation(projects.core.common)

    implementation(libs.lottie.compose)

    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.exoplayer.dash)
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.media3.ui.compose)

    implementation(libs.accompanist.permissions)
}