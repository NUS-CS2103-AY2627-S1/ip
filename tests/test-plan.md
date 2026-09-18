# Remy Test Plan

## Sorting feature

### Scope

This plan verifies the `sort /by date [/order asc|desc]` command without changing the behavior of existing commands.
Automated JUnit tests cover parsing, ordering logic, persistence, and command integration. The manual checks focus on
the JavaFX interaction and user-visible output.

### Environment

- Java 25
- Application started with `./gradlew run`
- A backed-up or disposable `data/remy.txt`

### Automated coverage

| Area | Test class | Coverage |
| --- | --- | --- |
| Parsing | `ParserTest` | Default ascending order, explicit descending order, whitespace, invalid syntax, and keyword boundary |
| Ordering | `TaskListTest` | Mixed task types, both directions, date-only endpoints, stable ties, todo placement, completion independence, and later additions |
| Integration | `RemyTest` | Exact list output, canonical indices, persistence, find ordering, empty-list saving, invalid-command safety, and non-automatic re-sorting |

### Manual test cases

#### SORT-01: Default ascending order

1. Add deadlines and events in nonchronological order, followed by at least one todo.
2. Enter `sort /by date`.
3. Verify dated tasks appear earliest first and todos appear last.
4. Enter `list` and verify it shows the same order and task numbers.

#### SORT-02: Explicit descending order

1. Enter `sort /by date /order desc` with a mixed task list.
2. Verify dated tasks appear latest first.
3. Verify todos remain last rather than moving to the front.

#### SORT-03: Date-only endpoints and stable ties

1. Add a date-only deadline and an event starting at `00:00` on the same date.
2. Enter `sort /by date`.
3. Verify the two tasks retain their previous relative order.
4. Add other timed tasks on that date and verify the midnight tasks appear before them in ascending order.

#### SORT-04: Canonical task numbers

1. Sort a list that changes the positions of at least two tasks.
2. Enter `mark 1`, `unmark 1`, or `delete 1`.
3. Verify the command affects the task displayed as number 1 by the full sorted list.

#### SORT-05: Persistence

1. Sort a mixed task list.
2. Close and restart Remy.
3. Enter `list` and verify the sorted order is preserved.

#### SORT-06: Later additions

1. Sort dated tasks in ascending order.
2. Add a new task with an earlier date.
3. Enter `list` and verify the new task was appended rather than automatically inserted chronologically.

#### SORT-07: Invalid syntax

1. Try each of these commands: `sort`, `sort /by name`, `sort /by date /order`, and
   `sort /by date /order ascending`.
2. Verify each produces exactly:

   ```text
   Invalid sort command.
   Use: sort /by date [/order asc|desc]
   Example: sort /by date /order asc
   ```

3. Enter `list` and verify the order was not changed.

#### SORT-08: Unknown keyword prefix

1. Enter `sorter /by date`.
2. Verify Remy shows its existing unknown-command response rather than the sort guidance.

#### SORT-09: Empty list

1. Start Remy with an empty task list.
2. Enter `sort /by date`.
3. Verify Remy displays `There are no tasks in your list yet.`
4. Verify an empty task file can be created by the valid sort operation.

#### SORT-10: Find behavior

1. Sort a list containing multiple tasks that match the same search keyword.
2. Use `find KEYWORD`.
3. Verify matches follow their relative order in the canonical sorted list.
4. Verify find results retain their existing filtered-result numbering behavior.

#### SORT-11: Save failure

1. Run Remy with a task file location that cannot be written.
2. Enter a valid sort command.
3. Verify the saving-error message is shown before the current in-memory sorted list.
4. Verify the in-memory order remains sorted for the session.

## Task file recovery

### Scope

This plan verifies that environmental storage problems do not crash Remy or silently hide lost persistence.
Automated JUnit tests cover missing files, invalid records, non-UTF-8 content, directory/file mismatches, and failed
writes. The manual checks focus on the recovery guidance shown in the JavaFX interface.

### Manual test cases

#### STORAGE-01: Missing task file

