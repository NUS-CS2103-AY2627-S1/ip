# Week 3 iP Increments Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Complete and publish every mandatory Week 3 iP increment for HABI without optional or stretch-goal behavior.

**Architecture:** Add persistence and parsed deadline dates to the existing console app before extracting `Ui`, `Storage`, `Parser`, and `TaskList`. Then package the code, add Gradle, JUnit, an executable JAR, documentation, coding-standard compliance, and keyword search in the course-prescribed Git order.

**Tech Stack:** Java 25.0.3 Zulu, Java standard library, Gradle wrapper, JUnit 5, Shadow JAR, Markdown UI tests, Git, GitHub CLI.

---

## File Map

- `src/main/java/Habi.java`: app lifecycle and command coordination.
- `src/main/java/{Task,Todo,Deadline,Event,TaskType}.java`: task model.
- `src/main/java/HabiException.java`: recoverable errors.
- `src/main/java/{Ui,Storage,Parser,TaskList}.java`: required OOP components.
- `src/main/java/habi/`: destination after `A-Packages`.
- `src/test/java/habi/`: JUnit tests after `A-JUnit`.
- `test/ui-test-plan.md`: exact console regression cases.
- `.gitignore`: runtime data and generated output.
- `build.gradle`, `gradlew*`, `gradle/wrapper/*`: Gradle and JAR support.

After every production-code edit below, update `test/ui-test-plan.md` if the
console contract changed or needs another scenario, then invoke the project
`test-ui` skill before making the next code edit. Java 25 is mandatory for all
build and test commands.

## Task 1: Verify the Java 25 baseline

**Files:** No repository files changed.

- [ ] Select and verify Java 25:

```bash
source /Users/zhai/.sdkman/bin/sdkman-init.sh
sdk use java 25.0.3.fx-zulu
java -version
javac -version
```

Expected: both tools report `25.0.3`.

- [ ] Run the Week 2 baseline:

```bash
python3 .codex/skills/test-ui/scripts/run_ui_tests.py
rm -rf /private/tmp/habi-baseline-classes
mkdir -p /private/tmp/habi-baseline-classes
javac -d /private/tmp/habi-baseline-classes \
    src/main/java/*.java src/test/java/HabiTest.java
java -cp /private/tmp/habi-baseline-classes HabiTest
```

Expected: UI cases pass and `HabiTest` exits 0.

## Task 2: Implement `Level-7` on `branch-Level-7`

**Files:** Modify `.gitignore`, task model classes, `Habi.java`,
`HabiTest.java`, and `test/ui-test-plan.md`.

- [ ] Create the exact required branch:

```bash
git switch -c branch-Level-7
```

- [ ] Add a failing persistence test to `HabiTest.main`:

```java
verifyTasksPersistAcrossSessions();
```

Add imports for `IOException`, `Files`, and `Path`, then add:

```java
private static void verifyTasksPersistAcrossSessions() {
    Path dataFile = Path.of("data", "habi.txt");
    try {
        Files.deleteIfExists(dataFile);
        Files.deleteIfExists(dataFile.getParent());
        runHabi("todo read book\nmark 1\nbye\n");
        String output = runHabi("list\nbye\n");
        assertContains(output, "1.[T][X] read book",
                "Tasks and their status should persist.");
    } catch (IOException exception) {
        throw new AssertionError("Persistence test setup should succeed.", exception);
    } finally {
        try {
            Files.deleteIfExists(dataFile);
            Files.deleteIfExists(dataFile.getParent());
        } catch (IOException exception) {
            throw new AssertionError("Persistence test cleanup should succeed.", exception);
        }
    }
}
```

Add a two-launch persistence case to the UI plan: the first launch adds and
marks `read book`; the second runs `list` and must show
`1.[T][X] read book`.

- [ ] Run the Java test and confirm RED: the second session lists no tasks.

- [ ] Add persistence accessors and serialization.

Add to `Task`:

```java
public TaskType getType() {
    return type;
}

public boolean isDone() {
    return isDone;
}

public String toDataString() {
    return type.getIcon() + "\t" + (isDone ? "1" : "0") + "\t" + description;
}
```

Add `getBy()` and an override appending `"\t" + by` to `Deadline`. Add
`getFrom()`, `getTo()`, and an override appending both fields to `Event`.

- [ ] Add these exact storage methods to `Habi` and call them at startup and
after every successful add, mark, unmark, or delete command:

