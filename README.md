# Epi project template

This is a project template for a greenfield Java project. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/Epi.java` file, right-click it, and choose `Run Epi.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see output similar to the following:
   ```
  ______       _
 |  ____|     (_)
 | |__   _ __  _
 |  __| | '_ \| |
 | |____| |_) | |
 |______| .__/|_|
        | |
        |_|
Meowdy! I'm Epi
Are you ready to tackle some purr-fectly good tasks today?
blah
Added: blah
list
   1. blah
bye
Meow for now. See you later!
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Code quality checks

Run the following commands from the project root to verify the Java coding
standard and automated tests:

```bash
./gradlew checkstyleMain checkstyleTest test
```

The code-quality improvements include shared command processing for the CLI
and GUI, use of collection abstractions, and named constants for GUI layout
values. Keeping these checks in the normal development workflow helps detect
style violations and regressions early.

## Continuous integration

[![Java CI](https://github.com/naymin-gif/ip/actions/workflows/gradle.yml/badge.svg?branch=master)](https://github.com/naymin-gif/ip/actions/workflows/gradle.yml)

The [Java CI workflow](.github/workflows/gradle.yml) runs on branch pushes,
pull requests, and manual dispatches from GitHub's **Actions** tab.
It validates the Gradle wrapper, installs Java 25, and runs:

```bash
./gradlew check shadowJar --console=plain --no-daemon
```

`check` includes JUnit and both Checkstyle checks. `shadowJar` verifies that
the distributable `build/libs/epi.jar` can be built. The matrix covers
Ubuntu 24.04, Windows 2025, and Intel macOS 15 to match the current x86-64
JavaFX dependencies. Windows also runs the isolated console regression
plan through the existing `test-ui` runner, which uses `gradlew.bat`.

To inspect a result, open **Actions > Java CI**, select the run, and open
each operating-system job. Test reports and the Windows console transcript
are retained as `test-reports-<runner>` artifacts for seven days, including
when checks fail. Fix failing checks before merging a branch. Actions are
pinned to reviewed commit hashes and use read-only repository permissions.

CI does not run the desktop-dependent `guiSmokeTest`, publish releases, or
upload JARs to a release. Continue the manual GUI and released-JAR smoke
tests in [the GUI test plan](tests/gui-test-plan.md); a passing build alone
does not establish GUI compatibility on every operating system or CPU.
