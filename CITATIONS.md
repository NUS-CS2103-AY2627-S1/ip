# CITATIONS.md — Citation To-Do List

Tracks items requiring citation under the course reuse policy.
Rules and agent protocol are in CITATION_GUIDE.md.

Entries can be added by the agent (upon approval) or by the user directly.
If you add an entry yourself, the agent can help place the citation in code or documentation —
just point to the entry by ID.

---

## Entry Format

```
### [C-NNN] Short description
- **Type:** AI-generated | Adapted | Library | Reused
- **Location:** `path/to/file.ext` — function or line reference
- **Source:** [tool name, URL, repo, or description of origin]
- **Citation format:** inspired by | adapted from | @@author-reused | README/DG | comment only
- **Notes:** [any relevant detail — e.g. extent of AI use, which parts were modified]
- **Status:** Pending | Done
```

---

## Pending

---

## Done

### [C-001] Invalid task-index input validation
- **Type:** AI-generated
- **Location:** `src/main/java/clara/Clara.java` — command error handling
- **Source:** Codex AI assistance
- **Citation format:** comment only
- **Notes:** Small change to show a user-facing error for non-numeric `mark` or `unmark` indexes.
- **Status:** Approved, done

### [C-002] Task-command syntax validation
- **Type:** AI-generated
- **Location:** `src/main/java/clara/parser/Parser.java` — task command parsing methods
- **Source:** Codex AI assistance
- **Citation format:** comment only
- **Notes:** Validation for blank descriptions and malformed deadline/event delimiters.
- **Status:** Approved, done

### [C-003] Consistent leading newline for Clara messages
- **Type:** AI-generated
- **Location:** `src/main/java/clara/Clara.java` — farewell and error output
- **Source:** Codex AI assistance
- **Citation format:** comment only
- **Notes:** Adds a leading newline to remaining Clara-prefixed messages.
- **Status:** Approved, done

### [C-004] Task persistence and saved-task parsing
- **Type:** AI-generated
- **Location:** `src/main/java/clara/storage/TodoFileHandler.java` — task serialization, parsing, loading, and saving methods
- **Source:** Codex AI assistance and https://chatgpt.com/share/6a801443-b818-83ec-9c19-d1e74a1bc7e9
- **Citation format:** comment only
- **Notes:** Assistance with the delimited save-file format, validation, and Java NIO buffered file I/O.
- **Status:** Approved, done

### [C-005] `LocalDateTime` methods and patterns
- **Type:** AI-generated
- **Location:** `src/main/java/clara/parser/Parser.java` — parsing user input into `LocalDateTime`; `src/main/java/Deadline.java`, `src/main/java/Event.java` — formatting `LocalDateTime`
- **Source:** https://chatgpt.com/share/6a8013d9-a11c-83ec-9940-edc0bf614544
- **Citation format:** comment only
- **Notes:** Assistance with looking up class methods and demonstrating implementation patterns.
- **Status:** Done

### [C-006] Task find implementation
- **Type:** AI-generated
- **Location:** `src/main/java/clara/Clara.java` — task finding method
- **Source:** https://chatgpt.com/share/6a8023ed-204c-83ec-a9a8-3b4df596f31d
- **Citation format:** comment only
- **Notes:** Assistance with implementing task find and functional pattern.
- **Status:** Done

### [C-007] `TaskList` extraction and collection operations
- **Type:** AI-generated
- **Location:** `src/main/java/clara/task/TaskList.java` — task collection management operations
- **Source:** Antigravity AI assistance
- **Citation format:** comment only
- **Notes:** Extraction and refactoring of `TaskList` to encapsulate task collection operations decoupled from UI and storage.
- **Status:** Done

### [C-008] `Ui` console interaction extraction
- **Type:** AI-generated
- **Location:** `src/main/java/clara/ui/Ui.java` — user interaction, command reading, and message formatting methods
- **Source:** Antigravity AI assistance
- **Citation format:** comment only
- **Notes:** Extraction and implementation of `Ui` class for centralized console input/output formatting.
- **Status:** Done

### [C-009] Main application loop refactoring
- **Type:** AI-generated
- **Location:** `src/main/java/clara/Clara.java` — `main` method application loop
- **Source:** Antigravity AI assistance
- **Citation format:** comment only
- **Notes:** Refactored `Clara.main` to coordinate `Ui`, `TaskList`, `Parser`, and `TodoFileHandler`.
- **Status:** Done

### [C-010] JUnit test suite implementation
- **Type:** AI-generated
- **Location:** `src/test/java/clara/parser/ParserTest.java`, `src/test/java/clara/task/TaskListTest.java`, `src/test/java/clara/task/DeadlineTest.java`, `src/test/java/clara/task/TodoTest.java`, `src/test/java/clara/task/EventTest.java`
- **Source:** Antigravity AI assistance
- **Citation format:** comment only
- **Notes:** Creation of JUnit 5 test classes for `Parser`, `TaskList`, and task models (`Deadline`, `Todo`, `Event`).
- **Status:** Done

