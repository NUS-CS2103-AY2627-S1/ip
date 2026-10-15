pluginManagement  {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement  {
    repositories {
        mavenCentral()
    }
}

rootProject.name = "marquee"
include("marquee-core")
include("marquee-cli")
include("marquee-gui")