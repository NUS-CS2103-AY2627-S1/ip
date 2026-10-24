# Week 7 Optional Features Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Add an optional read-only AI help command, task priorities, and a clearer responsive GUI while preserving offline usability.

**Architecture:** Keep AI access behind a small service boundary so normal commands and tests do not require a network. Store task priority as task metadata with a backward-compatible default, and refine the existing FXML/CSS without replacing the JavaFX structure.

**Tech Stack:** Java 25, JavaFX 17, Gradle, JUnit 5, LangChain4j OpenAI-compatible client.

**Spec:** `docs/superpowers/specs/2026-09-30-week7-optional-features-design.md`

## Global Constraints

- Java 25 must be used for builds and tests.
- AI credentials must come from `LLM_API_KEY`; never commit credentials.
- Existing task files without priority data must continue loading.
- AI must be read-only and must not execute generated commands.
- Follow the SE-EDU intermediate Java coding standard.
- Use imperative, course-style Git commit subjects.

## Review Focus

- Missing AI key: return a useful setup response instead of throwing.
- AI network/provider failure: return a stable user-facing error.
- Existing storage rows without priority: load with normal priority.
- Invalid priority input: reject it without changing the task list.
- Narrow GUI window and long messages: preserve readable wrapping and usable input.

### Task 1: AI Help Service

**Files:**
- Create: `src/main/java/kiki/ai/AiService.java`
- Create: `src/main/java/kiki/ai/LangChainAiService.java`
- Modify: `build.gradle`
- Modify: `src/main/java/kiki/Kiki.java`
- Modify: `src/main/java/kiki/parser/Parser.java`
- Test: `src/test/java/kiki/parser/ParserTest.java`
- Test: `src/test/java/kiki/KikiTest.java`

**Interfaces:**
- `AiService.ask(String question) -> String`
- `Parser.parseAiQuestion(String input) -> String`
- `Kiki` accepts `@ai <question>` and delegates only the question text.

- [ ] Add parser tests for valid, empty, and whitespace-only `@ai` input.
- [ ] Add the LangChain4j dependency and implement environment-key validation.
- [ ] Add a service factory/default implementation that returns friendly messages for missing key and provider failures.
- [ ] Route `@ai` through Kiki as a read-only command.
- [ ] Run focused parser/Kiki tests and commit `Add optional AI help command`.

### Task 2: Task Priorities

**Files:**
- Create: `src/main/java/kiki/task/Priority.java`
- Modify: `src/main/java/kiki/task/Task.java`
- Modify: `src/main/java/kiki/parser/Parser.java`
- Modify: `src/main/java/kiki/storage/Storage.java`
- Modify: `src/main/java/kiki/Kiki.java`
- Test: `src/test/java/kiki/parser/ParserTest.java`
- Test: `src/test/java/kiki/storage/StorageTest.java`
- Test: `src/test/java/kiki/KikiTest.java`

**Interfaces:**
- `Priority` values `LOW`, `NORMAL`, `HIGH`.
- `Task.getPriority() -> Priority` and `Task.setPriority(Priority)`.
- `Parser.parsePriority(String input) -> Priority`.

- [ ] Add tests for accepted priority names, invalid values, and default priority.
- [ ] Add priority metadata to tasks with `NORMAL` as the backward-compatible default.
- [ ] Extend storage serialization/deserialization while accepting old rows.
- [ ] Add an explicit `priority` command and display priority in task output.
- [ ] Run storage and behavior tests and commit `Add task priority support`.

### Task 3: GUI Presentation

**Files:**
- Modify: `src/main/resources/view/MainWindow.fxml`
- Modify: `src/main/resources/css/main.css`
- Modify: `src/main/java/kiki/gui/MainWindow.java`
- Test: `src/test/java/kiki/gui/DialogBoxTest.java`

- [ ] Add a compact command hint and accessible button sizing without hiding the input.
- [ ] Improve message spacing, contrast, and responsive width constraints.
- [ ] Preserve automatic scrolling and `bye` behavior.
- [ ] Run JavaFX tests and manually launch the GUI with a long response.
- [ ] Commit `Polish the Kiki GUI presentation`.

### Final Verification

- [ ] Run `java -version` and confirm Java 25.
- [ ] Run `./gradlew test`.
- [ ] Run `./gradlew clean shadowJar`.
- [ ] Inspect `git diff --check` and the final commit subjects.
- [ ] Commit any documentation updates separately with a compliant subject.
