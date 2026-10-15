plugins {
    application
    id("java-common-conventions")
    id("org.graalvm.buildtools.native")
    id("com.gradleup.shadow") version "9.5.1"
}

group = findProperty("group")!!
version = findProperty(project.name + "-version")!!

val outputName = (findProperty("outputName") ?: "${project.name}-v${project.version}") as String

dependencies {
    implementation(project(":marquee-core"))
}

application {
    mainClass = "org.cs2103t.marquee.cli.Application"
}

graalvmNative {
    toolchainDetection = true

    binaries.named("main") {
        imageName = outputName
        buildArgs.add("--static-nolibc")
        buildArgs.add("-march=compatibility")
        buildArgs.add("-O3")
    }

    agent {
        defaultMode = "standard"
    }
}

tasks.jar {
    archiveFileName = "${outputName}-lean.jar"
}

tasks.shadowJar {
    archiveFileName = "${outputName}.jar"
}

tasks.run {
    standardInput = System.`in`
}