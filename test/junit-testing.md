# JUnit Tests

Run the tests and style checks using Java 25:

```shell
./gradlew test jacocoTestReport checkstyleMain checkstyleTest
```

Open `build/reports/tests/test/index.html` for test results and `build/reports/jacoco/test/html/index.html` for line and branch coverage. The coverage report excludes `potatobot.ui.gui` and the JavaFX `Launcher`, as GUI behavior requires separate interactive checks. The existing pure scrolling calculation tests still run.

The suite tests parsing, command execution and undo, response flags, task formatting and searching, capacity and index boundaries, persistence, startup and shutdown, and console input/output. Storage tests use JUnit temporary directories. Console launcher tests use separate Java processes with temporary working directories and save files; they never open the user's real task file. Their coverage is collected separately and included in the report. Tests that replace standard streams restore them afterward.

Coverage identifies code that ran; it does not prove correctness for every possible input. Some defensive assertion failure branches are intentionally untested because reaching them would require corrupting otherwise valid internal state.

The existing console scenario plan can also be replayed with:

```shell
python skills/test-ui/scripts/run_ui_tests.py
```
