# Week 6 verification

Implemented increments: `A-BetterGui` and `A-MoreTesting`.

## Automated checks

Use Java 25, then run:

```powershell
.\gradlew.bat test checkstyleMain checkstyleTest shadowJar
```

`NoriWorkflowTest` covers mixed task types through a restart; mark/unmark/delete
and renumbering; invalid commands leaving the data unchanged; whitespace and
Unicode; case-sensitive search; empty lists; corrupted storage; recovery after
repair; blocked data directories; and failed saves for every mutating command.
All filesystem checks use JUnit temporary directories.

`ParserTest` also covers missing event fields, free-text event endpoints, and leap
year boundaries. `StorageTest` rejects blank stored descriptions and endpoints.

Verified on Windows with Java 25.0.4.1: **55 tests passed**, both Checkstyle tasks
passed, and the fat JAR built successfully. Fifteen tests were added for Week 6.

The running JAR was checked in a separate demo folder: Enter and Send submission,
all three task types, error highlighting and input correction, long help output,
normal and maximized layouts, and task persistence after reopening. The final
full-window screenshot is saved as `Ui.png`. Minimum-size resizing remains a
manual checklist item below.

## Manual GUI checks

Run the built JAR with Java 25 from a separate folder so demonstration tasks do
not touch personal data. Exercise the following:

- First launch: the title says Nori, the input is focused, and `list` explains an empty list.
- Add a to-do, a deadline, and an event; `list` shows all three.
- Submit with both Enter and Send; the input regains focus after submission.
- Submit `event /from Monday /to Tuesday`: a labelled error appears, with the
  invalid command selected for correction; a corrected command works.
- Resize to the minimum window size and a larger size: replies wrap, the input
  remains usable, and long conversations scroll without horizontal clipping.
- Use `help` to check a long response and automatic scrolling.
- Mark a task, close and reopen, then `list`: the task and its status persist.
- Capture one full Nori window, including its title bar, as `docs/Ui.png`.

## Release checklist

Build the release from a clean checkout with Java 25 using
`./gradlew clean test checkstyleMain checkstyleTest shadowJar` (use
`.\gradlew.bat` on Windows). Publish `build/libs/nori.jar` as the single JAR
asset in the GitHub release.

Serve the user guide with GitHub Pages from `master` and `/docs`. Check the
published page and `Ui.png` after deployment.

The Windows GUI smoke test is complete. A teammate should still test the released
JAR on another OS with Java 25, including creating tasks and reopening the app.