```java
private static final Path DATA_FILE_PATH = Path.of("data", "habi.txt");

private static ArrayList<Task> loadTasks() throws HabiException {
    try {
        Files.createDirectories(DATA_FILE_PATH.getParent());
        if (Files.notExists(DATA_FILE_PATH)) {
            Files.createFile(DATA_FILE_PATH);
        }
        ArrayList<Task> tasks = new ArrayList<>();
        for (String line : Files.readAllLines(DATA_FILE_PATH)) {
            if (!line.isBlank()) {
                tasks.add(parseStoredTask(line));
            }
        }
        return tasks;
    } catch (IOException | IllegalArgumentException exception) {
        throw new HabiException("OOPS! I could not load tasks from the data file.");
    }
}

private static Task parseStoredTask(String line) {
    String[] fields = line.split("\\t", -1);
    Task task = switch (fields[0]) {
        case "T" -> new Todo(fields[2]);
        case "D" -> new Deadline(fields[2], fields[3]);
        case "E" -> new Event(fields[2], fields[3], fields[4]);
        default -> throw new IllegalArgumentException("Unknown task type");
    };
    if (fields[1].equals("1")) {
        task.markAsDone();
    }
    return task;
}

private static void saveTasks(ArrayList<Task> tasks) throws HabiException {
    try {
        Files.write(DATA_FILE_PATH,
                tasks.stream().map(Task::toDataString).toList());
    } catch (IOException exception) {
        throw new HabiException("OOPS! I could not save tasks to the data file.");
    }
}
```

Change `handleCommand` to return whether the list changed. Add `/data/` to
`.gitignore`.

- [ ] Invoke the `test-ui` skill, compile with Java 25, and run `HabiTest`.
Expected: GREEN and no tracked runtime data.

- [ ] Commit and push the branch:

```bash
git add .gitignore src/main/java src/test/java/HabiTest.java test/ui-test-plan.md
git commit -m "Save tasks between HABI sessions" \
    -m "Task changes exist only in memory, so they disappear when HABI exits.\n\nPersist all task types and completion states in a relative data file. Create\nthe data directory on first use so a fresh copy starts successfully."
git push -u origin branch-Level-7
```

## Task 3: Merge, tag, and push `Level-7`

```bash
git switch master
git merge --no-ff branch-Level-7 \
    -m "Merge branch-Level-7 for task persistence"
python3 .codex/skills/test-ui/scripts/run_ui_tests.py
git tag Level-7
git push origin master branch-Level-7 Level-7
```

Expected: a merge commit is tagged `Level-7`; remote master, branch, and tag
all exist.

## Task 4: Implement `Level-8` on `branch-Level-8`

**Files:** Modify `Deadline.java`, `Habi.java`, `HabiTest.java`, and UI plan.

- [ ] Create the branch:

```bash
git switch -c branch-Level-8
```

- [ ] Update date tests to input `2026-09-15` and expect:

```text
[D][ ] return book (by: Sep 15 2026)
```

Add an invalid case expecting:

```text
OOPS! Use: deadline DESCRIPTION /by yyyy-MM-dd
```

Run tests and confirm RED because the date is still a raw string.

- [ ] Replace the deadline field and formatting:

```java
private static final DateTimeFormatter DISPLAY_FORMAT =
        DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);
private final LocalDate by;

public Deadline(String description, LocalDate by) {
    super(TaskType.DEADLINE, description);
    this.by = by;
}

public LocalDate getBy() {
    return by;
}

@Override
protected String getTimingDetails() {
    return " (by: " + by.format(DISPLAY_FORMAT) + ")";
}

@Override
public String toDataString() {
    return super.toDataString() + "\t" + by;
}
```

Parse user and stored values with `LocalDate.parse`. Catch
`DateTimeParseException` in deadline command parsing and throw the exact error
above before adding a task.

- [ ] Invoke `test-ui` and run Java 25 tests. Expected: GREEN, including a
deadline reloaded from disk.

- [ ] Commit and push:

```bash
git add src/main/java/Habi.java src/main/java/Deadline.java \
    src/test/java/HabiTest.java test/ui-test-plan.md
git commit -m "Parse and format deadline dates" \
    -m "Deadline dates are raw strings and cannot be interpreted by HABI.\n\nStore ISO input as LocalDate and present it in a readable English format.\nReject malformed dates before changing the task list."
git push -u origin branch-Level-8
```

## Task 5: Merge, tag, and push `Level-8`

