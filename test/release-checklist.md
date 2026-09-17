# Rei v0.2 release verification

Validated on Windows with Java 25.0.4 on 17 September 2026.

- Clean Gradle build: `gradlew clean test checkstyleMain checkstyleTest shadowJar`.
- All 33 JUnit tests pass, including JavaFX interaction tests.
- All 14 recorded console sessions pass using the project-local test-ui runner.
- Checkstyle passes for production and test code.
- The packaged JAR starts from a separate folder without existing task data.
- GUI smoke test: add a todo, add a deadline, list both, and exit using `bye`.
- Task data is created in the launch folder's `data/rei.txt`.
- The user guide covers all commands and includes the complete GUI window in `docs/Ui.png`.

## Reproduce the console tests

After compiling with Java 25, run:

```text
python .codex/skills/test-ui/scripts/run-ui-tests.py test/ui-test-plan.md
```

The runner prints each command session and compares it to the expected output.
The generated `data/ui-test.txt` is test data and is ignored by Git.

## Cross-platform test drive

The JAR bundles JavaFX native libraries for Windows, macOS, and Linux.
Only Windows execution has been verified locally. Ask someone using macOS or
Linux to download the release and run `java -jar rei.jar` with Java 25 from an
empty writable folder, add a task, exit, and restart to verify persistence.
Record their OS, CPU architecture, Java version, and result before claiming
cross-platform execution has been verified.
