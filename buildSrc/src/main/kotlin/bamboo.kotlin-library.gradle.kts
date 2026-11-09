plugins {
    `java-library`
    id("org.jetbrains.kotlin.jvm")
    id("org.jlleitschuh.gradle.ktlint")
    `maven-publish`
}

kotlin {
    jvmToolchain(25)
    explicitApi()
}

java {
    withSourcesJar()
}

val bambooGroup = rootProject.group.toString()
val bambooVersion = rootProject.version.toString()

publishing {
    publications {
        register<MavenPublication>("maven") {
            groupId = bambooGroup
            version = bambooVersion

            from(components["java"])
        }
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

tasks.named("build") {
    dependsOn("ktlintCheck")
}
