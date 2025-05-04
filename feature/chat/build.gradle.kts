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
}