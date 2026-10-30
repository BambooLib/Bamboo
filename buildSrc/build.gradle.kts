plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation(
        "org.jetbrains.kotlin:kotlin-gradle-plugin:${libs.versions.kotlin.get()}"
    )

    implementation(
        "org.jlleitschuh.gradle:ktlint-gradle:${libs.versions.ktlint.get()}"
    )
}
