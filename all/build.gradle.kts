plugins {
    id("bamboo.kotlin-library")
}

dependencies {
    api(project(":common"))
    api(project(":configuration"))
    api(project(":database"))
    api(project(":actions"))
    api(project(":plugin"))
}