plugins {
    id("bamboo.kotlin-library")
}

group = rootProject.group
version = rootProject.version

dependencies {
    api(project(":reflection"))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}
