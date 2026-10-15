plugins {
    `java-library`
    id("java-common-conventions")
}

group = findProperty("group")!!
version = findProperty(project.name + "-version")!!

dependencies {
    api("org.openjfx:javafx-base:26.0.2:win")
    api("org.openjfx:javafx-base:26.0.2:mac")
    api("org.openjfx:javafx-base:26.0.2:linux")
}