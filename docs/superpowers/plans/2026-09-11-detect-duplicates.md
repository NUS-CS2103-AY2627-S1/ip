# C-DetectDuplicates Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Warn about an exact duplicate task while still adding and persisting it.

**Architecture:** Give each task type responsibility for comparing its own user-visible details, expose a collection query through `TaskList`, and let `Pavanmaxxer` choose the response before performing the existing add-and-save flow. Keep command syntax and storage unchanged.

**Tech Stack:** Java 25, Gradle, JUnit 5, Checkstyle

**Spec:** `docs/superpowers/specs/2026-09-11-detect-duplicates-design.md`

## Global Constraints

- Use Java 25 via `sdk use java 25.0.3.fx-zulu`.
- Warn but still append and persist duplicates.
- Match concrete type and case-insensitive description; also match deadline date or event start/end details.
- Ignore completion status during matching.
- Do not change command syntax or the storage format.
- Do not commit or push without Pavan's explicit authorization.

---

### Task 1: Define task-detail matching

**Files:**
- Modify: `src/main/java/pavanmaxxer/Task.java`
- Modify: `src/main/java/pavanmaxxer/Deadline.java`
- Modify: `src/main/java/pavanmaxxer/Event.java`
- Modify: `src/main/java/pavanmaxxer/TaskList.java`
- Test: `src/test/java/pavanmaxxer/TaskListTest.java`

**Interfaces:**
- Produces: `public boolean Task.hasSameDetails(Task other)`
- Produces: subtype overrides in `Deadline` and `Event`
- Produces: `public boolean TaskList.containsSameDetails(Task candidate)`

- [x] **Step 1: Add failing collection-level matching tests**

  Add these tests to `TaskListTest`:

  ```java
  @Test
  void containsSameDetails_todoWithDifferentCase_returnsTrue() {
      TaskList tasks = new TaskList();
      tasks.add(new Todo("read book"));

      assertTrue(tasks.containsSameDetails(new Todo("READ BOOK")));
  }

  @Test
  void containsSameDetails_sameDescriptionDifferentType_returnsFalse() {
      TaskList tasks = new TaskList();
      tasks.add(new Todo("return book"));

      assertFalse(tasks.containsSameDetails(new Deadline(
              "return book", LocalDate.parse("2026-09-20"))));
  }

  @Test
  void containsSameDetails_deadlineWithDifferentDate_returnsFalse() {
      TaskList tasks = new TaskList();
      tasks.add(new Deadline(
              "return book", LocalDate.parse("2026-09-20")));

      assertFalse(tasks.containsSameDetails(new Deadline(
              "RETURN BOOK", LocalDate.parse("2026-09-21"))));
  }

  @Test
  void containsSameDetails_eventWithDifferentCase_returnsTrue() {
      TaskList tasks = new TaskList();
      tasks.add(new Event("meeting", "2PM", "4PM"));

      assertTrue(tasks.containsSameDetails(
              new Event("MEETING", "2pm", "4pm")));
  }
  ```

- [x] **Step 2: Run the focused tests and confirm the expected failure**

  Run:

  ```bash
  ./gradlew test --tests pavanmaxxer.TaskListTest
  ```

  Expected: compilation fails because `containsSameDetails` does not exist.

- [x] **Step 3: Add the minimal comparison methods**

  Add this common comparison to `Task`:

  ```java
  public boolean hasSameDetails(Task other) {
      return other != null
              && getClass().equals(other.getClass())
              && description.equalsIgnoreCase(other.description);
  }
  ```

  Override it in `Deadline` to require `super.hasSameDetails(other)` and an
  equal `by` date:

  ```java
  @Override
  public boolean hasSameDetails(Task other) {
      if (!(other instanceof Deadline deadline)) {
          return false;
      }
      return super.hasSameDetails(other) && by.equals(deadline.by);
  }
  ```

  Override it in `Event` to require the common match plus case-insensitive
  equality for `from` and `to`:

  ```java
  @Override
  public boolean hasSameDetails(Task other) {
      if (!(other instanceof Event event)) {
          return false;
      }
      return super.hasSameDetails(other)
              && from.equalsIgnoreCase(event.from)
              && to.equalsIgnoreCase(event.to);
  }
  ```

  Add this query to `TaskList`:

  ```java
  public boolean containsSameDetails(Task candidate) {
      assert candidate != null : "candidate task must not be null";
      return tasks.stream().anyMatch(candidate::hasSameDetails);
  }
  ```

