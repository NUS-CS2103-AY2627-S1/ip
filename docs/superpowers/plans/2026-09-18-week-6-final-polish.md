# Week 6 Final Polish Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Finish Nexus as a polished, resilient, documented, and release-ready JavaFX chatbot satisfying every Week 6 iP requirement.

**Architecture:** Preserve the current command-oriented model and data format while introducing a small immutable `Response` boundary between command processing and presentation. Parsing and storage will normalize or recover from anticipated user/environment errors, while JavaFX uses response metadata and CSS style classes for a responsive, asymmetric conversation interface.

**Tech Stack:** Java 25.0.3, JavaFX 17, Gradle 9.6.1, JUnit Jupiter 5.14.4, Checkstyle 11, Shadow 9.5.1.

**Spec:** `docs/superpowers/specs/2026-09-18-week-6-final-polish-design.md`

## Global Constraints

- Use Java 25.0.3 (`sdk use java 25.0.3.fx-zulu`) for every build and test.
- Implement all four increments: `A-BetterGui`, `A-Personality`, `A-MoreErrorHandling`, and `A-MoreTesting`.
- Preserve all existing commands and the `data/nexus.txt` persisted-data format.
- Do not add task categories, graphical task editors, accounts, networking, or reminders.
- Follow red-green-refactor: every production behavior begins with a failing test.
- Use lightweight Git tags.
- Do not push until independent subagents have reviewed requirement coverage and runtime behavior.

---

### Task 1: Structured Responses, Personality, and Input Validation

**Files:**
- Create: `src/main/java/nexus/Response.java`
- Create: `src/test/java/nexus/ParserTest.java`
- Modify: `src/main/java/nexus/Nexus.java`
- Modify: `src/main/java/nexus/Parser.java`
- Modify: `src/main/java/nexus/Event.java`
- Modify: `src/test/java/nexus/NexusTest.java`

**Interfaces:**
- Produces: `Response(String text, boolean error, boolean exit)` with accessors `text()`, `isError()`, and `shouldExit()`.
- Produces: `Nexus#getCommandResponse(String): Response` for the GUI.
- Preserves: `Nexus#getResponse(String): String` for console and existing test compatibility.
- Preserves: `Parser#createTask(String): Task`, now accepting normalized command whitespace and rejecting invalid marker combinations.

- [ ] **Step 1: Add failing tests for structured response behavior and personality**

Add focused cases to `NexusTest` using literal expected values:

```java
@Test
void getCommandResponse_invalidCommand_marksResponseAsError() {
    Nexus nexus = new Nexus(temporaryDirectory.resolve("nexus.txt"));

    Response response = nexus.getCommandResponse("nonsense");

    assertTrue(response.isError());
    assertFalse(response.shouldExit());
    assertTrue(response.text().contains("I don't recognise"));
}

@Test
void getCommandResponse_bye_marksResponseForExit() {
    Nexus nexus = new Nexus(temporaryDirectory.resolve("nexus.txt"));

    Response response = nexus.getCommandResponse("bye");

    assertFalse(response.isError());
    assertTrue(response.shouldExit());
    assertEquals("You're all set. See you next time!", response.text());
}
```

The production mutations caught are returning an ordinary success for invalid
input and tying application exit to raw input instead of command semantics.

- [ ] **Step 2: Run the focused test and verify RED**

Run:

```bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk use java 25.0.3.fx-zulu
./gradlew test --tests nexus.NexusTest
```

Expected: compilation fails because `Response` and `getCommandResponse` do not exist.

- [ ] **Step 3: Implement the minimal response boundary**

Create `Response.java`:

```java
package nexus;

/** Describes text and presentation behavior produced by a Nexus command. */
public record Response(String text, boolean isError, boolean shouldExit) {
    /** Creates a normal response that keeps Nexus open. */
    public static Response success(String text) {
        return new Response(text, false, false);
    }

    /** Creates a validation or environment error response. */
    public static Response error(String text) {
        return new Response(text, true, false);
    }

    /** Creates the farewell response that closes Nexus after display. */
    public static Response exit(String text) {
        return new Response(text, false, true);
    }
}
```

