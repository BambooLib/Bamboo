plugins {
    `java-library`
    id("org.jetbrains.kotlin.jvm")
}

kotlin {
    jvmToolchain(25)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}