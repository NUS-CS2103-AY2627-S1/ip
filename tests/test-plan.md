# Serina Test Plan

## Flexible Multi-Term Search

Run each test with a clean save file unless the test supplies its own setup commands.

### Find Partial Keywords In Any Order

Input:

```text
todo Submit Project report
todo Draft project slides
todo Repair printer
find REP PROJ
```

Expected search output:

```text
Here are the matching tasks in your list:
1.[T][ ] Submit Project report
```

### Require Every Keyword In One Description

Input:

```text
todo read book
todo buy bread
find book bread
```

Expected search output:

```text
Here are the matching tasks in your list:
```

### Allow One-Character Keywords

Input:

```text
todo read book
todo buy lunch
find b
```

Expected search output:

```text
Here are the matching tasks in your list:
1.[T][ ] read book
2.[T][ ] buy lunch
```

### Reject A Missing Query

Input:

```text
find
```

Expected output:

```text
Sorry captain, find needs a keyword.
```

### Preserve Stored Tasks

After running any valid `find` command, restart Serina and run `list`.

Expected behavior: all tasks, statuses, dates, and ordering are unchanged.

## More Error Handling

### Normalize harmless whitespace

Enter `  todo   inspect engines  `, followed by `list`. The task is accepted and displayed without the outer
whitespace.

### Reject malformed date parameters

Verify that a deadline containing two `/by` parameters and an event containing `/to` before `/from` show errors
without changing the task list. Verify that `2026-02-30` and an event with equal start and end dates are rejected.

### Reject normalized duplicates

Add `todo Inspect engines`, then enter `todo   inspect   ENGINES`. Serina should identify the existing task number
and retain one task.

### Recover after reaching capacity

Fill the roster with 100 unique tasks. The next addition should be rejected without exiting. Delete one task and
verify that another unique task can then be added.

### Preserve data after a save failure

Make the save location unwritable, then try adding, deleting, marking, and unmarking a task. Each command should
report that it was not applied. Restore access and verify that both the displayed list and save file contain the
original data.

### Protect an invalid save file

Place an invalid record on line 2 of the save file and start Serina. Serina should identify line 2, keep `help` and
`bye` usable, and reject task-data commands. Verify that the invalid file remains unchanged.

## More Testing

Run the portable automated suite with Java 25 using `./gradlew test jacocoTestReport`. The HTML coverage report is
generated at `build/reports/jacoco/test/html/index.html`. Run display-dependent JavaFX checks separately using
`./gradlew guiTest`.

### MT-01: Supported operating systems

Run `./gradlew check javadoc shadowJar jacocoTestReport` on current Windows, macOS, and Linux installations.
Confirm all portable tests and quality checks pass and the packaged JAR is produced. Record the OS version and CPU
architecture. The GitHub Actions operating-system matrix performs the same portable checks automatically.

### MT-02: Window sizes and display scaling

Launch the GUI at 1366x768 and 1920x1080 screen resolutions, plus a high-resolution display when available. Repeat
at 100%, 150%, and 200% display scaling where the OS supports those settings. Test the minimum window size, default
size, maximized window, and repeated shrinking after expansion. Confirm Help, Send, and the command text remain
visible; messages wrap without horizontal clipping; and the transcript remains readable.

### MT-03: English and Chinese environments

Launch Serina once with an English OS language and once with a Chinese OS language. Enter Chinese descriptions,
emoji, and dates through the normal input method. Confirm Chinese input-method composition can be completed without
submitting an unfinished command, dates remain in Serina's documented English display format, and saved tasks
survive a restart without damaged characters.

### MT-04: Long and maximum-size conversations

Enter a long command, a long word without spaces, several multiline responses through `help` and `list`, and a list
containing 100 tasks. Confirm input focus remains usable, response text wraps, errors retain their visible heading,
and scrolling reaches both the beginning and end of every response.

### MT-05: Storage environments

Test first launch, restart, a malformed save file, a location with denied access, restored access, and a path whose
folders contain spaces and Chinese characters. Confirm Serina never overwrites an invalid file, explains failures,
keeps Help and exit available after a load failure, and resumes normal operation after the storage problem is fixed
and the application is restarted.

### MT-06: Exit the graphical application

Launch with `./gradlew run`, enter `bye`, and submit with Enter. Confirm the chat window closes and Gradle reports
`BUILD SUCCESSFUL` and returns to the terminal prompt. Repeat using Send and with surrounding whitespace in `bye`.
Confirm `bye later` shows a format error and keeps the window open. Repeat `bye` after starting with an invalid save
file. An idle Gradle build daemon may remain available for later builds; the application process must terminate.

### Environment-dependent limitations

Permission denial, disk exhaustion, interrupted processes, and filesystems without atomic-move support are manual
checks because they cannot be reproduced reliably on every development machine. Automated tests use temporary
directories and injected save failures to verify the corresponding application rollback behavior deterministically.