Refactor `Nexus#getResponse` to delegate to `getCommandResponse(input).text()`.
`getCommandResponse` catches expected exceptions and creates an error response;
`executeCommand` returns an exit response for `bye` and success responses for
other commands. Update the greeting and action copy to be concise and encouraging.

- [ ] **Step 4: Run the focused test and verify GREEN**

Run `./gradlew test --tests nexus.NexusTest`.

Expected: all `NexusTest` cases pass after updating existing literal response
expectations to the approved personality.

- [ ] **Step 5: Add failing parser and validation tests**

Create `ParserTest` and add Nexus integration cases covering the observable
breaks:

```java
@Test
void createTask_extraCommandWhitespace_normalizesDescription() throws Exception {
    Task task = Parser.createTask("todo     read   book");

    assertEquals("[T][ ] read book", task.toString());
}

@Test
void createTask_deadlineWithRepeatedBy_throwsReadableError() {
    NexusException exception = assertThrows(NexusException.class,
            () -> Parser.createTask("deadline submit /by 2026-09-18 /by 2026-09-19"));

    assertEquals("Use exactly one /by followed by a date in YYYY-MM-DD format.",
            exception.getMessage());
}

@Test
void createTask_eventWhoseEndIsNotAfterStart_throwsReadableError() {
    NexusException exception = assertThrows(NexusException.class,
            () -> Parser.createTask("event workshop /from 2026-09-18 /to 2026-09-18"));

    assertEquals("An event's /to date must be after its /from date.",
            exception.getMessage());
}
```

Add integration tests for blank input, whitespace around `list`, empty `find`,
zero/negative/out-of-range indices, impossible dates, missing markers, reversed
markers, and repeated markers.

- [ ] **Step 6: Run parser and Nexus tests and verify RED**

Run:

```bash
./gradlew test --tests nexus.ParserTest --tests nexus.NexusTest
```

Expected: failures show the current parser preserves repeated spaces, accepts
equal event dates, and returns generic errors for malformed commands.

- [ ] **Step 7: Implement command normalization and precise validation**

In `Parser`, trim input, normalize whitespace in descriptions, count marker
occurrences, validate marker order, and reject empty components. Construct
`LocalDate` values through the task constructors so impossible dates retain one
consistent error path. In `Event`, compare parsed dates after construction:

```java
if (!to.isAfter(from)) {
    throw new NexusException("An event's /to date must be after its /from date.");
}
```

Make the `Event` constructor declare `throws NexusException`. In `Nexus`, trim
the complete command before routing; validate a non-empty find keyword and parse
indices through a helper that rejects values outside `1..tasks.size()`.

- [ ] **Step 8: Verify GREEN and refactor**

Run:

```bash
./gradlew test --tests nexus.ParserTest --tests nexus.NexusTest
./gradlew checkstyleMain checkstyleTest
```

Expected: tests and Checkstyle pass. Extract only helpers that remove duplicated
normalization, marker counting, or index validation.

- [ ] **Step 9: Commit Task 1**

```bash
git add src/main/java/nexus/Response.java src/main/java/nexus/Nexus.java \
  src/main/java/nexus/Parser.java src/main/java/nexus/Event.java \
  src/test/java/nexus/NexusTest.java src/test/java/nexus/ParserTest.java
git commit -m "Polish Nexus responses and command validation" \
  -m "Add structured response metadata, a consistent productivity-focused voice, and precise validation so common input mistakes are handled without crashes."
```

---

### Task 2: Recoverable Storage Errors

**Files:**
- Create: `src/main/java/nexus/LoadResult.java`
- Create: `src/test/java/nexus/StorageTest.java`
- Modify: `src/main/java/nexus/Storage.java`
- Modify: `src/main/java/nexus/Nexus.java`
- Modify: `src/test/java/nexus/NexusTest.java`

**Interfaces:**
- Produces: `LoadResult(List<Task> tasks, int skippedRecords)`; its compact constructor copies the list defensively.
- Changes: `Storage#load(): LoadResult`.
- Consumes: Task 1's `Response` to surface startup and save errors.

- [ ] **Step 1: Write failing tests for partial recovery**

