
import com.android.build.gradle.LibraryExtension
import com.bojanvilic.koren.configureFeatureModule
import com.bojanvilic.koren.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.project

class FeatureConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
                apply("koren.compose.library")
                apply("koren.hilt")
                apply("org.jetbrains.kotlin.plugin.serialization")
            }

            extensions.configure<LibraryExtension> {
                configureFeatureModule(this) { targetVersion ->
                    defaultConfig.targetSdk = targetVersion
                }
            }

            dependencies {
                add("implementation", libs.findLibrary("androidx-core-ktx").get())
                add("implementation", libs.findLibrary("androidx-lifecycle-runtime-ktx").get())
                add("implementation", libs.findLibrary("androidx-appcompat").get())
                add("testImplementation", libs.findLibrary("junit").get())
                add("androidTestImplementation", libs.findLibrary("androidx-junit").get())
                add("androidTestImplementation", libs.findLibrary("androidx-espresso-core").get())
                add("implementation", libs.findLibrary("timber").get())
                add("implementation", libs.findLibrary("kotlin-serialization").get())

                add("implementation", project(":core:designsystem"))
                add("implementation", project(":core:common"))
            }
        }
    }
}