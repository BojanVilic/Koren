plugins {
    alias(libs.plugins.koren.core)
    alias(libs.plugins.koren.hilt)
    alias(libs.plugins.koren.compose.library)
}

android {
    namespace = "com.koren.common"
}

dependencies {
    implementation(libs.molecule.runtime)
}