In `StorageTest`, create a real temporary file containing a valid todo, one
malformed record, and a valid deadline. Assert that the result has two tasks and
one skipped record. Add a `NexusTest` that creates the same fixture and asserts
the greeting contains a recovery warning while `list` contains both valid tasks.

```java
@Test
void load_mixedValidAndMalformedRecords_loadsValidRecordsAndCountsSkipped()
        throws Exception {
    Path dataFile = temporaryDirectory.resolve("nexus.txt");
    Files.writeString(dataFile, "T | 0 | read book\nbroken record\n"
            + "D | 0 | submit report | 2026-09-18\n");

    LoadResult result = new Storage(dataFile).load();

    assertEquals(2, result.tasks().size());
    assertEquals(1, result.skippedRecords());
}
```

The production mutation caught is silently losing malformed records without
giving the user evidence that recovery was incomplete.

- [ ] **Step 2: Run storage tests and verify RED**

Run `./gradlew test --tests nexus.StorageTest --tests nexus.NexusTest`.

Expected: compilation fails because `LoadResult` does not exist and `load`
returns a list.

- [ ] **Step 3: Implement defensive load results**

Create `LoadResult`:

```java
package nexus;

import java.util.List;

/** Contains tasks recovered from storage and the number of unusable records. */
public record LoadResult(List<Task> tasks, int skippedRecords) {
    /** Protects the recovered collection from external mutation. */
    public LoadResult {
        tasks = List.copyOf(tasks);
    }
}
```

Make `Storage#load` increment `skippedRecords` whenever `parseTask` returns null
or a record contains invalid data. Catch record-level parsing failures inside
the loop but continue reading remaining records. Let file-level `IOException`
propagate. Make `Nexus` retain a startup warning and append it to the greeting.

- [ ] **Step 4: Add and verify a real save-failure test**

Use a temporary directory as the configured data-file path. Adding a todo then
causes `Files.write` to fail without mocks:

```java
@Test
void getCommandResponse_dataPathIsDirectory_returnsStorageError() throws Exception {
    Path directoryPath = Files.createDirectory(temporaryDirectory.resolve("data-target"));
    Nexus nexus = new Nexus(directoryPath);

    Response response = nexus.getCommandResponse("todo read book");

    assertTrue(response.isError());
    assertTrue(response.text().contains("couldn't save"));
}
```

Run the test before implementation to observe RED, then adjust `saveTasks` copy
so it reports the failure through `NexusException` and rolls back any in-memory
add/delete/state change that was not persisted.

- [ ] **Step 5: Verify GREEN and full storage compatibility**

Run:

```bash
./gradlew test --tests nexus.StorageTest --tests nexus.NexusTest
./gradlew test
```

Expected: all tests pass, including loading files written by existing versions.

- [ ] **Step 6: Commit Task 2**

```bash
git add src/main/java/nexus/LoadResult.java src/main/java/nexus/Storage.java \
  src/main/java/nexus/Nexus.java src/test/java/nexus/StorageTest.java \
  src/test/java/nexus/NexusTest.java
git commit -m "Recover safely from storage failures" \
  -m "Preserve valid saved tasks, report skipped records, and keep persistence failures from terminating or silently desynchronizing Nexus."
```

---

### Task 3: Responsive and Distinctive JavaFX Interface

**Files:**
- Create: `src/main/resources/css/main.css`
- Modify: `src/main/resources/view/MainWindow.fxml`
- Modify: `src/main/resources/view/DialogBox.fxml`
- Modify: `src/main/java/nexus/Main.java`
- Modify: `src/main/java/nexus/MainWindow.java`
- Modify: `src/main/java/nexus/DialogBox.java`
- Modify: `src/test/java/nexus/GuiTest.java`
- Modify: `src/test/java/nexus/GuiResourceTest.java`

**Interfaces:**
- Adds: `DialogBox#getNexusDialog(String, Image, boolean)` where `true` applies the `error-dialog` style class.
- Consumes: Task 1's `Nexus#getCommandResponse` metadata.
- Preserves: existing two-argument dialog factory for compatibility if tests or console helpers still use it.

- [ ] **Step 1: Write failing GUI behavior tests**