1. Start Remy after moving `data/remy.txt` to a temporary backup location.
2. Verify Remy starts with an empty task list and no error message.
3. Add a task and verify Remy creates a new `data/remy.txt` containing that task.

#### STORAGE-02: Invalid task data

1. Add an invalid line between two valid task records in `data/remy.txt`.
2. Start Remy and verify the greeting identifies the invalid line number.
3. Enter `list` and verify both valid tasks were recovered.
4. Change a task and verify the invalid line is removed from the saved file.

#### STORAGE-03: Read access denied

1. Remove read permission from a disposable task file and start Remy with that path.
2. Verify the greeting identifies the path, explains that access was denied, and states that an empty list was used.
3. Restore the original file permission after the test.

#### STORAGE-04: Write failure

1. Start Remy with a disposable task-file path whose parent is not writable.
2. Enter a valid task-changing command.
3. Verify Remy identifies the path, suggests checking permissions, and says the change is session-only.
4. Enter `list` and verify the in-memory change is still present.

## Task data validation

### Scope

This plan verifies that Remy rejects impossible dates, empty or reversed event ranges, and duplicate task details.
Automated JUnit tests cover parsing, task invariants, task-list uniqueness, saved-file recovery, and complete command
handling.

### Manual test cases

#### VALIDATION-01: Event endpoint order

1. Enter an event whose start and end are the same date and time.
2. Verify Remy explains that the event must start before it ends and does not add the event.
3. Repeat with a start later than the end and verify the same behavior.
4. Enter an event whose start is earlier than its end and verify it is added.

#### VALIDATION-02: Duplicate task details

1. Add a task, then enter the same task command again.
2. Verify Remy explains that the task already exists and `list` contains only one copy.
3. Mark the task as done and try to add it again; verify it is still rejected as a duplicate.
4. Change the task type or a date endpoint and verify the distinct task can be added.

#### VALIDATION-03: Non-existent dates and times

1. Try to add a deadline dated `30/2/2026` and verify Remy rejects it as invalid.
2. Try to add an event containing `29/2/2025` and verify Remy rejects it as invalid.
3. Try a time of `24:00` and verify Remy rejects it as invalid.
4. Add a task dated `29/2/2024` and verify the valid leap day is accepted.

#### VALIDATION-04: Invalid saved values

1. Add duplicate records, a non-existent date, and a reversed event to a disposable task file.
2. Start Remy and verify the greeting reports the rejected line numbers.
3. Enter `list` and verify only valid unique tasks were recovered.

## Command format validation

### Scope

This plan verifies that malformed command structure produces actionable errors without executing partial commands.
Automated JUnit tests cover whitespace, command boundaries, missing and repeated parameters, parameter order, numeric
indices, unexpected arguments, and punctuation in free-form descriptions.

### Manual test cases

#### FORMAT-01: Command whitespace

1. Enter a valid command with a leading space, a trailing space, or two spaces between command parts.
2. Verify Remy asks for one space between parts and no outer spaces.
3. Enter the correctly spaced command and verify it succeeds.

#### FORMAT-02: Missing essential parameters

1. Omit the description or date parameter from each task-creation command.
2. Omit the keyword from `find` and the task number from each index command.
3. Verify each response identifies the missing information and no command is partially executed.

#### FORMAT-03: Repeated or misordered parameters

1. Enter a deadline with `/by` twice and verify Remy rejects the command.
2. Enter events with repeated `/from`, repeated `/to`, and `/to` before `/from`.
3. Verify each response shows the expected command format and no task is added.

#### FORMAT-04: Unexpected characters and arguments

1. Try `mark +1`, `delete #1`, `list extra`, and a misspelled command keyword such as `todoing`.
2. Verify each command is rejected without changing the task list.
3. Add a task whose description contains normal punctuation and verify the description is preserved.

## Coverage and platform compatibility

### Scope

