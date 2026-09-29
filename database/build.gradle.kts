plugins {
    id("bamboo.kotlin-library")
}

val hikariVersion = providers.gradleProperty("hikariVersion").get()

dependencies {
    implementation("com.zaxxer:HikariCP:$hikariVersion")
}