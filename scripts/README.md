# Duchess project scripts

These small wrappers make the common project workflows discoverable and give
Windows and POSIX environments equivalent entry points. They delegate to the
Gradle wrapper, so the project uses the pinned Gradle version rather than a
developer's globally installed Gradle version.

| Workflow | POSIX | Windows |
| --- | --- | --- |
| Start the JavaFX GUI | `scripts/run-gui.sh` | `scripts\\run-gui.bat` |
| Start the CLI | `scripts/run-cli.sh` | `scripts\\run-cli.bat` |
| Run checks and JUnit tests | `scripts/test.sh` | `scripts\\test.bat` |
| Run the console UI plan | `scripts/test-ui.sh` | `scripts\\test-ui.bat` |

Run the scripts from any directory. Each script changes to the repository root
before invoking Gradle, and extra arguments are forwarded to Gradle where that
is useful, for example `scripts/run-cli.sh --info`.
