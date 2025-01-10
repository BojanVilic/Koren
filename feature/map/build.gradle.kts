plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.koren.feature)
}

android {
    namespace = "com.koren.map"
}

dependencies {
    implementation(projects.core.domain)

    implementation(libs.maps.compose)

    implementation(libs.accompanist.permissions)
    implementation(libs.play.services.location)
}