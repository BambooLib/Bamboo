plugins {
    id("bamboo.kotlin-library")
}

dependencies {
    api(project(":common"))
    api(project(":reflection"))
    api(project(":configuration"))
    api(project(":database"))
    api(project(":packets"))
    api(project(":actions"))
    api(project(":plugin"))
}