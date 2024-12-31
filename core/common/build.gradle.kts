plugins {
    alias(libs.plugins.koren.core)
    alias(libs.plugins.koren.hilt)
}

android {
    namespace = "com.koren.common"
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}