---
name: seedu-java-coding-standard
description: Enforces the SE-Education Java Coding Standard (Basic + Intermediate rules) based on https://se-education.org/guides/conventions/java/intermediate.html for all Java source and test files in this project.
---

# SE-EDU Java Coding Standard (Basic + Intermediate)

This skill provides the mandatory guidelines and rules for writing, formatting, refactoring, and reviewing Java code in this project, adhering to the SE-EDU Java Coding Standard (Basic + Intermediate).

Any aspects not covered below follow the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).

---

## 1. Naming Conventions

* **Packages**: Must be entirely in lowercase (e.g., `bro.command`, `bro.parser`, `bro.task`). Never use `edu.nus.*` for student projects.
* **Classes & Enums**: Must be nouns written in `PascalCase` (e.g., `TaskDateTime`, `UserInterface`, `Command`).
* **Methods**: Must be verbs or verb phrases written in `camelCase` (e.g., `parseCommand()`, `findTasksOnDate()`).
* **Variables & Parameters**: Must be written in `camelCase` (e.g., `matchingTasks`, `targetDate`). Avoid single-letter variable names except for temporary loop counters (`i`, `j`).
* **Constants**: Must be in `SCREAMING_SNAKE_CASE` (e.g., `DATA_FILE_PATH`, `DIVIDER_LINE`, `EVENT_PATTERN`).
* **Booleans**:
  * Boolean variables, fields, and methods must sound like booleans and be prefixed with `is`, `has`, `was`, `can`, or `should` (e.g., `isDone`, `hasTime()`, `isParsedDateTime()`).
  * Boolean setters must follow the form: `void setFound(boolean isFound)`.

---

## 2. Layout & Formatting

* **Indentation**: 4 spaces per indentation level. Do not use tab characters.
* **Line Length**:
  * **Soft limit**: 110 characters.
  * **Hard limit**: 120 characters. No line may exceed 120 characters.
* **Line Wrapping / Continuation**: When a line wraps, indent the continuation line by **8 spaces** (2 indentation levels) relative to the parent statement.
* **Braces (`{}` - K&R Style)**:
  * Opening brace `{` goes on the same line at the end of the declaration or control flow statement.
  * Closing brace `}` begins on a new line indented to match the opening statement.
* **Whitespace & Spacing**:
  * **Binary & Ternary Operators**: Must be surrounded by single spaces (e.g., `a = b + c;`, `condition ? val1 : val2;`).
  * **Reserved Keywords**: Java keywords (`if`, `for`, `while`, `switch`, `catch`) must be followed by a single space before the opening parenthesis (e.g., `if (condition)`, `while (isRunning)`).
  * **Commas & Semicolons**: Commas must be followed by a space. Semicolons in `for` headers must be followed by a space (e.g., `for (int i = 0; i < n; i++)`).

---

## 3. Statements & Control Flow

* **One Statement Per Line**: Never combine multiple statements on a single line.
* **Variable Declarations**:
  * Declare variables in the **smallest possible scope**.
  * Initialize variables immediately upon declaration where possible.
  * Do **not** use dummy / "phony" initial values if a logical value is not yet available; declare when ready.
* **Braces Mandatory**:
  * Control flow blocks (`if`, `else`, `for`, `while`, `do-while`) must **always** use braces `{}` even for single-line bodies. Never omit braces.
* **Switch Statements**:
  * Always include a `default` case.
* **Imports**:
  * **No Wildcard Imports**: `import java.util.*;` is strictly prohibited. Use explicit individual imports.
  * Static imports and regular imports should be cleanly separated.

---

## 4. Class Structure & Organization

Classes and interfaces must maintain a clean, standardized member order:

1. **Class / Interface Javadoc Comment**
2. **Class Header Statement**
3. **Class (`static`) Variables**: Ordered by visibility (`public` -> `protected` -> package-private -> `private`)
4. **Instance Variables**: Ordered by visibility (`public` -> `protected` -> package-private -> `private`)
   * Non-constant instance/static variables must **never** be `public`. Maintain encapsulation.
5. **Constructors**
6. **Methods**

### Modifier Ordering
Method and field modifiers must follow the standard sequence:
```
<access> static abstract final synchronized <unusual>
```
* *Correct*: `public static final String NAME = "Bro";`
* *Incorrect*: `static public final String NAME = "Bro";`

---

## 5. Comments & Javadoc

* **Required Javadoc**:
  * All public classes, enums, and interfaces.
  * All public, non-trivial methods and constructors.
  * Non-trivial fields or regular expressions whose purpose is not immediately self-evident.
* **Allowed Javadoc Omissions**:
  * Trivial getters and setters.
  * Overriding methods (`@Override`) if the superclass contract applies unmodified.
  * Test classes and test methods.
* **Content Quality**:
  * Comments should explain the *why* (rationale and intent) rather than merely echoing obvious code syntax.