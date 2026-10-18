plugins {
    id("bamboo.kotlin-library")
}

dependencies {
    api(project(":common"))

    testImplementation(kotlin("test"))
}