# Jeff Test Plan

## Automated testing

Run the complete automated test suite from the project root:

```powershell
.\gradlew clean test checkstyleMain checkstyleTest
```

The tests cover:

- parsing valid and invalid command arguments;
- task completion status and display text;
- adding, retrieving, finding, deleting, and updating tasks;
- preservation of task details and status during updates;
- storage round trips, overwriting, empty lists, and missing files;
- command workflows and persistence through `Jeff`;
- text captured for display by `Ui`.

## Manual GUI testing

JavaFX rendering and window interaction are checked manually because they are
costly and brittle to automate in this project.

### Setup

1. Use Java 25.
2. Run `.\gradlew clean run` on Windows or `./gradlew clean run` on macOS/Linux.
3. Use an empty data directory or back up `data/jeff.txt` before testing.

### Main window and input

1. Confirm that the Jeff window opens without an exception.
2. Confirm that the welcome message and both profile images appear.
3. Enter `todo read book` and press Enter.
4. Confirm that the user command and Jeff response appear in separate bubbles.
5. Enter `todo buy groceries` using the Send button.
6. Confirm that the Send button performs the same action as pressing Enter.
7. Add enough tasks to exceed the visible area.
8. Confirm that the conversation scrolls to the latest response.

### Command workflow

1. Add a todo, deadline, and event.
2. Use `list` and confirm all three task types and task numbers are shown.
3. Mark and unmark each task and confirm its status changes.
4. Search using different letter cases and confirm matching tasks are shown.
5. Update each supported field and confirm other details remain unchanged.
6. Delete a task and confirm the remaining tasks are renumbered.
7. Enter invalid commands and confirm Jeff displays an error without closing.

### Persistence

1. Add and update several tasks.
2. Close Jeff normally.
3. Start Jeff again.
4. Use `list` and confirm that task types, details, order, and completion states
   match the previous session.

### Exit behavior

1. Enter `bye`.
2. Confirm that the farewell message appears.
3. Confirm that the window closes after a short delay.

### Environment coverage

When practical, repeat the launch, add, list, persistence, and exit checks on:

- Windows;
- macOS on Apple silicon;
- a display using scaling above 100%; and
- an operating system using a non-English locale.
