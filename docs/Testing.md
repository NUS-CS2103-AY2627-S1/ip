# Testing Woofer

Use Java 25. On macOS with SDKMAN:

```bash
sdk use java 25.0.3.fx-zulu
./gradlew test checkstyleMain checkstyleTest
```

On Windows, use `gradlew.bat` with `JAVA_HOME` pointing to Java 25.
Open `build/reports/tests/test/index.html` for individual results. To force a fresh execution after a previously successful run, use `./gradlew test --rerun-tasks`.

## Automated coverage

| Suite | Behaviours checked |
| --- | --- |
| `ParserTest` | All command types, whitespace normalization, valid dates and leap days, Unicode and punctuation, missing/repeated/misordered parameters, unknown commands, task number boundaries, unsafe input |
| `TaskTest` | All task types and icons, idempotent status changes, exact date display, Chinese default locale, invalid event ranges |
| `TaskListTest` | Insertion/deletion order, invalid indices, capacity and insertion boundaries, immutable list snapshots, empty searches, case-insensitive matching under a Turkish locale |
| `StorageTest` | First-run missing file, every task type/status, UTF-8, blank lines, exact saved records, repeated/empty saves, corrupted records and encoding, overflow protection, repair/reload, failed replacement cleanup |
| `WooferServiceTest` | End-to-end command processing, persisted state across restart, search results, every undo operation, repeated status commands, rejected commands preserving undo, full lists, load/save warnings and recovery, exit metadata |
| `UiTest` | Terminal input including EOF and Unicode, task output, search results, status/count output, greeting, errors and warnings |
| `WooferTest` | Real CLI JVM entry point, recovery after an invalid command, persistence, stopping at `bye`, EOF, corrupt-file warnings |

File tests use JUnit temporary directories. CLI tests start a separate JVM using the test runtime's Java installation and a temporary working directory. Stream and locale tests restore global settings in cleanup blocks; keep these tests sequential if changing JUnit's parallel-execution configuration.

Coverage here describes tested behaviours, not a measured line/branch percentage. GUI rendering, OS-specific permission enforcement, and filesystem support for atomic moves still require environment-specific checks. The automated failure cases use controlled filesystem fixtures rather than relying on machine-specific permission settings.

## Manual GUI checklist

These checks are **not recorded as performed**. Use a disposable copy of the application with separate test data when checking file failures.

1. Launch with `./gradlew run`. Confirm the paw badge and greeting appear, the input has focus, and Enter and Send both submit commands.
2. Add `todo walk dog`, `deadline buy food /by 2028-02-29`, and `event holiday /from 2026-12-31 /to 2027-01-02`. Check wrapping and legibility of replies.
3. Run `list`, `find dog`, `mark 1`, `unmark 1`, `delete 1`, and `undo`. Confirm displayed results and ordering. Restart and check saved tasks.
4. Submit `todo`, `mark 0`, and an event with equal dates. Check the red attention card, useful error text, and retained input for correction.
5. Resize from minimum size to a large window, then add a long description and enough messages to scroll. Check wrapping, scrollbar reachability, auto-scroll, and composer visibility.
6. Type `bye`. Confirm the reply appears and command entry is disabled.
7. In the disposable data folder, introduce a malformed record and restart. Confirm the startup warning and that adding a task does not overwrite the original file.
8. Where available, repeat the GUI checks on macOS, Windows, and Linux; small and high-DPI displays; and English and Chinese OS language settings. Record actual platform, resolution/scaling, Java version, result, and any issue below.

| Date | Platform / language | Resolution / scaling | Java version | Result / issue |
| --- | --- | --- | --- | --- |
| Not run | — | — | — | Manual verification pending |
