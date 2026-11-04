plugins {
    id("bamboo.kotlin-library")
}

group = rootProject.group
version = rootProject.version

dependencies {
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)

    implementation(libs.night.config.core)
    implementation(libs.night.config.yaml)
    implementation(libs.night.config.json)
    implementation(libs.night.config.hocon)
    implementation(libs.night.config.toml)
}
