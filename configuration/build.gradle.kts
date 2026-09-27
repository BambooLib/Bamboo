plugins {
    id("bamboo.kotlin-library")
}

val configurateVersion = providers.gradleProperty("configurateVersion").get()
val nightConfigVersion = providers.gradleProperty("nightConfigVersion").get()

dependencies {
    implementation("org.spongepowered:configurate-core:$configurateVersion")
    implementation("org.spongepowered:configurate-yaml:$configurateVersion")
    implementation("org.spongepowered:configurate-gson:$configurateVersion")
    implementation("org.spongepowered:configurate-hocon:$configurateVersion")

    implementation("com.electronwill.night-config:core:$nightConfigVersion")
    implementation("com.electronwill.night-config:toml:$nightConfigVersion")
}