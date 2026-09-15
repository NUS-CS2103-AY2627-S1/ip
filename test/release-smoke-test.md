# Release smoke test

## Environment

Tested on September 14, 2026, on Apple Silicon macOS with Java 25.0.3.
The JAR was copied into an isolated folder and launched with `java -jar`.
The test runtime contained `java.se` and `jdk.unsupported`, but no JavaFX
modules or native JavaFX libraries. No project task data was used.

When creating a stripped-down runtime with `jlink`, include
`java.se,jdk.unsupported`. Omitting `jdk.unsupported` causes JavaFX rendering
to fail with `NoClassDefFoundError: sun/misc/Unsafe`; that is an incomplete
test runtime, not a missing library in the application JAR.

## GUI checks

| Check | Expected result | Result |
| --- | --- | --- |
| Open the standalone JAR | Complete CHOO window, avatars, welcome, and composer render | Pass |
| `todo Read lecture notes` | One todo is created and saved | Pass |
| `deadline Submit project report /by 2026-09-18 1800` | Deadline displays as `Sep 18 2026, 6:00PM` | Pass |
| `event Team project meeting /from Friday 2pm /to 4pm` | Event preserves both schedule fields | Pass |
| `mark 1`, `unmark 1`, `mark 1` | Todo changes from done to not done and back | Pass |
| `find project` | Only the deadline and event appear | Pass |
| `todo` | Red error reply explains the missing description | Pass |
| `deadline Bad date /by 2026-02-30` | Impossible date is rejected without adding a task | Pass |
| `mark 99` | Invalid position is rejected without changing tasks | Pass |
| `sort` | Deadline moves first; todo and event retain relative order | Pass |
| `bye`, reopen JAR, `list` | Window closes; all three tasks reload in sorted order | Pass |
| Resize from 440 to 520 points wide | Text wraps; composer remains accessible | Pass |

After restart, the final task list was:

```text
1.[D][ ] Submit project report (by: Sep 18 2026, 6:00PM)
2.[T][X] Read lecture notes
3.[E][ ] Team project meeting (from: Friday 2pm to: 4pm)
```

`docs/Ui.png` was refreshed on September 15 using a separate sample list of
nine realistic tasks in a 640-by-600-point window. All task entries fit on
one line, and the capture includes the complete GUI and title bar.
It is an actual screen capture, not a mockup or edited rendering.

## Automated checks and limitations

- `./gradlew clean check shadowJar` passes with Java 25, including 39 JUnit
  tests, Checkstyle, coverage thresholds, and release-artifact verification.
- All eight recorded console sessions also pass directly against the JAR.
  These cover deletion, escaping, persistence, and further invalid inputs.
- GitHub Actions passes on Linux, macOS, and Windows. These build checks do
  not replace visual GUI testing on Windows, Linux, or an Intel Mac.
- For keyboard-based GUI automation, the user must leave input idle. A
  focus change can send keystrokes to another app. Stop immediately if the
  expected response does not appear; never continue a failed test session.
