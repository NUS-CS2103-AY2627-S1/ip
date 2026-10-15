# NiuLai Error-Handling Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Make NiuLai reject malformed or inconsistent task data, recover valid records from damaged storage, and
preserve state when expected input, filesystem, or GUI-resource failures occur.

**Architecture:** Normalize and validate syntax in `Parser`, keep temporal and identity invariants in the model,
enforce uniqueness in `TaskList`, and return structured recovery results from `Storage`. Commands retain transactional
rollback, while `NiuLai` and `Ui` translate typed expected failures into consistent CLI and GUI messages.

**Tech Stack:** Java 25, JavaFX 17, JUnit Jupiter 5.14.4, Gradle, Checkstyle, PowerShell UI-test runner.

**Spec:** `docs/superpowers/specs/2026-09-09-error-handling-design.md`

## Global Constraints

- Use Java 25 for every compile, test, and application command.
- Follow the SE-EDU basic and intermediate Java coding standard and Google Java Style for uncovered topics.
- Preserve free-form deadline/event values when they do not match a supported temporal shape.
- Normalize identity by task type and all details, ignoring case and repeated whitespace; ignore completion status.
- Preserve valid records from partly corrupt files and protect the original before the first recovery save.
- Add or revise JUnit coverage for every changed high-value method, targeting approximately the top 50% of methods.
- Review and update `test/ui-test-plan.md` after every code update, then run the project `test-ui` skill.
- Do not commit or push; the user has not authorized repository history changes.

---

### Task 1: Strict Comparable Temporal Values

**Files:**
- Create: `src/main/java/niulai/model/TemporalValue.java`
- Create: `src/test/java/niulai/model/TemporalValueTest.java`
- Modify: `src/main/java/niulai/model/Deadline.java`
- Modify: `src/main/java/niulai/model/Event.java`
- Modify: `src/test/java/niulai/model/DeadlineTest.java`
- Modify: `src/test/java/niulai/model/EventTest.java`

**Interfaces:**
- Produces: `TemporalValue.parse(String)`, `TemporalValue.getDate()`, and
  `TemporalValue.isEndStrictlyAfter(TemporalValue, TemporalValue)`.
- Produces: model constructors that throw `IllegalArgumentException` for recognizable invalid values and comparable
  event ranges whose end is not later than their start.

- [ ] **Step 1: Add focused failing temporal tests**

```java
@Test
void parse_impossibleRecognizableValues_exceptionThrown() {
    assertThrows(IllegalArgumentException.class, () -> TemporalValue.parse("2026-02-30"));
    assertThrows(IllegalArgumentException.class, () -> TemporalValue.parse("30/2/2026"));
    assertThrows(IllegalArgumentException.class, () -> TemporalValue.parse("Feb 30"));
    assertThrows(IllegalArgumentException.class, () -> TemporalValue.parse("24:30"));
    assertThrows(IllegalArgumentException.class, () -> TemporalValue.parse("13pm"));
}

@Test
void parse_freeFormValue_preservesTextWithoutComparableDate() {
    TemporalValue value = TemporalValue.parse("tomorrow morning");
    assertEquals("tomorrow morning", value.toString());
    assertEquals(null, value.getDate());
}

@Test
void event_equalOrReversedComparableEndpoints_exceptionThrown() {
    assertThrows(IllegalArgumentException.class,
            () -> new Event("meeting", "2026-09-10 1000", "2026-09-10 1000"));
    assertThrows(IllegalArgumentException.class,
            () -> new Event("meeting", "11am", "10am"));
}

@Test
void event_freeFormEndpoints_preserved() {
    Event event = new Event("meeting", "after lunch", "before dinner");
    assertEquals("[E][ ] meeting (from: after lunch to: before dinner)", event.toString());
}
```

- [ ] **Step 2: Run the new model tests and confirm the intended red state**

Run:

```powershell
.\gradlew.bat test --tests niulai.model.TemporalValueTest --tests niulai.model.EventTest
```

Expected: compilation fails because `TemporalValue` does not exist, and existing `Event` accepts equal/reversed
endpoints.

- [ ] **Step 3: Implement temporal classification and strict parsing**

Create a final immutable value with these public methods:

```java
public static TemporalValue parse(String input)
public LocalDate getDate()
public static boolean isEndStrictlyAfter(TemporalValue start, TemporalValue end)
@Override public String toString()
```