Add JavaFX-thread tests that send an invalid command and inspect the resulting
dialog's style classes, then resize the root pane and assert the scroll pane and
input row receive the larger layout width.

```java
@Test
void mainWindow_invalidCommand_addsErrorStyledNexusDialog() throws Exception {
    runOnJavaFxThread(() -> {
        LoadedWindow window = loadWindow("error-style.txt");
        window.userInput().setText("unknown");
        window.sendButton().fire();

        Node response = window.dialogContainer().getChildren().get(2);
        assertTrue(response.getStyleClass().contains("error-dialog"));
        return null;
    });
}
```

The production mutations caught are dropping response severity at the GUI
boundary and retaining fixed pixel widths when the stage grows.

- [ ] **Step 2: Run GUI tests and verify RED**

Run `./gradlew test --tests nexus.GuiTest`.

Expected: the error style is missing and fixed FXML dimensions prevent responsive
width growth.

- [ ] **Step 3: Implement the responsive layout and CSS**

Replace fixed `AnchorPane` positions with a `BorderPane`: a compact header at
the top, `ScrollPane`/`VBox` in the center, and an `HBox` containing the growing
`TextField` and compact Send button at the bottom. Bind the dialog container's
preferred width to the scroll viewport width in `MainWindow#initialize`.

Create `main.css` with named style classes for the app background, header,
input, button, user bubble, Nexus bubble, and error bubble. Use 44-pixel avatars,
12-16 pixel padding, rounded bubble corners, and accessible dark text on light
backgrounds. Set `maxWidth` on dialog labels so long responses wrap.

Load the stylesheet from `Main` and allow resizing:

```java
stage.setResizable(true);
stage.setMinWidth(420.0);
stage.setMinHeight(520.0);
```

Use `Response#isError` in `MainWindow` when creating the Nexus dialog and
`Response#shouldExit` for delayed shutdown.

- [ ] **Step 4: Verify GUI tests and resource packaging**

Update `GuiResourceTest` to require `/css/main.css`. Run:

```bash
./gradlew test --tests nexus.GuiTest --tests nexus.GuiResourceTest
./gradlew checkstyleMain checkstyleTest
```

Expected: both suites and Checkstyle pass.

- [ ] **Step 5: Visually inspect minimum and enlarged windows**

Run `./gradlew run`, exercise a successful command and an invalid command, and
inspect the complete interface at 420x520 and approximately 720x720. Confirm:

- no clipped controls or horizontal scrollbar;
- bubbles wrap without covering avatars;
- errors are visually distinct;
- focus begins in the input field; and
- the newest response scrolls into view.

Correct any visual defect and rerun GUI tests.

- [ ] **Step 6: Commit Task 3**

```bash
git add src/main/resources/css/main.css src/main/resources/view/MainWindow.fxml \
  src/main/resources/view/DialogBox.fxml src/main/java/nexus/Main.java \
  src/main/java/nexus/MainWindow.java src/main/java/nexus/DialogBox.java \
  src/test/java/nexus/GuiTest.java src/test/java/nexus/GuiResourceTest.java
git commit -m "Polish the responsive Nexus interface" \
  -m "Use compact asymmetric conversation styling, responsive controls, and distinct error bubbles to improve readability and command feedback."
```

---

### Task 4: Product Website and Representative Screenshot

**Files:**
- Create: `docs/Ui.png`
- Modify: `docs/README.md`
- Modify: `README.md`

**Interfaces:**
- Consumes: all final commands, messages, validation rules, and the completed GUI.
- Produces: GitHub Pages content rooted at `docs/` and the exact case-sensitive screenshot path `docs/Ui.png`.

- [ ] **Step 1: Rewrite the user guide from the user's perspective**

Place `# Nexus User Guide` first, embed `![Nexus interface](Ui.png)`, and document:

- installation and `java -jar nexus.jar` startup;
- all ten commands with exact formats and at least one example per task type;
- ISO `YYYY-MM-DD` rules and event ordering;
- accepted whitespace and task-number behavior;
- automatic storage at `data/nexus.txt`;
- malformed-storage recovery; and
- how to exit.

