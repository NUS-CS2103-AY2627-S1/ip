# Manual GUI and environment test plan

These checks cover JavaFX rendering, native window behavior, fonts, and input methods that the
JUnit suite does not exercise. All manual cases below are **not run** for this change. Record the
actual OS, Java version, display scaling, resolution, language, outcome, and screenshot for each run.
JUnit locale tests change the JVM locale; they do not change the OS language or exercise an IME.

## Setup

1. Use Java 25. Build the distributable with `./gradlew shadowJar` (Windows: `.\gradlew.bat shadowJar`).
2. Create a fresh temporary working directory. Start `java -jar /absolute/path/to/build/libs/jassabot.jar`
   from it, using the actual absolute JAR path. This isolates `data/jassabot.txt` from personal tasks.
3. For each environment below, run M-01 through M-07. Restart with the same temporary directory only
   for the persistence check; use a fresh directory when switching environments.

## Environment matrix

| Environment | Display settings | Language/input | Status |
| --- | --- | --- | --- |
| Windows | 1920 x 1080, 100% scaling | English | Not run |
| Windows | 1366 x 768, 150% scaling | Chinese, Chinese IME | Not run |
| macOS | Retina default scaling; resized window | English and Chinese IME | Not run |
| Linux desktop | 1920 x 1080, 100% and 200% scaling | English and Chinese input | Not run |

These are proposed test environments, not a claim that each platform has been validated.

## Cases

| ID | Actions | Expected result |
| --- | --- | --- |
| M-01 | Launch; resize down to the 400 x 420 logical-pixel minimum; maximize and restore. | Welcome appears once. Input and Send remain usable. Text, avatars, and controls do not overlap or disappear. |
| M-02 | Enter `todo read book` with Enter; enter `deadline return book /by 2024-02-29 1805` with Send; enter `event meeting /from 2024-02-29 1400 /to 2024-02-29 1600`. | Each action creates exactly one user message and one reply. Input clears and regains focus. Dates display in English. Bot and user avatars appear on opposite sides. |
| M-03 | Run `list`, `mark 1`, `unmark 1`, `find book`, `delete 1`, and `help`. | Replies agree with the console plan. Add, mark/unmark, and delete replies use their respective styles. Help is readable and wraps at narrow widths. |
| M-04 | Send blank input, `mark 999`, and `deadline bad /by 2023-02-29`; then `todo recovered`. | Errors appear without closing the window or changing tasks. The valid command still succeeds. |
| M-05 | Add a task description of at least 300 characters; add 30 short tasks; scroll up and then send `list`. | Long messages wrap without horizontal clipping. History remains scrollable. The newest reply becomes visible automatically. |
| M-06 | Switch to Chinese IME; compose and commit `todo 中文任务`; run `find 中文`; add `todo compare A \| B` and restart in the same directory. | Composition does not prematurely submit the command. Chinese text remains readable and searchable. `list` after restart preserves descriptions and completion status. |
| M-07 | Enter `bye`; attempt to type or press Send during the farewell. | Farewell renders, controls disable, and the window closes after approximately one second. No further task is saved. |

For a warning check, close the application, append `X | 0 | invalid` to the isolated saved file, and
restart. A readable warning should accompany the greeting while valid tasks remain available.

## Recording results

For each failure, record the case ID, exact inputs, expected and actual behavior, and screenshot.
Do not mark an environment as passed until every applicable case has been checked there.