- [x] **Step 4: Run the focused tests**

  Run:

  ```bash
  ./gradlew test --tests pavanmaxxer.TaskListTest
  ```

  Expected: all `TaskListTest` tests pass.

### Task 2: Warn while preserving add and persistence behaviour

**Files:**
- Modify: `src/main/java/pavanmaxxer/Pavanmaxxer.java`
- Test: `src/test/java/pavanmaxxer/PavanmaxxerTest.java`

**Interfaces:**
- Consumes: `TaskList.containsSameDetails(Task candidate)`
- Preserves: `private String addTask(String input, Command command)`

- [x] **Step 1: Add a failing application-level duplicate test**

  Add this test to `PavanmaxxerTest`:

  ```java
  @Test
  void getResponse_addDuplicate_warnsButAddsAndPersistsTask() {
      Path dataFile = temporaryDirectory.resolve("tasks.txt");
      Pavanmaxxer pavanmaxxer = new Pavanmaxxer(dataFile);
      pavanmaxxer.getResponse("todo read book");

      assertEquals("This task already exists in your list, but I've added it again:\n"
                      + "  [T][ ] READ BOOK\n"
                      + "Now you have 2 tasks in the list.",
              pavanmaxxer.getResponse("todo READ BOOK"));

      Pavanmaxxer reloadedPavanmaxxer = new Pavanmaxxer(dataFile);
      assertEquals("1.[T][ ] read book\n2.[T][ ] READ BOOK",
              reloadedPavanmaxxer.getResponse("list"));
  }
  ```

- [x] **Step 2: Run the focused test and confirm the expected failure**

  Run:

  ```bash
  ./gradlew test --tests pavanmaxxer.PavanmaxxerTest
  ```

  Expected: the duplicate addition returns the ordinary `Got it` response.

- [x] **Step 3: Select the response before the existing add-and-save flow**

  In `addTask`, compute `boolean isDuplicate = tasks.containsSameDetails(task)`
  before `tasks.add(task)`. Always call `tasks.add(task)` and
  `storage.save(tasks)`. Use this introduction for a duplicate:

  ```java
  String introduction = isDuplicate
          ? "This task already exists in your list, but I've added it again:"
          : "Got it. I've added this task:";
  ```

  Build the existing response using `introduction`, the task, and the updated
  count.

- [x] **Step 4: Run the focused tests**

  Run:

  ```bash
  ./gradlew test --tests pavanmaxxer.PavanmaxxerTest
  ```

  Expected: all `PavanmaxxerTest` tests pass.

### Task 3: Document and verify the extension

**Files:**
- Modify: `docs/README.md`

**Interfaces:**
- Documents: duplicate definition, warning, and intentional insertion behaviour

- [x] **Step 1: Add the user-facing duplicate section**

  Add this section to `docs/README.md`:

  ````markdown
  ## Adding duplicate tasks

  Pavanmaxxer warns when a task has the same type and details as an existing
  task. The duplicate is still added and saved.

  Example: `todo read book`

  ```text
  This task already exists in your list, but I've added it again:
    [T][ ] read book
  Now you have 2 tasks in the list.
  ```
  ````

- [x] **Step 2: Run full verification**

  Run:

  ```bash
  ./gradlew clean test checkstyleMain checkstyleTest shadowJar
  git diff --check
  ```

  Expected: all tests and Checkstyle pass, `git diff --check` prints nothing,
  and `build/libs/pavanmaxxer.jar` is larger than 5 MB.

- [x] **Step 3: Review and stop at the commit boundary**

  Confirm that only the planned source, test, documentation, specification, and
  plan files changed. Present the diff and verification evidence to Pavan. Do
  not stage, commit, tag, push, or open a PR until Pavan explicitly authorizes
  each required repository action.
