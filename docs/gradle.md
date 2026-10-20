# Building Yapper with Gradle

Install JDK 25 and set `JAVA_HOME` to its installation directory (or put its
`bin` directory on `PATH`). In IntelliJ, also select JDK 25 as the Gradle JVM.
The build's Java toolchain selects JDK 25 for compilation, tests, and `run`.

Use the checked-in wrapper; a separate Gradle installation is unnecessary:

| Task | Windows PowerShell | macOS / Linux |
| --- | --- | --- |
| Check Gradle and JVM | `.\gradlew.bat --version` | `sh gradlew --version` |
| Clean and build | `.\gradlew.bat clean build` | `sh gradlew clean build` |
| Run the chatbot | `.\gradlew.bat --console=plain run` | `sh gradlew --console=plain run` |
| Run tests | `.\gradlew.bat test` | `sh gradlew test` |

The first invocation downloads Gradle; resolving JavaFX, Checkstyle, and test dependencies also requires
internet access. Generated files go in `build/`; local Gradle state goes in
`.gradle/`. Both directories are already ignored. Test results
are available at `build/reports/tests/test/index.html`.

Production sources belong under `src/main/java/yapper` with package `yapper`.
The GUI entry point is `yapper.Launcher`; it starts the FXML interface. JUnit Jupiter tests belong under
`src/test/java/yapper`; the build supplies Jupiter 5.13.4 and its platform launcher.
Use `run` for the GUI or `runCli` for interactive console commands.

## Wrapper provenance

Gradle 9.1.0 is the first release with official Java 25 runtime and toolchain
support according to the [Gradle compatibility matrix](https://docs.gradle.org/current/userguide/compatibility.html).
The scripts and wrapper JAR come from the official
[Gradle v9.1.0 repository](https://github.com/gradle/gradle/tree/v9.1.0).
The downloaded JAR was checked against Gradle's
[published wrapper SHA-256](https://services.gradle.org/distributions/gradle-9.1.0-wrapper.jar.sha256):

```text
76805e32c009c0cf0dd5d206bddc9fb22ea42e84db904b764f3047de095493f3
```

`gradle-wrapper.properties` pins the distribution URL and its
[published SHA-256](https://services.gradle.org/distributions/gradle-9.1.0-bin.zip.sha256),
which the wrapper checks when downloading the distribution.

## Increment integration

The course `add-gradle-support` branch is merged into this increment. Its starter
entry point and build defaults are adapted to Yapper: the reviewed Gradle 9.1.0
wrapper, Java 25 toolchain, and Jupiter 5.13.4 configuration are retained.
The application plugin also applies Java support. The Unix wrapper is executable.

Ordinary `run` launches the JavaFX GUI through `yapper.Launcher`.
The `test` task runs the JUnit tests under `src/test/java/yapper`, including
keyword search, parsing, dates, task-list operations, and persistence.
The A-Jar configuration is applied from `gradle/jar.gradle`; `jar` creates
`build/libs/yapper.jar`. See [JAR instructions](jar.md) for distribution and
execution. The JAR task bundles JavaFX runtime dependencies and desktop native libraries.
