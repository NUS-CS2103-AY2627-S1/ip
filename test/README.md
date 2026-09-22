# Testing Stewie

Use Java 25 for every build and application launch. On this macOS setup:

```sh
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk use java 25.0.3.fx-zulu
./gradlew check jacocoTestReport jacocoCoreReport
python3 test/run-ui-tests.py out/ui-test-session.txt
```

On Windows, select an installed JDK 25 with `JAVA_HOME` and use
`gradlew.bat check jacocoTestReport jacocoCoreReport`. The Python console-plan
runner currently requires the configured SDKMAN JDK path and POSIX shell and
permissions; it is not a native Windows test runner. The JUnit suite selects
`java.exe` on Windows and requires neither Python nor a shell.

`check` runs JUnit and Checkstyle. Reports are generated locally:

- JUnit: `build/reports/tests/test/index.html`.
- Complete coverage, including the untested GUI: `build/reports/jacoco/test/html/index.html`.
- Non-GUI coverage: `build/reports/jacoco/core/html/index.html`.
- Machine-readable coverage: the XML file alongside each report's HTML directory.
- Console sessions: `out/ui-test-session.txt`, including every input, actual
  output, comparison result, stderr, and exit status. Cases execute in the order
  written in the plan and stop on the first failure.

JaCoCo 0.8.14 provides [official Java 25 support](https://www.jacoco.org/jacoco/trunk/doc/changes.html).
The CLI tests launch actual child JVMs and merge their instrumentation with the
JUnit worker's coverage. Their data file is reset before each test task execution
to prevent old runs from inflating coverage. Run the complete suite, without a
`--tests` filter, when measuring coverage.

Verified on 2026-09-14 with macOS 26.5.1 and Zulu Java 25.0.3:

| Check | Result |
| --- | --- |
| JUnit | 67 passed; no failures, errors, or skips |
| Checkstyle | Main and test sources passed |
| Ordered console plan | All 15 sessions passed exact output comparisons |
| Non-GUI coverage | 468/472 lines (99.15%); 243/257 branches (94.55%) |
| Complete application coverage | 468/956 lines (48.95%); 243/370 branches (65.68%), including untested JavaFX code |

These are measured results for this revision and environment, not results for
the unexecuted native OS/display combinations below. Regenerate the reports
after further changes.

## Automated scope

| Area | Behaviors checked |
| --- | --- |
| Parser | Every command, unknown commands, null and missing input, whitespace, number boundaries and overflow, marker order, empty/repeated/unknown fields, update aliases, optional fields, search splitting |
| Models | Every supported date format, invalid dates and year boundaries, event ordering, task identity, unsafe descriptions, Unicode normalization, rendering, repeated marking and unmarking |
| Task list | All task types, order, snapshot isolation, searching, duplicate rejection, partial updates, metadata and completion preservation, invalid indices, deletion to empty, saved revisions, failed mutations |
| Storage | Exact serialization, all types/statuses, missing/empty files, malformed and duplicate records, line endings, UTF-8 and Burmese text, read-only recovery, external edits/creation/deletion, symbolic links, denied writes, temporary-file cleanup after success |
| Console | Real startup, EOF, bye, every command route, error recovery, warnings, persistence/restart, English and Burmese sessions, exact messages, numbering, counts, and standalone echo |

All storage and child-process tests use JUnit temporary directories. Console
stream and locale changes are restored in `finally` blocks. JUnit uses its default
sequential execution; do not enable parallel tests without coordinating every
test that changes global streams or locales. POSIX permission tests explicitly
skip on unsupported filesystems or when effective permissions are bypassed
(e.g., a privileged user). Symbolic-link tests currently run on POSIX filesystems.
A skipped test is not evidence of coverage on that platform.

The console plan's English expectations remain unchanged because no application
behavior was changed. JVM locale checks simulate formatting rules, not an entire
OS language change or a native input method. Burmese confirmations currently use
Burmese count digits (`၁`, `၂`); dates remain English and CLI task numbers use ASCII
input such as `mark 1`. Locale tests preserve that existing behavior.

## Coverage limits

The non-GUI report excludes only `stewie.ui.gui`, whose JavaFX scene construction,
event handlers, animations, images, focus, and rendering need a native toolkit.
The complete report retains those classes so the exclusion is visible. Their
shared parsing, task management, and storage logic is exercised through JUnit.

Do not chase 100% by weakening assertions or calling private methods reflectively.
The remaining non-GUI gaps are:

- Unused implicit constructors of static utility classes `Parser` and `Ui`.
- The all-missing-update fallback in `Stewie`, which valid parser output cannot
  reach after a valid task index and nonempty update have been required.
- Assertion-failure branches for invariants already enforced by constructors,
  plus redundant type checks after exact-class identity has been established.
- A cleanup exception after temporary-file deletion fails, which would require
  filesystem fault injection or a timing-dependent race.
- The non-POSIX permissions branch on this macOS filesystem. Run on Windows to
  exercise that filesystem behavior; a simulated locale does not cover it.

## Manual platform and display matrix

Universal release verification on 2026-09-16:

- Built `build/libs/stewie.jar` (approximately 41 MB) with Java 25. It contains
  Windows x64, macOS x64/ARM64, and Linux x64/ARM64 packages in separate entries.
- All 78 JUnit tests passed, including platform aliases, unsupported systems,
  missing packages, and a real child-JVM test of argument handling, working
  directory, Java version, and exit status. Main and test Checkstyle checks passed.
- All 15 ordered console sessions passed; the full input/output record is
  `out/universal-ui-session.txt`. Console behavior and expectations are unchanged.
- `python3 test/check-universal-jar.py` passed for all five packages, including
  their plain launchers, JavaFX modules, icons, resources, native OS formats,
  and CPU headers. Both existing platform archive checkers also passed.
- The same universal JAR opened the real Stewie stage on Apple Silicon macOS
  with Zulu Java 25.0.3. Both JVMs used `--limit-modules=java.se` to exclude the
  JDK's own JavaFX modules and require the bundled dependencies.
- The same JAR opened the real Stewie stage in both existing Ubuntu 22.04 test
  images using standard Temurin Java 25.0.4 and Xvfb. ARM64 ran natively in the
  Docker VM; x64 used emulation. Network access was disabled in both containers.
- These automated startup checks used a separate test agent to observe the
  real application's stage title and `isShowing()` state, then call JavaFX's
  normal exit API. All three launches ran in folders containing spaces, exited
  with status 0, and removed the extracted temporary runtime. The agent is not
  included in the release. JavaFX printed its existing unnamed-module warning.
- Windows x64 and Intel macOS native GUI launches remain **not run**. Full GUI
  command, persistence, keyboard, language, and scaling checks remain separate
  from these startup checks. Use the
  [universal release startup plan](ui-test-plan.md#universal-release-startup-checks)
  for those checks; a successful archive inspection is not a native launch test.

Windows release packaging verification on 2026-09-16:

- Built `stewie-windows-x64.jar` on macOS using Zulu Java 25.0.3 with
  `./gradlew build`. All 74 JUnit tests passed and Checkstyle was up to date.
- All 15 ordered console cases passed exact output comparisons; the full
  input/output record is generated at `out/windows-fix-ui-session.txt`.
- `python3 test/check-windows-jar.py` passed: the manifest selects the plain
  `stewie.ui.gui.Launcher`, Windows JavaFX classes and GUI resources are present,
  icon services are retained, and all 54 bundled DLLs have x64 PE headers.
  No macOS/Linux native libraries or platform classes are bundled.
- Both existing Linux archive checks also passed after the packaging change.
- A Windows host was unavailable. Windows 11 Pro N GUI startup, persistence,
  keyboard input, and display scaling remain **not run** for the new artifact.
  The reported older JAR fails before opening the GUI; that report is not a
  test result for this replacement artifact.

For a Windows retest, use the current universal `stewie.jar` or
`stewie-windows-x64.jar` from `./gradlew windowsJars`
(PowerShell: `.\gradlew.bat windowsJars`). The old host-only `stewie.jar` is not suitable.
Run the [Windows release startup checks](ui-test-plan.md#windows-release-startup-checks)
on the reported Windows 11 Pro N system with standard x64 Java 25, then record
the artifact checksum (`Get-FileHash .\stewie-windows-x64.jar -Algorithm SHA256`)
along with the OS/JDK details and observed results.

Linux release verification on 2026-09-16:

- Built both Linux JARs on macOS using Zulu Java 25.0.3. The build passed
  Checkstyle and all 74 JUnit tests; all 15 ordered console sessions passed.
- `python3 test/check-linux-jars.py` passed for both architectures.
- Launched each macOS-built JAR with `java -jar` in an Ubuntu 22.04.5 container
  running standard Temurin Java 25.0.4, GTK 3, Xvfb, and Openbox, without a separate
  JavaFX installation. Both displayed the visible "Stewie — your task studio"
  window and exited with status 0 after Alt+F4. ARM64 ran natively in Docker's
  Linux VM; x86-64 used emulation on the ARM64 Mac.
- Both launches printed JavaFX's warning about classes loaded from an unnamed
  module, which is expected with this fat-JAR distribution. Neither reported
  missing JavaFX runtime components or native-library failures.

These are automated startup checks, not the full manual desktop, command,
language, or display-scaling matrix below. Other distributions still need
their own native smoke tests.

Use a disposable project copy or launch directory so the real agenda stays safe.
Use a Java 25 build appropriate for each OS. Build Linux release JARs with
`./gradlew linuxJars` and choose `stewie-linux-x64.jar` for x86-64 or
`stewie-linux-aarch64.jar` for ARM64. These tasks replace the build host's JavaFX
dependencies with the target's JavaFX classes and native libraries.
Run `python3 test/check-linux-jars.py` after building to check both manifests,
bundled resources and JavaFX classes, icon services, and native ELF CPU types.
This archive check runs on macOS too; it does not replace a Linux GUI launch.
Run the [Linux release startup checks](ui-test-plan.md#linux-release-startup-checks),
then the existing
[GUI navigation and recovery checks](ui-test-plan.md#additional-gui-navigation-checks)
for each environment below. Record the actual OS/JDK versions and display scaling.

| Environment | Language | Resolution / scale | Status |
| --- | --- | --- | --- |
| macOS | English | 1440 × 900 logical workspace / Retina default | Not run manually |
| macOS | Burmese | Same display / OS language and Burmese keyboard enabled | Not run manually |
| Windows | English and Burmese | 1366 × 768 at 100%; 1920 × 1080 at 150% | Not run |
| Linux desktop | English and Burmese | 1920 × 1080 at 100%; available fractional scaling | Not run |
| Each available OS | English and Burmese | Resize to 960 × 640 minimum, 1180 × 760 default, and maximized | Not run |

For every matrix row:

1. Launch, enter all three task types, mark/unmark, update, search, delete, exit,
   and restart. Confirm the same task details and completion states survive.
2. Paste and type `todo စာအုပ် ဖတ်ရန်` using the native keyboard/input method.
   Check glyph shaping, combining marks, caret movement, deletion, clipboard,
   focus, Enter, and persistence. No missing-glyph boxes or corrupted characters.
3. Check sidebar, Help, Chat, My List, long descriptions, and dates at every
   listed size. No overlapping controls; scrolling reaches the last task and
   help entry; the portrait keeps its proportions. Verify minimum-size behavior
   when the effective desktop is smaller because of display scaling.
4. Use Tab, Shift+Tab, Space, Enter, mouse/trackpad, and window resizing. Verify
   focus remains visible and the composer is reachable. Check the existing
   delayed completion and stale-card scenarios while resizing and navigating.
5. Repeat storage-warning and failed-save checks with that OS's actual permission
   mechanism. Restore access and retry. Keep original bytes and live state after
   rejected changes; show one clear error and no false success message.
6. Record result, exact inputs, observed versus expected behavior, and screenshots
   for any failure. Do not mark an untested matrix row as passed.

Result template:

```text
OS/version and architecture:
JDK vendor/version:
OS language, keyboard, and JVM locale:
Display resolution, logical size, and scale:
Scenario and inputs:
Expected:
Actual:
PASS / FAIL / NOT RUN:
Evidence or issue:
```