```bash
git switch master
git merge --no-ff branch-Level-8 \
    -m "Merge branch-Level-8 for parsed deadline dates"
python3 .codex/skills/test-ui/scripts/run_ui_tests.py
git tag Level-8
git push origin master branch-Level-8 Level-8
```

## Task 6: Complete `A-MoreOOP`

**Files:** Create `Ui.java`, `Storage.java`, `Parser.java`, and `TaskList.java`;
modify `Habi.java` and `HabiTest.java`.

- [ ] Add failing checks that instantiate all four required classes and call
`Parser.parseDeadline`, `TaskList.add`, and `TaskList.delete`. Confirm missing
class compilation errors.

- [ ] Create `TaskList` with this API and move all list mutations to it:

```java
public class TaskList {
    private final ArrayList<Task> tasks;

    public TaskList() {
        this(new ArrayList<>());
    }

    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    public void add(Task task) { tasks.add(task); }
    public Task get(int index) { return tasks.get(index); }
    public Task delete(int index) { return tasks.remove(index); }
    public int size() { return tasks.size(); }
    public List<Task> asList() { return List.copyOf(tasks); }
}
```

- [ ] Create `Parser` with `getKeyword`, `parseTodo`, `parseDeadline`,
`parseEvent`, and `parseTaskIndex`. Move the current validation bodies and
messages verbatim; `parseDeadline` constructs `Deadline` with `LocalDate`.

- [ ] Create `Storage(Path filePath)` with public `load()` and `save(List<Task>)`.
Move the complete Level 7 file logic and stored-line switch into it. It creates
the parent and file when missing and retains both exact I/O error messages.

- [ ] Create `Ui` with a `Scanner` field and these methods:

```java
public boolean hasNextCommand();
public String readCommand();
public void showGreeting();
public void showResponse(String... lines);
public void showTaskList(String heading, List<Task> tasks);
```

Move the banner, divider, input, response, and numbered-list bodies verbatim.

- [ ] Reduce `Habi` to component construction and orchestration:

```java
private final Storage storage;
private final TaskList tasks;
private final Ui ui;

public Habi(Path filePath) {
    ui = new Ui();
    storage = new Storage(filePath);
    TaskList loadedTasks;
    try {
        loadedTasks = new TaskList(storage.load());
    } catch (HabiException exception) {
        ui.showResponse(exception.getMessage());
        loadedTasks = new TaskList();
    }
    tasks = loadedTasks;
}

public static void main(String[] args) {
    new Habi(Path.of("data", "habi.txt")).run();
}
```

`run` reads with `Ui`, parses with `Parser`, changes `TaskList`, saves after
mutations, and catches `HabiException` at the command-loop boundary.

- [ ] Invoke `test-ui` and run Java 25 tests. Exact output must be unchanged.

- [ ] Commit, lightweight-tag, and push:

```bash
git add src/main/java src/test/java/HabiTest.java
git commit -m "Separate HABI component responsibilities" \
    -m "Command handling, persistence, UI, and task storage are concentrated in\none class.\n\nExtract the four required components so each responsibility can evolve and be\ntested independently without changing console behavior."
git tag A-MoreOOP
git push origin master A-MoreOOP
```

## Task 7: Complete `A-Packages`

**Files:** Move production files to `src/main/java/habi/` and the test to
`src/test/java/habi/`.

- [ ] Add `package habi;` as the first statement of every Java file and move it
under the matching `habi` directory. Do not make `src`, `main`, or `java` part
of the package name.

- [ ] Compile and test:

```bash
rm -rf /private/tmp/habi-package-classes
mkdir -p /private/tmp/habi-package-classes
javac -d /private/tmp/habi-package-classes \
    src/main/java/habi/*.java src/test/java/habi/HabiTest.java
java -cp /private/tmp/habi-package-classes habi.HabiTest
python3 .codex/skills/test-ui/scripts/run_ui_tests.py
```

Expected: all tests pass and the runner invokes `habi.Habi`.

- [ ] Commit, tag, and push:

```bash
git add src/main/java src/test/java test/ui-test-plan.md
git commit -m "Organize HABI classes into a package" \
    -m "The growing set of classes remains in the unnamed package.\n\nMove production and test classes under the habi package while retaining the\nstandard source roots required by Gradle."
git tag A-Packages
git push origin master A-Packages
```

## Task 8: Complete `A-Gradle`

**Files:** Merge `origin/add-gradle-support`; add Gradle wrapper and
`build.gradle`; preserve `.gitignore`; remove the added starter `Duke.java`.