Keep the guide concise and friendly. Update the root README so its command and
build information matches the final behavior and links to `docs/README.md`.

- [ ] **Step 2: Prepare representative conversation data**

Start Nexus from a clean temporary working directory and enter commands that
show a todo, a readable deadline, and one error response. Keep the conversation
short enough for the full content to be legible in one window.

- [ ] **Step 3: Capture and validate `docs/Ui.png`**

Capture exactly one complete Nexus window, including its title bar, using the
native macOS screenshot facility. Save it as `docs/Ui.png` without cropping out
the window frame or stitching images. Inspect dimensions and the image itself;
confirm the Nexus name, input, Send button, and representative messages are all
visible and legible.

- [ ] **Step 4: Check website links and Markdown rendering**

Check every relative image/link target and preview `docs/README.md`. Confirm the
image path is case-exact and no local absolute paths appear in the published
guide.

- [ ] **Step 5: Commit Task 4**

```bash
git add docs/README.md docs/Ui.png README.md
git commit -m "Complete the Nexus product website" \
  -m "Document every user-facing feature and add the required full-window screenshot so GitHub Pages provides a concise final user guide."
```

---

### Task 5: Final Verification, Independent Reviews, and Public Release

**Files:**
- Modify only if verification or review identifies a concrete defect.
- Build artifact: `build/libs/nexus.jar`

**Interfaces:**
- Produces: one verified fat JAR, a lightweight release tag, a public GitHub
  Pages site, and one GitHub release containing only the intended JAR asset.

- [ ] **Step 1: Run the clean Java 25 quality gate**

Run:

```bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk use java 25.0.3.fx-zulu
java -version
./gradlew clean test checkstyleMain checkstyleTest shadowJar
```

Expected: Java reports `25.0.3`; Gradle reports `BUILD SUCCESSFUL`; and exactly
`build/libs/nexus.jar` is the intended runnable JAR.

- [ ] **Step 2: Inspect the fat JAR and smoke-test it in isolation**

Use `jar tf build/libs/nexus.jar` to confirm Nexus classes, FXML, CSS, images,
and JavaFX dependencies are present. Create an empty directory with `mktemp -d`,
copy only `nexus.jar` there, and run `java -jar nexus.jar`. Exercise add, list,
invalid input, and exit; confirm a relative `data/nexus.txt` is created in that
temporary directory and the app never refers to the repository path.

- [ ] **Step 3: Dispatch independent subagent reviews before push**

Dispatch at least two independent reviewers:

1. A requirements reviewer compares the Week 6 source page and the approved
   design against the current files, tests, screenshot, JAR, and Git history.
2. A runtime/code reviewer inspects correctness and runs the clean quality gate,
   isolated JAR smoke test, and targeted malformed-input/storage scenarios.

Do not push while either review is outstanding. Address every blocking finding,
rerun the affected checks, and ask the same reviewer to verify the fix.

- [ ] **Step 4: Audit release state**

Confirm `git status --short` is empty, inspect `git log`, verify no secrets or
unintended build/data files are tracked, and choose the next release version by
checking existing local tags and public GitHub releases.

- [ ] **Step 5: Create and push the lightweight tag and commits**

Create the selected lightweight tag (expected `v0.2` unless remote state shows
it already exists), then push `master` and that exact tag to `origin` only after
the independent reviews pass.

- [ ] **Step 6: Configure and verify GitHub Pages**

Set Pages to deploy from the `master` branch `/docs` folder. Wait for deployment,
then open the public site and `Ui.png` URL in an unauthenticated browser context.
Verify the guide content and image render correctly.

- [ ] **Step 7: Publish and verify the GitHub release**

Create the release for the new tag, title it `Nexus v0.2` (or the selected
version), provide a concise summary of the four polish increments, and upload
only `build/libs/nexus.jar` as the JAR asset. Verify the release is public and
the latest release has exactly one intended JAR asset.

- [ ] **Step 8: Record final evidence**

Report the test/check output, JAR smoke result, review outcomes, pushed commit
and tag, GitHub Pages URL, and public release URL. Mark completion only after
each item in the design's Completion Criteria has direct evidence.
