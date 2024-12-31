import com.bojanvilic.koren.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class HiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.kapt")
                apply("com.google.dagger.hilt.android")
            }

            dependencies {
                add("implementation", libs.findLibrary("hilt-android").get())
                add("kapt", libs.findLibrary("hilt-compiler").get())
                add("kaptAndroidTest", libs.findLibrary("hilt-compiler").get())
            }
        }
    }
}