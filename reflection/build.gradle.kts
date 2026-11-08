plugins {
    id("bamboo.kotlin-library")
    id("me.champeau.jmh") version "0.7.3"
}

group = rootProject.group
version = rootProject.version

dependencies {
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}