- [ ] Merge the course branch:

```bash
git merge --no-ff origin/add-gradle-support
```

Resolve conflicts by retaining HABI's README and existing ignore rules while
accepting `build.gradle`, `gradlew*`, and `gradle/wrapper/*`. Delete
`src/main/java/Duke.java`; HABI's entry point already exists.

- [ ] Set the application entry point in `build.gradle`:

```groovy
application {
    mainClass = 'habi.Habi'
}
```

Keep the provided Java 25, JUnit 5, Shadow, `useJUnitPlatform()`, and standard
input settings unchanged.

- [ ] Build and run with Gradle:

```bash
./gradlew clean build
printf 'bye\n' | ./gradlew run --console=plain
```

Expected: `BUILD SUCCESSFUL` and HABI prints greeting and goodbye.

- [ ] Finish the merge commit, tag, and push:

```bash
git add .gitignore build.gradle gradle gradlew gradlew.bat src/main/java
git commit -m "Merge Gradle support for HABI builds" \
    -m "Manual compilation does not provide a repeatable project build.\n\nAdopt the course Gradle wrapper and configure it for Java 25 and habi.Habi so\nthe app can be built and run consistently."
git tag A-Gradle
git push origin master A-Gradle
```

Expected: the `A-Gradle` commit has two parents.

## Task 9: Complete `A-JUnit`

**Files:** Delete `src/test/java/habi/HabiTest.java`; create
`ParserTest.java` and `StorageTest.java` in the same directory.

- [ ] Create `ParserTest.java`:

```java
package habi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class ParserTest {
    @Test
    public void parseDeadline_validIsoDate_returnsDeadline() throws HabiException {
        Deadline deadline = Parser.parseDeadline(
                "deadline return book /by 2026-09-15");

        assertEquals("return book", deadline.getDescription());
        assertEquals(LocalDate.of(2026, 9, 15), deadline.getBy());
    }

    @Test
    public void parseDeadline_invalidDate_throwsHabiException() {
        HabiException exception = assertThrows(HabiException.class,
                () -> Parser.parseDeadline("deadline return book /by 15-09-2026"));

        assertEquals("OOPS! Use: deadline DESCRIPTION /by yyyy-MM-dd",
                exception.getMessage());
    }
}
```

- [ ] Create `StorageTest.java`:

```java
package habi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class StorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    public void saveThenLoad_multipleTaskTypes_restoresAllFields()
            throws HabiException {
        Storage storage = new Storage(
                temporaryDirectory.resolve("data/habi.txt"));
        Todo todo = new Todo("read book");
        todo.markAsDone();
        List<Task> originalTasks = List.of(
                todo,
                new Deadline("return book", LocalDate.of(2026, 9, 15)),
                new Event("project meeting", "Mon", "Tue"));

        storage.save(originalTasks);
        ArrayList<Task> loadedTasks = storage.load();

        assertEquals(3, loadedTasks.size());
        assertEquals("[T][X] read book", loadedTasks.get(0).toString());
        assertEquals("[D][ ] return book (by: Sep 15 2026)",
                loadedTasks.get(1).toString());
        assertEquals("[E][ ] project meeting (from: Mon to: Tue)",
                loadedTasks.get(2).toString());
    }
}
```

- [ ] Run `./gradlew test` and confirm the three JUnit methods are discovered.
Delete the old custom harness only after the new tests pass. Invoke `test-ui`
to preserve full console coverage.

- [ ] Commit, tag, and push:

```bash
git add src/test/java
git commit -m "Add JUnit tests for parsing and storage" \
    -m "The custom assertion harness is not reported as standard Gradle tests.\n\nCover deadline parsing and persistence with JUnit 5, including invalid input\nand round-trip behavior for all task types."
git tag A-JUnit
git push origin master A-JUnit
```

## Task 10: Complete `A-Jar`

**Files:** Modify `build.gradle`.

- [ ] Build the current Shadow JAR and verify RED:

```bash
./gradlew clean shadowJar
test -f build/libs/habi.jar
```

Expected: the file check fails because the provided configuration still names
the artifact `duke.jar`.

- [ ] Change the Shadow JAR filename:

```groovy
shadowJar {
    archiveFileName = 'habi.jar'
}
```

- [ ] Build and run from an empty directory:

```bash
./gradlew clean shadowJar
rm -rf /private/tmp/habi-jar-smoke
mkdir -p /private/tmp/habi-jar-smoke
cp build/libs/habi.jar /private/tmp/habi-jar-smoke/habi.jar
cd /private/tmp/habi-jar-smoke
printf 'bye\n' | java -jar habi.jar
```