### [C-011] Explicit Locale in DateTimeFormatter
- **Type:** AI-generated
- **Location:** `src/main/java/clara/task/Deadline.java`, `src/main/java/clara/task/Event.java` — `toString` date formatting
- **Source:** Antigravity AI assistance
- **Citation format:** comment only
- **Notes:** Added `Locale.US` to `DateTimeFormatter.ofPattern` to ensure consistent English month names across different system locales.
- **Status:** Done

### [C-012] Javadoc documentation for ClaraException
- **Type:** AI-generated
- **Location:** `src/main/java/clara/exception/ClaraException.java` — class and constructor Javadoc comments
- **Source:** Antigravity AI assistance
- **Citation format:** comment only
- **Notes:** Added Javadoc comments to `ClaraException` class and constructor.
- **Status:** Done

### [C-013] JavaFX GUI and command-response integration
- **Type:** AI-generated
- **Location:** `src/main/java/clara/Clara.java` — GUI command-response bridge and exit signal; `src/main/java/clara/gui/Main.java`, `MainWindow.java`, and `DialogBox.java`; `src/main/resources/view/MainWindow.fxml` and `DialogBox.fxml`; `src/main/resources/css/main.css` and `dialog-box.css`
- **Source:** Codex AI assistance
- **Citation format:** comment only
- **Notes:** JavaFX GUI implementation and the adaptation that exposes Clara command responses to the GUI. The FXML structure follows the SE-EDU JavaFX tutorial, which is course material and does not require a reuse citation.
- **Status:** Approved, done

### [C-014] Clara GUI avatar illustrations
- **Type:** AI-generated
- **Location:** `src/main/resources/images/Clara.png`, `src/main/resources/images/ClaraUser.png`; `README.md` — Acknowledgements
- **Source:** OpenAI image generation through Codex
- **Citation format:** README/DG
- **Notes:** Original avatar illustrations generated for Clara's JavaFX dialog boxes.
- **Status:** Approved, done

### [C-015] Vanilla Java invariant assertions
- **Type:** AI-generated
- **Location:** `src/main/java/clara/task/Task.java`, `Deadline.java`, `Event.java`, and `TaskList.java`; corresponding task tests
- **Source:** Codex AI assistance
- **Citation format:** comment only
- **Notes:** Added assertions for task-model invariants and tests that verify assertion failures.
- **Status:** Approved, done

### [C-016] Code-quality refactoring
- **Type:** AI-generated
- **Location:** `src/main/java/clara/Clara.java`, `src/main/java/clara/parser/Parser.java`, `src/main/java/clara/storage/TodoFileHandler.java`, `src/main/java/clara/task/Task.java`, `src/main/java/clara/task/TaskList.java`, `src/main/java/clara/task/Deadline.java`, `src/main/java/clara/task/Event.java`, `src/test/java/clara/parser/ParserTest.java`, and `src/test/java/clara/task/TaskListTest.java` — code-quality refactors
- **Source:** Codex AI assistance
- **Citation format:** comment only
- **Notes:** Refactoring to improve readability and comply with the course code-quality and Java coding standards: immutable formatter constants, extracted duplicate parsing and validation, centralized task-index conversion, removal of unnecessary qualifiers and undocumented capacity, and descriptive test-variable names.

### [C-017] Error-handling validation and resilient task persistence
- **Type:** AI-generated
- **Location:** `src/main/java/clara/parser/Parser.java` — strict date parsing, blank find-query validation, and event-range validation; `src/main/java/clara/storage/TodoFileHandler.java` — safe save-file replacement and complete saved-task validation; `src/main/java/clara/Clara.java` — task-state rollback when persistence fails; `src/test/java/clara/parser/ParserTest.java` — corresponding parser tests
- **Source:** Codex AI assistance
- **Citation format:** comment only
- **Notes:** Added user-facing validation for blank search queries, non-existent dates, and event ranges; hardened save loading and writing; and added rollback logic to prevent in-memory task state from diverging when saving fails.

### [C-018] Task priority feature
- **Type:** AI-generated
- **Location:** `src/main/java/clara/task/Priority.java`, `Task.java`,
  `src/main/java/clara/storage/TodoFileHandler.java`, `src/main/java/clara/Clara.java`,
  `src/main/java/clara/parser/Parser.java`, and corresponding task/parser tests; `README.md` and
  `docs/README.md`
- **Source:** Codex AI assistance
- **Citation format:** README/DG + comment only
- **Notes:** Added task priority levels, display formatting, persistence, the `priority` command,
  parser validation, regression tests, and user documentation.
- **Status:** Approved, done

### [C-019] GUI polish and input-error styling
- **Type:** AI-generated
- **Location:** `src/main/java/clara/Clara.java` — GUI input-error response state; `src/main/java/clara/gui/Main.java`, `MainWindow.java`, and `DialogBox.java`; `src/main/resources/view/MainWindow.fxml` and `DialogBox.fxml`; `src/main/resources/css/main.css` and `dialog-box.css`
- **Source:** Codex AI assistance
- **Citation format:** comment only
- **Notes:** Refined the JavaFX interface with an asymmetric command-and-response layout, compact circular assistant avatar, responsive message widths, improved composer and header styling, explicit window resize bounds, and distinct input-error presentation.
- **Status:** Approved, done
