plugins {
    id("bamboo.kotlin-library")
}

val nightConfigVersion = providers.gradleProperty("nightConfigVersion").get()

dependencies {
    implementation("com.electronwill.night-config:core:${nightConfigVersion}")
    implementation("com.electronwill.night-config:yaml:${nightConfigVersion}")
    implementation("com.electronwill.night-config:json:${nightConfigVersion}")
    implementation("com.electronwill.night-config:hocon:${nightConfigVersion}")
    implementation("com.electronwill.night-config:toml:${nightConfigVersion}")
}