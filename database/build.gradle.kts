plugins {
    id("bamboo.kotlin-library")
}

val hikariVersion = providers.gradleProperty("hikariVersion").get()

dependencies {
    implementation("com.zaxxer:HikariCP:$hikariVersion")
    implementation("org.mongodb:mongodb-driver-sync:5.13.0")
    implementation("io.lettuce:lettuce-core:7.8.0.RELEASE")

    testImplementation(kotlin("test"))
    testRuntimeOnly("org.slf4j:slf4j-simple:2.0.20")
    testRuntimeOnly("org.xerial:sqlite-jdbc:3.53.4.0")
    testRuntimeOnly("com.h2database:h2:2.5.252")
}

tasks.test {
    useJUnitPlatform()

    jvmArgs("--enable-native-access=ALL-UNNAMED")
}