Use `ResolverStyle.STRICT`, case-insensitive English month parsing, and explicit lexical patterns for the formats in
the spec. Store one `Kind` value from `FREE_FORM`, `DATE`, `DATE_TIME`, `MONTH_DAY`, `MONTH_DAY_TIME`, or `TIME` and
only the matching `java.time` representation. `isEndStrictlyAfter` returns `true` when values are not safely
comparable; otherwise it performs the direct or contextual comparison defined by the spec.

Update the constructors as follows:

```java
TemporalValue temporalValue = TemporalValue.parse(by.strip());
// Deadline retains the existing LocalDate/LocalDateTime display and storage behavior.
```

```java
TemporalValue start = TemporalValue.parse(normalizedFrom);
TemporalValue end = TemporalValue.parse(normalizedTo);
if (!TemporalValue.isEndStrictlyAfter(start, end)) {
    throw new IllegalArgumentException("An event must end after it starts.");
}
```

- [ ] **Step 4: Run all model tests and confirm green**

Run:

```powershell
.\gradlew.bat test --tests "niulai.model.*"
```

Expected: all model tests pass with no warning or stack trace.

- [ ] **Step 5: Review the UI plan, run it, and retain the transcript**

No UI output changes in this task because parser messages are not yet connected, so do not edit the plan. Run the
configured Python 3 interpreter against:

```powershell
& "C:\Users\User\.cache\codex-runtimes\codex-primary-runtime\dependencies\python\python.exe" `
    .codex/skills/test-ui/scripts/run_ui_tests.py
```

Expected: every existing UI case passes. Stop immediately on the first failure.

### Task 2: Command Grammar, Canonical Task Identity, and No-Op Status Errors

**Files:**
- Create: `src/main/java/niulai/model/DuplicateTaskException.java`
- Modify: `src/main/java/niulai/model/Task.java`
- Modify: `src/main/java/niulai/model/Deadline.java`
- Modify: `src/main/java/niulai/model/Event.java`
- Modify: `src/main/java/niulai/model/TaskList.java`
- Modify: `src/main/java/niulai/service/Parser.java`
- Modify: `src/main/java/niulai/command/AddCommand.java`
- Modify: `src/main/java/niulai/command/StatusCommand.java`
- Modify: `src/test/java/niulai/model/TaskTest.java`
- Modify: `src/test/java/niulai/model/TaskListTest.java`
- Modify: `src/test/java/niulai/service/ParserTest.java`
- Modify: `src/test/java/niulai/command/PersistenceCommandTest.java`
- Modify: `test/ui-test-plan.md`

**Interfaces:**
- Consumes: `TemporalValue.parse(String)` and strict `Event` construction from Task 1.
- Produces: `Task.hasSameIdentity(Task)`, `Task.normalizeField(String)`, and `DuplicateTaskException`.
- Produces: parser behavior that emits `NiuLaiException` for every anticipated malformed command.

- [ ] **Step 1: Add failing normalization, grammar, identity, and state-precondition tests**

Add parser assertions such as:

```java
assertEquals("read book",
        parser.parseTaskCreation("  todo\t read   book  ").getDescription());
assertThrows(NiuLaiException.class, () -> parser.parseCommand("list now", 0));
assertThrows(NiuLaiException.class,
        () -> parser.parseTaskCreation("deadline report /by tomorrow /by Friday"));
assertThrows(NiuLaiException.class,
        () -> parser.parseTaskCreation("event meeting /to 11am /from 10am"));
assertThrows(NiuLaiException.class,
        () -> parser.parseTaskIndex("mark +1", Command.Type.MARK, 2));
assertThrows(NiuLaiException.class,
        () -> parser.parseTaskIndex("mark 999999999999999999999", Command.Type.MARK, 2));
assertThrows(NiuLaiException.class,
        () -> parser.parseCommand("todo line\nbreak", 0));
```

Add model and command assertions:

```java
TaskList tasks = new TaskList(new Todo("Read   Book"));
assertThrows(DuplicateTaskException.class, () -> tasks.add(new Todo(" read book ")));
tasks.add(new Deadline("read book", "tomorrow"));
assertEquals(2, tasks.size());
```

```java
Task task = new Todo("read book");
task.markAsDone();
NiuLaiException error = assertThrows(NiuLaiException.class,
        () -> new MarkCommand(0).execute(new TaskList(task), new Ui(), storage));
assertEquals("NOOO!!! That task is already marked as done.", error.getMessage());
```

- [ ] **Step 2: Run targeted tests and confirm failures are behavioral**

Run:

```powershell
.\gradlew.bat test --tests niulai.service.ParserTest --tests niulai.model.TaskListTest `
    --tests niulai.command.PersistenceCommandTest
```

Expected: repeated markers and control characters are accepted, duplicate tasks are added, and repeated status
changes do not raise the expected errors.