The automated suite covers testable command, parser, storage, task, console UI, and application behavior. JaCoCo
generates an HTML report at `build/reports/jacoco/test/html/index.html` and makes `check` fail below 95% line coverage
or 90% branch coverage. JavaFX classes are excluded from those thresholds because their FXML lifecycle, native
toolkit, focus behavior, and rendered layout require a graphical environment. Their pure viewport calculation remains
covered by `DialogBoxTest`.

### Manual test matrix

#### COMPAT-01: Supported desktop operating systems

1. On Windows 11, macOS, and Ubuntu, install Java 25 and build the application with `./gradlew clean shadowJar`.
2. Start the packaged JAR, add each task type, mark and unmark a task, sort and find tasks, then exit.
3. Restart the application and verify all changes and completion states were preserved.
4. Verify startup, task-file paths, and line endings do not produce platform-specific errors.

#### COMPAT-02: Window size and display scaling

1. Test at 1280x720, 1920x1080, and 2560x1440, including 100%, 150%, and 200% display scaling where available.
2. Resize the window to its minimum dimensions and then maximize it.
3. Enter short and long commands until the conversation scrolls beyond one screen.
4. Verify controls remain reachable, text and images are not clipped, messages wrap correctly, and scrolling follows
   the latest response.

#### COMPAT-03: OS language, locale, and Unicode text

1. Run the application with the OS language and regional format set to English, then Chinese.
2. Add tasks with Chinese descriptions, punctuation, and valid numeric and English-text dates.
3. Restart the application and verify the UTF-8 descriptions are preserved exactly.
4. Verify command keywords remain consistent and displayed dates remain unambiguous regardless of the OS locale.
5. Enter a command containing a full-width or non-breaking space and verify Remy rejects the unsupported spacing with
   actionable guidance rather than executing a partial command.

#### COMPAT-04: JavaFX interaction and lifecycle

1. Launch the application, verify the input field receives focus, and send commands with both Enter and the button.
2. Enter a blank message and verify no empty user bubble is added while Remy displays an error response.
3. Enter `bye now`, `Bye`, and a `bye` command with leading or trailing whitespace; verify each error is displayed and
   the window remains usable.
4. In a new session, enter `bye` and press Enter; verify every Remy window closes immediately, without a confirmation
   or visible `Chat ended` state, and the process terminates without a native-toolkit or shutdown error.
5. Relaunch Remy, enter `bye`, and click Send; verify the same immediate, clean shutdown.
6. Reopen the application repeatedly and verify no native-toolkit or shutdown error is shown.

## Profile picture display

### Scope

This plan verifies that the user and Remy profile pictures have consistent dimensions, rounded corners, and centered
cropping without changing chat behavior. Automated JUnit tests cover the viewport calculations, while manual checks
cover the rendered JavaFX appearance.

### Automated coverage

| Area | Test class | Coverage |
| --- | --- | --- |
| Center cropping | `DialogBoxTest` | Landscape, portrait, and square source-image dimensions |

### Manual test cases

#### PROFILE-01: Consistent dimensions

1. Start Remy and enter any valid command.
2. Verify the profile pictures beside the user's message, Remy's response, and Remy's initial greeting are square.
3. Verify all three profile pictures have the same width and height.

#### PROFILE-02: Rounded corners and shadow

1. Inspect both the user and Remy profile pictures.
2. Verify each picture has visibly rounded corners rather than sharp corners or a circular crop.
3. Verify the existing drop shadow follows each rounded picture and remains visible outside its edges.

#### PROFILE-03: Centered crop without distortion

1. Compare the displayed user profile picture with `src/main/resources/images/User.png`.
2. Verify the longer horizontal dimension is cropped evenly from both sides.
3. Verify the face is not stretched and there are no empty bands inside the square.
4. Verify Remy's already-square source image remains centered and undistorted.

#### PROFILE-04: Alignment and window resizing

1. Enter enough commands to show multiple user and Remy messages, including at least one wrapped message.
2. Resize the window between its minimum and preferred widths.
3. Verify all profile pictures remain square, equal in size, and aligned with the top of their message bubbles.
4. Verify user pictures stay on the right and Remy pictures stay on the left.
