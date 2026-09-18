# GUI Manual Test Plan

Use this plan for JavaFX behavior that is intentionally omitted from the automated
JUnit coverage target. Build and launch the application with Java 25 before each
test session:

```text
gradlew.bat run
```

On macOS or Linux, use `./gradlew run` instead. Start with a disposable or backed-up
`data/cookie.txt` because the commands below change saved tasks.

## Environment matrix

Record the environments actually tested. A row should be marked `Pass`, `Fail`, or
`Not available`; do not report an untested configuration as passing.

| Operating system | Display configuration | OS language | Result | Notes |
| --- | --- | --- | --- | --- |
| Windows | 1280 x 720, 100% scaling | English | | |
| Windows | 1920 x 1080, 150% scaling | Chinese | | |
| macOS Apple Silicon | Default and smallest practical window | English | | |
| Linux x64 | Default and smallest practical window | English | | |

## Test case 1: Launch and initial layout

1. Launch Cookie with no saved task file.
2. Confirm the window opens without a blank scene or exception.
3. Confirm the input field, conversation area, Cookie avatar, and startup layout are visible.
4. Resize the window to its smallest permitted size and then maximize it.

Expected: Controls remain usable, messages do not overlap, and no content is clipped
without an available scroll bar.

## Test case 2: Successful conversation flow

1. Enter `todo buy milk` and submit it using the keyboard.
2. Enter `list`.
3. Enter `mark 1`.

Expected: Each user message is right-aligned, each Cookie response is left-aligned,
the avatar is circular, the input clears after submission, and the marked task shows
`[X]`.

## Test case 3: Error presentation

1. Enter `unknown`.
2. Enter `deadline invalid /by 2026-02-30`.

Expected: Both responses use the visually distinct error style, while the application
continues accepting commands.

## Test case 4: Scrolling and long content

1. Add enough tasks to make the conversation taller than the window.
2. Add a task with a long description and another containing Chinese characters.
3. Enter `list`.

Expected: The view follows the newest response, older messages remain reachable by
scrolling, long text wraps within the window, and Unicode text is readable.

## Test case 5: Startup warning and persistence

1. Close Cookie after adding a valid task.
2. Add one malformed nonblank line to `data/cookie.txt` using a text editor.
3. Relaunch Cookie and enter `list`.

Expected: A startup warning identifies the malformed line, the valid task is retained,
and the warning does not prevent read-only commands.

## Test record

For each session, record the date, tester, Java version, operating system, display
resolution/scaling, OS language, test-case results, and any screenshots or issue links.
