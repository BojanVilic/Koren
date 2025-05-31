plugins {
    alias(libs.plugins.koren.core)
    alias(libs.plugins.koren.hilt)
}

android {
    namespace = "com.koren.domain"
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.data)

    implementation(libs.firebase.database)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.functions.ktx)

    implementation(libs.android.maps.utils)
    implementation(libs.play.services.places)
}