# Build and run Yapper as a JAR

The Gradle build applies `gradle/jar.gradle` to create `build/libs/yapper.jar`.
This fat JAR contains the application, JavaFX classes and desktop native
libraries. Its entry point is `yapper.Launcher`; JUnit is not distributed.

## Build

Install JDK 25 and set `JAVA_HOME` and `PATH` to it. From the project folder:

```powershell
java -version
.\gradlew.bat --version
.\gradlew.bat clean build
```

On macOS/Linux, use `./gradlew --version` and `./gradlew clean build`.
For a packaging-only build, use `jar` instead of `build`; this skips tests.
On macOS, the course specifies `sdk use java 25.0.3.fx-zulu` when using SDKMAN.

The single output is `build/libs/yapper.jar`. Generated build files are ignored
by Git. The build bundles native libraries for Windows x64, Linux x64, and macOS x64. ARM64 native libraries are not bundled or verified. Bundling these libraries does not replace a real smoke test
on each target operating system.

## Smoke test

Copy `yapper.jar` into an empty folder. Open a terminal in that folder, confirm
Java 25, and run:

```text
java -jar yapper.jar
```

In the window, send `todo verify jar packaging`, `list`, `mark 1`, and `stats`.
Expect one completed task and zero pending tasks. Enter `bye`, close the window,
then reopen the JAR and send `list`: the completed task should remain.
Also try an invalid command and resize the window to check readable responses.

Yapper creates `data/yapper.txt` under the terminal's working folder. Keep that
folder to preserve the saved list. Running the JAR needs a desktop environment.

## Distribution

Publish one `.jar` asset in a public GitHub release, then download that asset
and repeat the smoke test. The course evaluates the latest release. Verify its
visibility without signing in; do not substitute a zip archive for the JAR.
See the [remaining delivery checks](remaining-course-work.md) for work that
still needs external verification.
