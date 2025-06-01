pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "Koren"
include(":app")
include(":core:designsystem")
include(":feature:auth")
include(":core:common")
include(":feature:home")
include(":feature:onboarding")
include(":feature:map")
include(":feature:activity")
include(":feature:account")
include(":feature:invitation")
include(":core:data")
include(":core:domain")
include(":feature:calendar")
include(":core:notifications")
include(":feature:chat")
include(":feature:manage_familiy")
include(":feature:answers")
