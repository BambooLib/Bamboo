pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        mavenCentral()
    }
}

rootProject.name = "Bamboo"

include(
    ":common",
    ":configuration",
    ":database",
    ":packets",
    ":actions",
    ":plugin",
    ":all"
)