- [ ] **Step 3: Implement canonical fields and duplicate rejection**

Implement normalization once in `Task`:

```java
protected static String normalizeField(String value) {
    if (value == null || value.isBlank()) {
        throw new IllegalArgumentException("Task fields cannot be blank.");
    }
    String stripped = value.strip();
    for (int i = 0; i < stripped.length(); i++) {
        char character = stripped.charAt(i);
        if (Character.isISOControl(character) && character != '\t') {
            throw new IllegalArgumentException("Task fields cannot contain control characters.");
        }
    }
    return stripped.replaceAll("[\\p{Zs}\\t]+", " ");
}
```

Store normalized fields in every task subclass. Implement `hasSameIdentity` using type plus a protected immutable list
of lower-case identity fields. Override the identity fields in `Deadline` and `Event`. In both `TaskList.add` methods,
scan before mutation and throw:

```java
throw new DuplicateTaskException("That task already exists.");
```

Catch that exception in `AddCommand` before any save and translate it to:

```java
throw new NiuLaiException("NOOO!!! That task already exists.");
```

- [ ] **Step 4: Replace permissive regex parsing with explicit marker validation**

Normalize the raw input after rejecting `\r`, `\n`, and non-whitespace ISO control characters. Count standalone
markers using bounded regular expressions, require exact counts and order, and slice fields by marker positions. For
indices, require `[0-9]+` before `Integer.parseInt`. Wrap model `IllegalArgumentException` messages with the chatbot
prefix:

```java
try {
    return new Event(description, from, to);
} catch (IllegalArgumentException e) {
    throw new NiuLaiException("NOOO!!! " + e.getMessage());
}
```

Call `TemporalValue.parse` for date-shaped `find` arguments so impossible dates never fall through to keyword mode.

- [ ] **Step 5: Reject status operations that would not change state**

Before `applyStatus`, compare the task's status to the target command. Throw the exact messages:

```text
NOOO!!! That task is already marked as done.
NOOO!!! That task is already marked as not done.
```

Do not call storage and do not return a new undo action for either rejected command.

- [ ] **Step 6: Run the complete JUnit suite and confirm green**

Run:

```powershell
.\gradlew.bat test
```

Expected: all JUnit tests pass.

- [ ] **Step 7: Extend and run the UI test plan**

Add complete cases with these input sequences and exact resulting transcripts:

```text
list extra
deadline report /by 2026-02-30
deadline report /by tomorrow /by Friday
event meeting /from 11am /to 10am
event meeting /from 10am /to 10am
todo Read   Book
todo read book
bye
```

Run the project UI runner. Expected: syntax/date/range errors are displayed, the second todo is rejected as a
duplicate, the application remains responsive, and all cases pass. Stop at the first mismatch.

### Task 3: Partial Storage Recovery, Backups, and Concurrent-Change Protection

**Files:**
- Create: `src/main/java/niulai/service/StorageException.java`
- Modify: `src/main/java/niulai/service/Storage.java`
- Modify: `src/test/java/niulai/service/StorageTest.java`
- Modify: `src/test/java/niulai/command/PersistenceCommandTest.java`
- Modify: `test/ui-test-plan.md`

**Interfaces:**
- Consumes: strict task constructors and `DuplicateTaskException` from Tasks 1 and 2.
- Produces: `Storage.LoadIssue`, `Storage.LoadResult`, and `Storage.load()` returning `LoadResult`.
- Produces: `StorageException.getUserMessage()` for typed save/load failures.

- [ ] **Step 1: Add failing storage recovery and protection tests**

```java
@Test
void load_mixedValidInvalidAndDuplicateLines_recoversUniqueValidTasks() throws IOException {
    Files.writeString(file, "T | 0 | first\ninvalid\nT | 1 | FIRST\nD | 0 | report | 2026-09-10");

    Storage.LoadResult result = new Storage(file.toString()).load();

    assertEquals(2, result.tasks().size());
    assertEquals(List.of(2, 3), result.issues().stream()
            .map(Storage.LoadIssue::lineNumber).toList());
}

@Test
void save_afterPartialRecovery_createsBackupBeforeReplacingData() throws IOException {
    byte[] original = "T | 0 | first\ninvalid".getBytes(StandardCharsets.UTF_8);
    Files.write(file, original);
    Storage storage = new Storage(file.toString());
    Storage.LoadResult result = storage.load();

    storage.save(result.tasks());

    assertArrayEquals(original, Files.readAllBytes(file.resolveSibling("niulai.txt.bak")));
    assertEquals("T | 0 | first", Files.readString(file).strip());
}

@Test
void save_externalModification_exceptionLeavesExternalFileUntouched() throws IOException {
    Files.writeString(file, "T | 0 | first");
    Storage storage = new Storage(file.toString());
    Storage.LoadResult result = storage.load();
    Files.writeString(file, "T | 0 | externally changed");

    assertThrows(StorageException.class, () -> storage.save(result.tasks()));
    assertEquals("T | 0 | externally changed", Files.readString(file));
}
```