Expected: greeting and goodbye; the temporary directory contains
`data/habi.txt`; no JAR is tracked in the repository.

- [ ] Commit, tag, and push:

```bash
cd /Users/zhai/Desktop/CS2103T/ip
git add build.gradle
git commit -m "Name the executable HABI JAR" \
    -m "The Gradle distribution still uses the starter chatbot filename.\n\nProduce habi.jar for the documented java -jar command while keeping generated\nbinaries out of Git."
git tag A-Jar
git push origin master A-Jar
```

## Task 11: Create the required parallel branches

- [ ] Create all branches from the same `A-Jar` commit:

```bash
git switch master
git branch branch-A-JavaDoc
git branch branch-A-CodingStandard
git branch branch-Level-9
git rev-parse branch-A-JavaDoc branch-A-CodingStandard branch-Level-9
```

Expected: all three hashes are identical.

## Task 12: Complete `A-JavaDoc` on its parallel branch

**Files:** Modify every class under `src/main/java/habi/` that lacks a header.

- [ ] Switch branches:

```bash
git switch branch-A-JavaDoc
```

- [ ] Add descriptive Javadoc to every class and every non-private constructor
or method. Leave private helpers unchanged because stretch goals are excluded.
Use this exact shape for parser methods:

```java
/**
 * Parses a deadline command containing an ISO date.
 *
 * @param command Complete deadline command.
 * @return Parsed deadline task.
 * @throws HabiException If the description or date is invalid.
 */
public static Deadline parseDeadline(String command) throws HabiException {
```

Getters, constructors, storage methods, UI methods, and task-list operations
all receive similarly specific first sentences. Do not change behavior.

- [ ] Verify documentation and behavior:

```bash
./gradlew javadoc test
python3 .codex/skills/test-ui/scripts/run_ui_tests.py
```

Expected: no Javadoc errors and all tests pass.

- [ ] Commit and push the branch; do not tag before merging:

```bash
git add src/main/java/habi
git commit -m "Document HABI classes and methods" \
    -m "Several component APIs require reading their implementations to learn their\ncontracts.\n\nDocument responsibilities, inputs, results, and recoverable errors for future\nmaintainers without changing runtime behavior."
git push -u origin branch-A-JavaDoc
```

## Task 13: Complete `A-CodingStandard` on its parallel branch

**Files:** Modify `TaskList.java`, Java imports, and lines exceeding the course
limit.

- [ ] Switch to the untouched parallel branch:

```bash
git switch branch-A-CodingStandard
```

- [ ] Expand the compact `TaskList` methods into the standard method layout:

```java
public void add(Task task) {
    tasks.add(task);
}

public Task get(int index) {
    return tasks.get(index);
}

public Task delete(int index) {
    return tasks.remove(index);
}

public int size() {
    return tasks.size();
}

public List<Task> asList() {
    return List.copyOf(tasks);
}
```

Replace any broad `Storage.java` imports with explicit imports:

```java
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
```

Run:

```bash
awk 'length($0) > 120 { print FNR ":" length($0) ":" FILENAME }' \
    src/main/java/habi/*.java
```

Wrap every printed statement using 8-space continuation indentation. Re-run
until there is no output. Verify package names are lowercase, class names use
PascalCase, variables/methods use camelCase, constants use
SCREAMING_SNAKE_CASE, imports are explicit and ordered, and conditionals and
loops use braces.

- [ ] Verify, commit, and push:

```bash
./gradlew test
python3 .codex/skills/test-ui/scripts/run_ui_tests.py
git diff --check
git add src/main/java/habi
git commit -m "Apply the Java coding standard" \
    -m "Import and layout choices do not fully meet the course standard.\n\nUse explicit ordered imports and required line wrapping to establish a\nconsistent baseline for future changes."
git push -u origin branch-A-CodingStandard
```

## Task 14: Complete `Level-9` on its parallel branch

**Files:** Modify `Habi.java`, `Parser.java`, `TaskList.java`, and UI plan;
create `TaskListTest.java`.

- [ ] Switch branches:

```bash
git switch branch-Level-9
```

- [ ] Create the failing JUnit test:

