plugins {
    java
    application
    id("com.gradleup.shadow") version "9.5.1"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    val javaFxVersion = "17.0.7"

    // JavaFX runtime dependencies for all supported platforms
    implementation("org.openjfx:javafx-base:$javaFxVersion:win")
    implementation("org.openjfx:javafx-base:$javaFxVersion:mac")
    implementation("org.openjfx:javafx-base:$javaFxVersion:linux")

    implementation("org.openjfx:javafx-controls:$javaFxVersion:win")
    implementation("org.openjfx:javafx-controls:$javaFxVersion:mac")
    implementation("org.openjfx:javafx-controls:$javaFxVersion:linux")

    implementation("org.openjfx:javafx-fxml:$javaFxVersion:win")
    implementation("org.openjfx:javafx-fxml:$javaFxVersion:mac")
    implementation("org.openjfx:javafx-fxml:$javaFxVersion:linux")

    implementation("org.openjfx:javafx-graphics:$javaFxVersion:win")
    implementation("org.openjfx:javafx-graphics:$javaFxVersion:mac")
    implementation("org.openjfx:javafx-graphics:$javaFxVersion:linux")

    // Testing
    testImplementation(platform("org.junit:junit-bom:5.14.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

application {
    mainClass.set("sanyueqi.ui.Launcher")
}

tasks.named<JavaExec>("run") {
    enableAssertions = true
}

tasks.test {
    useJUnitPlatform()
}

tasks.shadowJar {
    archiveFileName.set("SanYueQi.jar")
}