Also add deterministic tests for invalid UTF-8 bytes, a directory used as the data path, an existing `.bak` requiring
`.bak.1`, and rollback when backup creation cannot succeed.

- [ ] **Step 2: Run storage and persistence-command tests and confirm red**

Run:

```powershell
.\gradlew.bat test --tests niulai.service.StorageTest `
    --tests niulai.command.PersistenceCommandTest
```

Expected: `LoadResult`, line recovery, backups, and external-change rejection do not exist.

- [ ] **Step 3: Implement typed load results and per-line recovery**

Add nested immutable records:

```java
public record LoadIssue(int lineNumber, String reason) { }
public record LoadResult(TaskList tasks, List<LoadIssue> issues) { }
```

Read bytes first, decode with a UTF-8 decoder configured with `CodingErrorAction.REPORT`, and split decoded text into
numbered lines. Parse each nonblank line inside its own try/catch for malformed fields, model validation, or duplicate
identity. Retain the first duplicate and add an immutable issue for each skipped later record.

Wrap whole-file I/O, permission, decoding, and path-type failures in `StorageException`, set a write-block flag, and
provide this user message:

```text
I couldn't read the task file safely. Task changes are disabled until you fix the file and restart NiuLai.
```

- [ ] **Step 4: Implement original-file backup and snapshot checks**

Keep the original byte array and SHA-256 digest after a successful load. Before every save, compare current existence
and bytes with the expected snapshot. On mismatch, throw a `StorageException` whose user message asks the user to
restart instead of overwriting external changes.

When recovery issues exist, write the original bytes with `CREATE_NEW` to the first available backup name. Only after
the backup succeeds may the normal temporary-file replacement proceed. Update the snapshot and clear recovery state
after a successful save.

- [ ] **Step 5: Preserve transactional command behavior with typed causes**

Change `Command.createStorageFailure` to accept the caught exception. If it is a `StorageException`, retain its
specific user message; otherwise retain the current generic save message. Verify add/delete/status/undo restore the
exact prior state when save or backup fails.

- [ ] **Step 6: Run the complete JUnit suite and confirm green**

Run:

```powershell
.\gradlew.bat test
```

Expected: all JUnit tests pass and malformed storage no longer discards valid lines.

- [ ] **Step 7: Update and run storage-related UI cases**

Replace the current all-or-nothing malformed-file case with a file containing one valid line, one malformed line, and
another valid line. The expected transcript must show both valid tasks, identify the skipped line, accept a new task,
and confirm that the original file was saved to the first backup name. Run the complete UI runner and stop at its first
failure.

### Task 4: Session Recovery Messages and Explicit GUI Resources

**Files:**
- Modify: `src/main/java/niulai/NiuLai.java`
- Modify: `src/main/java/niulai/service/Ui.java`
- Modify: `src/main/java/niulai/gui/Main.java`
- Modify: `src/main/java/niulai/gui/DialogBox.java`
- Modify: `src/test/java/niulai/gui/GuiUiTest.java`
- Create: `src/test/java/niulai/NiuLaiTest.java`
- Create: `src/test/java/niulai/gui/MainTest.java`
- Modify: `test/ui-test-plan.md`

**Interfaces:**
- Consumes: `Storage.LoadResult`, `Storage.LoadIssue`, and `StorageException` from Task 3.
- Produces: `Ui.showRecoveryWarning(List<Integer>)` and `Ui.showLoadingError(String)`.
- Produces: package-visible `Main.requireResource(String)` returning a non-null `URL` or throwing a clear
  `IllegalStateException`.

- [ ] **Step 1: Add failing session and GUI-resource tests**

```java
@Test
void startSession_partlyCorruptFile_reportsSkippedLinesAndKeepsValidTasks() {
    Files.writeString(dataFile, "T | 0 | valid\nmalformed");
    GuiUi ui = new GuiUi();
    NiuLai chatbot = new NiuLai(dataFile.toString(), ui);

    chatbot.startSession();
    String startup = ui.consumeOutput();
    chatbot.processCommand("list");

    assertTrue(startup.contains("line 2"));
    assertTrue(ui.consumeOutput().contains("valid"));
}

