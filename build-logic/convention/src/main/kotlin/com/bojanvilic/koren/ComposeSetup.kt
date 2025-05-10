package com.bojanvilic.koren

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal fun Project.configureComposeSetup(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    with(this) {
        with(pluginManager) {
            apply("org.jetbrains.kotlin.plugin.compose")
        }
    }

    commonExtension.apply {
        buildFeatures {
            compose = true
        }

        kotlinOptions {
            jvmTarget = "17"
        }

        dependencies {
            val bom = libs.findLibrary("androidx-compose-bom").get()
            add("implementation", platform(bom))
            add("androidTestImplementation", platform(bom))
            add("implementation", libs.findLibrary("androidx-compose-activity").get())
            add("implementation", libs.findLibrary("androidx-compose-ui-tooling-preview").get())
            add("debugImplementation", libs.findLibrary("androidx-compose-ui-tooling").get())
            add("implementation", libs.findLibrary("androidx-compose-navigation").get())
            add("implementation", libs.findLibrary("hilt-navigation-compose").get())
            add("implementation", libs.findLibrary("coil-compose").get())
            add("implementation", libs.findLibrary("coil-network-okhttp").get())
            add("implementation", libs.findLibrary("coil-video").get())

            add("implementation", libs.findLibrary("androidx-compose-ui").get())
            add("implementation", libs.findLibrary("androidx-compose-ui-graphics").get())
            add("implementation", libs.findLibrary("androidx-compose-material3").get())
            add("androidTestImplementation", libs.findLibrary("androidx-compose-ui-test-junit4").get())
            add("debugImplementation", libs.findLibrary("androidx-compose-ui-test-manifest").get())
        }
    }
}