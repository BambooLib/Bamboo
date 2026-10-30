plugins {
    `java-library`
    id("org.jetbrains.kotlin.jvm")
    id("org.jlleitschuh.gradle.ktlint")
}

kotlin {
    jvmToolchain(25)
    explicitApi()
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

tasks.named("build") {
    dependsOn("ktlintCheck")
}