@Test
void startSession_unreadablePath_blocksMutationsButAllowsReadOnlyCommands() {
    GuiUi ui = new GuiUi();
    NiuLai chatbot = new NiuLai(directoryPath.toString(), ui);
    chatbot.startSession();

    chatbot.processCommand("todo new task");
    assertTrue(ui.consumeOutput().contains("changes are disabled"));
    chatbot.processCommand("list");
    assertTrue(ui.consumeOutput().contains("Here are the tasks"));
}

@Test
void requireResource_missingResource_exceptionNamesResource() {
    IllegalStateException error = assertThrows(IllegalStateException.class,
            () -> Main.requireResource("/view/missing.fxml"));
    assertTrue(error.getMessage().contains("/view/missing.fxml"));
}
```

- [ ] **Step 2: Run targeted integration tests and confirm red**

Run:

```powershell
.\gradlew.bat test --tests niulai.NiuLaiTest --tests niulai.gui.MainTest `
    --tests niulai.gui.GuiUiTest
```

Expected: startup cannot consume `LoadResult`, no recovery warning API exists, and GUI resources are resolved through
nullable lookups.

- [ ] **Step 3: Connect structured load outcomes to the UI**

Have `NiuLai.startSession` install `result.tasks()` and call `showRecoveryWarning` when issues are present. On a typed
unrecoverable error, keep an empty list and call `showLoadingError(error.getUserMessage())`. Format line numbers in a
stable comma-separated list so CLI and GUI tests share the same expected wording.

Keep `processCommand` limited to `NiuLaiException`; storage commands already translate expected save failures. This
avoids hiding unrelated programming errors.

- [ ] **Step 4: Resolve required GUI resources explicitly before building the scene**

Implement:

```java
static URL requireResource(String path) {
    URL resource = Main.class.getResource(path);
    if (resource == null) {
        throw new IllegalStateException("Required GUI resource is missing: " + path);
    }
    return resource;
}
```

Resolve `MainWindow.fxml`, `DialogBox.fxml`, and `style.css` before creating the chatbot or showing a stage. Use the
resolved URLs in both loaders. At the JavaFX startup boundary, print one concise fatal diagnostic to standard error
and exit the platform without converting it to a chat response.

- [ ] **Step 5: Run all JUnit tests and Checkstyle**

Run:

```powershell
.\gradlew.bat test checkstyleMain checkstyleTest
```

Expected: every JUnit and Checkstyle task passes.

- [ ] **Step 6: Finalize and run the UI plan**

Ensure every changed CLI input or message has a complete expected transcript. Run the project UI runner from the
repository root with the configured Python 3 interpreter. Expected: all cases pass; retain the full runner transcript
for handoff.

### Task 5: Full Verification and Handoff

**Files:**
- Review: every file listed in Tasks 1-4
- Review: `docs/superpowers/specs/2026-09-09-error-handling-design.md`
- Review: `test/ui-test-plan.md`

**Interfaces:**
- Consumes: the completed implementation and all automated tests.
- Produces: fresh build, test, style, and scripted UI evidence plus a concise change summary.

- [ ] **Step 1: Confirm Java 25 and inspect the final diff**

Run:

```powershell
java -version
git diff --check
git status --short
git diff --stat
```

Expected: Java reports version 25, `git diff --check` produces no diagnostics, and the status contains only intended
source, test, UI-plan, spec, and plan changes.

- [ ] **Step 2: Run clean full verification**

Run:

```powershell
.\gradlew.bat clean test checkstyleMain checkstyleTest shadowJar
```

Expected: `BUILD SUCCESSFUL`, zero failed tests, zero Checkstyle violations, and `build/libs/niulai.jar` is created.

- [ ] **Step 3: Run the complete scripted UI suite once more**

Run:

```powershell
& "C:\Users\User\.cache\codex-runtimes\codex-primary-runtime\dependencies\python\python.exe" `
    .codex/skills/test-ui/scripts/run_ui_tests.py
```

Expected: every case passes. Stop on the first failure and report actual output, expected output, exit status, and the
runner's diff without making further code changes.

- [ ] **Step 4: Check requirements against the specification**

Confirm each spec section has corresponding production behavior and passing tests: grammar, temporal values,
duplicates, status preconditions, partial recovery, backup protection, external-change protection, rollback,
actionable messages, and GUI resource checks.

- [ ] **Step 5: Hand off without committing**

Report modified files, behavior changes, fresh Gradle results, and the complete UI runner transcript. State explicitly
that no commit or push was performed.
