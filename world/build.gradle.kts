plugins {
    id("bamboo.kotlin-library")
}

group = rootProject.group
version = rootProject.version

dependencies {
    api(project(":common"))
    api(project(":geometry"))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}