```java
package habi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

public class TaskListTest {
    @Test
    public void find_keywordInDescriptions_returnsMatchingTasks() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("buy groceries"));
        tasks.add(new Todo("return book"));

        List<Task> matches = tasks.find("book");

        assertEquals(2, matches.size());
        assertEquals("read book", matches.get(0).getDescription());
        assertEquals("return book", matches.get(1).getDescription());
    }
}
```

Add an exact UI case with input:

```text
todo read book
todo buy groceries
deadline return book /by 2026-09-15
find book
find
bye
```

Require matching output numbered from 1 and the empty-keyword error
`OOPS! Please provide a keyword for find.` Run tests and confirm RED.

- [ ] Add to `Parser`:

```java
public static String parseFindKeyword(String command) throws HabiException {
    String keyword = command.substring("find".length()).trim();
    if (keyword.isEmpty()) {
        throw new HabiException("OOPS! Please provide a keyword for find.");
    }
    return keyword;
}
```

Add to `TaskList`:

```java
public List<Task> find(String keyword) {
    return tasks.stream()
            .filter(task -> task.getDescription().contains(keyword))
            .toList();
}
```

Dispatch `find` in `Habi`, print heading
`Here are the matching tasks in your list:`, and do not save because search is
read-only.

- [ ] Invoke `test-ui`, run `./gradlew test`, commit, and push:

```bash
git add src/main/java/habi src/test/java/habi/TaskListTest.java \
    test/ui-test-plan.md
git commit -m "Find tasks by description keyword" \
    -m "Users must inspect the full list to locate related tasks.\n\nAdd the required find command using direct substring matching and number the\nmatching result list independently."
git push -u origin branch-Level-9
```

## Task 15: Merge, tag, and push the parallel increments

- [ ] Merge and publish JavaDoc:

```bash
git switch master
git merge --no-ff branch-A-JavaDoc \
    -m "Merge branch-A-JavaDoc for API documentation"
./gradlew javadoc test
python3 .codex/skills/test-ui/scripts/run_ui_tests.py
git tag A-JavaDoc
git push origin master branch-A-JavaDoc A-JavaDoc
```

- [ ] Merge and publish coding standard:

```bash
git merge --no-ff branch-A-CodingStandard \
    -m "Merge branch-A-CodingStandard for Java style"
./gradlew javadoc test
python3 .codex/skills/test-ui/scripts/run_ui_tests.py
git tag A-CodingStandard
git push origin master branch-A-CodingStandard A-CodingStandard
```

Resolve conflicts by retaining both documentation and explicit imports.

- [ ] Merge and publish find:

```bash
git merge --no-ff branch-Level-9 \
    -m "Merge branch-Level-9 for task search"
./gradlew javadoc test
python3 .codex/skills/test-ui/scripts/run_ui_tests.py
git tag Level-9
git push origin master branch-Level-9 Level-9
```

Resolve conflicts by retaining JavaDoc/style changes and all find behavior.

## Task 16: Final verification and upstream PR

- [ ] Run all Java 25 checks:

```bash
source /Users/zhai/.sdkman/bin/sdkman-init.sh
sdk use java 25.0.3.fx-zulu
./gradlew clean build javadoc shadowJar
python3 .codex/skills/test-ui/scripts/run_ui_tests.py
```

- [ ] Smoke-test persistence in the final JAR:

```bash
rm -rf /private/tmp/habi-final-jar
mkdir -p /private/tmp/habi-final-jar
cp build/libs/habi.jar /private/tmp/habi-final-jar/habi.jar
cd /private/tmp/habi-final-jar
printf 'todo read book\nbye\n' | java -jar habi.jar
printf 'list\nbye\n' | java -jar habi.jar
```

Expected: the second launch shows `1.[T][ ] read book`.

- [ ] Audit local and remote Git topology:

```bash
cd /Users/zhai/Desktop/CS2103T/ip
git status --short
git log --graph --decorate --oneline --all
git tag --list
git ls-remote --heads --tags origin
```

Expected: clean tree; all required branches remain; Week 3 tags exist remotely;
Level 7, Level 8, and the three parallel increments have merge commits.

- [ ] Check for the public upstream PR:

```bash
gh pr list --repo NUS-CS2103-AY2627-S1/ip \
    --head JiayiZhai:master --state all
```

If none exists, create exactly one:

```bash
gh pr create --repo NUS-CS2103-AY2627-S1/ip \
    --base master --head JiayiZhai:master \
    --title "[JiayiZhai] iP" --body ""
```

Expected: one PR from `JiayiZhai/ip:master` to the course `master`, titled
`[JiayiZhai] iP`.
