---
name: run-gradle-check
description: Run and troubleshoot the complete Gradle verification suite for this Windows project after code changes or when tests or Checkstyle need checking.
---

# Run Gradle Check

Use Java 25 and run the Gradle wrapper from the project root. On Windows, set
`JAVA_TOOL_OPTIONS` so every Java process, including the Gradle daemon, uses a
writable Unix-domain socket directory:

```powershell
$env:JAVA_TOOL_OPTIONS = '-Djdk.net.unixdomain.tmpdir=C:\NUS\CS2103T\ip\build'
.\gradlew.bat check
```

Prefer `JAVA_TOOL_OPTIONS` over `GRADLE_OPTS` for this workaround because the
setting must reach the Gradle daemon as well as the wrapper process.

Treat `BUILD SUCCESSFUL` as the completion condition. The `check` task runs the
JUnit tests and the main and test Checkstyle tasks configured by this project.

If Gradle reports that it cannot connect to a daemon or establish a loopback
connection, stop existing daemons and retry once with the same environment:

```powershell
.\gradlew.bat --stop
.\gradlew.bat check
```

If verification fails after Gradle tasks have started, inspect the named task
and its generated report. Fix test or Checkstyle failures rather than treating
them as Gradle infrastructure errors. Report any verification limitation if
the retry fails before project tasks can run.
