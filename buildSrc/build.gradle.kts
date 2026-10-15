plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation("org.graalvm.buildtools.native:org.graalvm.buildtools.native.gradle.plugin:1.1.12")
}