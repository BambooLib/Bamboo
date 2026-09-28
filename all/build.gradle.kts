plugins {
    id("bamboo.kotlin-library")
}

dependencies {
    api(project(":configuration"))
    api(project(":plugin"))
}