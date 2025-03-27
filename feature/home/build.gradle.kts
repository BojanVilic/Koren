plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.koren.feature)
}

android {
    namespace = "com.koren.home"
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.data)
    implementation(projects.core.domain)

    implementation(libs.firebase.database)
    implementation(libs.firebase.storage)

    implementation(libs.lottie.compose)

    implementation(libs.accompanist.permissions)

    implementation(libs.android.maps.utils)
    implementation(libs.play.services.places)

    implementation(libs.androidx.material.navigation)
}
