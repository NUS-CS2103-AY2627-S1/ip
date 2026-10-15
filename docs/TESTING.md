# Testing Gongrilla

## Automated checks

Use Java 25. From the repository root, run:

```powershell
.\gradlew.bat check jacocoTestReport
```

On macOS/Linux, use `./gradlew check jacocoTestReport`.

Reports:

- Test results: `build/reports/tests/test/index.html`
- Coverage: `build/reports/jacoco/test/html/index.html`

The suite uses temporary directories, not your `data/gongrilla.txt`.
Console integration tests launch the Java executable from the test JVM's
`java.home`, with a private working directory and a timeout. Gradle merges
those processes' JaCoCo data with the ordinary JUnit coverage report.

### Verified on 17 September 2026

- Windows, Java 25.0.4: **109 tests passed**, including Checkstyle and the
  configured coverage check.
- Non-GUI production code: **490/498 lines covered (98.4%)**.
- All production code, including GUI: **561/607 lines covered (92.4%)**.
- Console startup, EOF, explicit exit, corrupt-data startup, and persistence.
- English (`en_US`) and Chinese (`zh_CN`) JVM language settings in separate
  processes; Chinese text survives storage and dates retain English formatting.
- Malformed journal types, fields, flags, indices, encoding, and dates; errors
  identify the original line and do not rewrite the file.
- Save failure preserves tasks, suppresses success replies, and permits recovery.
- Leap days, invalid calendar/time values, midnight, and Unicode whitespace.
- Existing automated GUI checks cover help expansion, wrapping at two widths,
  biography expansion, and error styling. These are not visual sign-off.

Line coverage is not proof that every input or environment works. Remaining
non-GUI branch gaps are defensive assertions for internal invariants and the
storage path-without-a-parent case (the application uses `data/gongrilla.txt`).
Partial-write rollback and cross-process lock contention are not fault-injected; the suite covers same-process locks and simulated access denial. GUI launch failures and some input/scroll event paths remain outside automated
coverage. The error-handling update adds strict event ranges, duplicate detection, parameter validation, and journal write protection.

## Manual checks still to perform

These are a checklist, **not claims of completed testing**. Record the OS,
Java version, resolution, scaling, language, result, and any screenshot or bug
reference for each run. Use a disposable working directory/data file.

| Environment | Status |
| --- | --- |
| Windows, English desktop, 1920x1080 at 100% scaling | Not manually verified |
| Windows, 1366x768 at 125% and 150% scaling | Not manually verified |
| Windows, Chinese desktop with Chinese IME | Not manually verified |
| macOS, Java 25, Retina display | Not verified on that OS |
| Linux, Java 25, desktop display | Not verified on that OS |

JVM locale tests do not change the OS language, IME, fonts, or desktop settings.

### Appearance and keyboard interaction

1. Launch the GUI and resize from its smallest allowed window to maximized.
   Check that the input and Send button stay reachable and text does not clip.
2. Open About Gongrilla. Check the full name, position, paragraph wrapping,
   contrast, and closing text. Collapse it and confirm messages remain readable.
3. Enter `help`. Open every section with mouse and keyboard. Verify that long
   examples wrap, scroll normally, and remain readable at high display scaling.
4. Check the circular pictures and decorative frames for clipping, and verify
   that Chinese characters and emoji render legibly instead of empty squares.
5. Use Tab/Shift+Tab and Enter to navigate and submit a command. With Chinese
   IME, confirm that composing text is not accidentally submitted early.

### Commands and persistence

1. Submit `todo 买香蕉`, then `list`, `mark 1`, `unmark 1`, and `find 香蕉`.
   Confirm task text, completion state, and responses are understandable.
2. Submit `deadline report /by 29/2/2028 2359` and
   `event lunch /from 20/9/2026 1200 /to 20/9/2026 1300`.
   Check the displayed dates on both English and Chinese systems.
3. Try an unknown command, `mark 999`, `deadline report /by 31/2/2026`, and
   an event ending before it starts. Check the error appearance and help hint;
   confirm that no task was added or altered.
4. Create enough tasks/messages to require scrolling. Read old messages, expand
   help, and send another command. Check that scrolling remains controllable.
5. Restart from the same working directory. Check task order and completion.
   Delete a task, restart again, and verify it stays deleted.
6. In a disposable copy, corrupt a journal record and restart. Check that the
   error identifies the problem and the original file remains intact.
7. Enter `bye`: the console exits; the GUI currently displays a farewell.
   Check the actual behavior against the help text rather than expecting the
   GUI window to close automatically.
