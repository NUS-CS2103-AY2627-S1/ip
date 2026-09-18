# Code quality backlog

Findings from reviewing `src/main/` against the CS2103T
[Code Quality guidelines](https://nus-cs2103-ay2627-s1.github.io/website/se-book-adapted/chapters/codeQuality.html),
ordered by priority. We fix one stand-alone item per commit. Line numbers
drift as commits land — treat them as a starting point, not gospel.

This file is a working note for the iterative refactoring effort; it is not
part of the graded product.

## Done

- **`Storage.parseTask` did too much** — 53-line method, hardcoded `"T"/"D"/"E"`
  tags duplicating `TaskType`, magic field indices. Split into
  `parseTask` + `buildTask`; added `TaskType.fromTag` /
  `TaskStatus.fromFileFlag` as the inverses of the existing encoders; named
  the field positions and counts. (branch `branch-A-CodeQuality`)

## Open — ranked

### 1. `Ui` repeats the numbered-list rendering three times
`src/main/java/meowmeow/ui/Ui.java:142-192` — `showTasks`, `showTasksOn` and
`showMatchingTasks` each build a `String[]` sized `n + 1`, put a header in
slot 0, then loop `" " + (i + 1) + "." + task`.
*Breaks:* Minimize Code Duplication (Intermediate).
*Fix:* extract `private String numberedList(String header, List<Task> tasks)`;
each caller becomes one line (the empty-result branches in `showTasksOn` /
`showMatchingTasks` stay in the callers).

### 2. `Ui.showAdded` and `Ui.showRemoved` are near-identical
`src/main/java/meowmeow/ui/Ui.java:104-122` — same three lines, differing only
in `"added"` vs `"removed"`.
*Breaks:* Minimize Code Duplication (Intermediate).
*Fix:* one private helper taking the verb, or a small `enum`; keep the two
public methods as thin wrappers so call sites still read well.

### 3. Magic marker-length numbers in `Parser`
`src/main/java/meowmeow/parser/Parser.java:146,173,174` — `byMarker + 3`,
`arguments.substring(fromMarker + 5, toMarker)`, `toMarker + 3`. The `3` and
`5` are the lengths of `"/by"`, `"/to"` and `"/from"`.
*Breaks:* Avoid Magic Numbers (Basic), Make the Code Obvious (Basic).
*Fix:* `private static final String BY_MARKER = "/by";` etc., then
`byMarker + BY_MARKER.length()`. Also lets `lastIndexOfIgnoreCase` calls name
the marker instead of a bare literal.

### 4. `Parser.parseDeadline` and `parseEvent` share an un-extracted shape
`src/main/java/meowmeow/parser/Parser.java:143-186` — both: find the rightmost
marker case-insensitively, slice the string around it, reject if any piece is
empty, then `TaskDateTime.parse`.
*Breaks:* Minimize Code Duplication (Intermediate), SLAP (Intermediate).
*Fix:* factor the "split on a trailing marker" step into a helper. Lower
priority than 1–3: the two methods differ enough (one vs two markers, the
end-before-start check) that a shared helper needs care not to become a
parameter soup.

### 5. Mutable `protected` fields in the `Task` hierarchy
`src/main/java/meowmeow/task/Task.java:14-16`, `task/Deadline.java:15`,
`task/Event.java:15-16` — `protected String description`,
`protected TaskDateTime by`, etc. No subclass mutates them, and only `status`
genuinely changes after construction.
*Breaks:* Minimize Scope / avoid unnecessary mutability (Intermediate).
Checkstyle permits `protected` (`protectedAllowed=true`), so this is a
guideline fix, not a lint fix.
*Fix:* make `description`, `by`, `from`, `to` `private final`; add getters
where a subclass or `toString`/`toFileString` needs them. Leave `status`
mutable (or route it through `setStatus`).

### 6. `printStackTrace()` swallows a fatal GUI error
`src/main/java/meowmeow/gui/Main.java:32-34`,
`src/main/java/meowmeow/gui/DialogBox.java:35-37` — a failed FXML load prints a
trace and then carries on with half-built UI (NPE on the next line).
*Breaks:* Avoid Empty Catch Blocks / don't hide errors (Basic).
*Fix:* wrap in an unchecked exception and rethrow, or show an error dialog and
exit. A broken FXML resource is a build error, not something to limp past.

### 7. Unguarded `getResourceAsStream` for the avatar images
`src/main/java/meowmeow/gui/MainWindow.java:30-31` — `new Image(this.getClass()
.getResourceAsStream("/images/DaUser.png"))` throws an opaque NPE during field
init if the image is missing or renamed.
*Breaks:* Make the Code Obvious (Basic) — fail with a clear message.
*Fix:* load via a helper that null-checks the stream and names the missing
resource.

### 8. Save-file path literal duplicated across both entry points
`src/main/java/meowmeow/Meowmeow.java:106` and
`src/main/java/meowmeow/gui/Main.java:18` both pass `"data", "meowmeow.txt"`.
*Breaks:* Minimize Code Duplication (Intermediate).
*Fix:* one shared constant (e.g. `Meowmeow.DEFAULT_SAVE_PATH` as a `String[]`,
or a no-arg `Meowmeow()` that fills them in).

### 9. `run()` and `getResponse()` duplicate the parse/execute/catch flow
`src/main/java/meowmeow/Meowmeow.java:70-76` vs `92-98` — both do
`Parser.parse(input)` → `execute(tasks, ui, storage)` → `catch
MeowmeowException` → `ui.showError`.
*Breaks:* Minimize Code Duplication (Intermediate).
*Fix:* have `run()`'s loop body call `getResponse` (or a shared private
`handle(String)`), so the console loop only adds the divider and the
exit-check.

### 10. `null` as a sentinel across the load path
`src/main/java/meowmeow/storage/Storage.java` `parseTask` / `buildTask`, plus
the new `TaskType.fromTag` / `TaskStatus.fromFileFlag`, all return `null` for
"not recognised".
*Breaks:* Avoid Unsafe Shortcuts (Intermediate) — `null` return values.
*Fix:* migrate the whole path to `Optional<Task>` / `Optional<TaskType>` /
`Optional<TaskStatus>` in one commit (doing it piecemeal leaves the code
mixing both idioms — that is why it was left out of the first pass).

### 11. `Ui` Javadoc claims a try-with-resources it never gets
`src/main/java/meowmeow/ui/Ui.java:22-26` says a caller holds `Ui` "in a
try-with-resources block", but `Meowmeow.run()` (`Meowmeow.java:57-80`) uses a
plain `try { ... } finally { ui.close(); }` and `getResponse` never closes it.
*Breaks:* Comment must match code (Basic).
*Fix:* either switch `run()` to an actual try-with-resources, or soften the
Javadoc to describe what the code does.
