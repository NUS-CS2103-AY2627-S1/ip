---
name: seedu-java-coding-standard
description: >-
  Use this skill whenever writing, modifying, reviewing, or refactoring Java code in this project
  to adhere to the SE-EDU Java Coding Standard (Basic + Intermediate rules) and the Google Java Style Guide.
---

# SE-EDU Java Coding Standard (Basic + Intermediate)

This guide documents the coding standard to follow when writing Java code for SE-EDU projects. For any topics not explicitly covered here, follow the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).

---

## 1. Naming Conventions

### Packages
* Package names must be **all lowercase**, without underscores or special characters (e.g., `bob.command`, `bob.parser`).
* For course/school projects, the root package name should be the project name or group name (e.g., `bob`), never `edu.nus.comp.*`.

### Classes, Interfaces, Enums, and Records
* Must be nouns or noun phrases written in **PascalCase** (e.g., `TaskStorage`, `AddCommand`, `Deadline`).
* Interface names should describe capabilities or roles (e.g., `Storage<T>`, `Iterable<Task>`).
* Exception classes must end with `Exception` (e.g., `BobException`).

### Methods
* Method names must be verbs or verb phrases written in **camelCase** (e.g., `execute`, `readCommand`, `showTaskList`).
* Methods returning a boolean must sound like boolean queries (e.g., `isExit()`, `isEmpty()`, `hasNextCommand()`). Avoid negative names like `isNotDone()`.
* Test method names should clearly convey feature, scenario, and expected outcome (e.g., `execute_validTask_addsTaskAndSavesToStorage()` or `parse_emptyCommand_throwsBobException()`).

### Variables and Parameters
* Variable and parameter names must be written in **camelCase** (e.g., `taskId`, `fullCommand`, `taskList`).
* Boolean variables must sound like booleans (e.g., `isDone`, `hasError`, `isExit`).
* Collections and arrays should use plural names (e.g., `tasks`, `lines`, `parts`).
* Avoid cryptic abbreviations. Loop counters may use `i`, `j`, `k` (with `j`, `k` reserved for nested loops).
* Single-character variable names outside short loop counters are disallowed.

### Constants
* Static final constants must be in **`SCREAMING_SNAKE_CASE`** (e.g., `INPUT_FORMATTER`, `OUTPUT_FORMATTER`, `FILE_PATH`).
* Related constants should share a common prefix.

---

## 2. Layout & Formatting

### Indentation and Spacing
* Use **4 spaces** for indentation. **Never use tabs**.
* Indent wrapped / continuation lines by **8 spaces** (twice the normal indentation level).
* Binary operators (`+`, `-`, `==`, `&&`, etc.) and assignment operators (`=`) must be surrounded by single spaces.
* Commas and semicolons are followed by a space, but not preceded by one.
* No trailing whitespace at the end of lines.
* Files must end with a single newline character.

### Line Length
* **Soft limit**: 110 characters.
* **Hard limit**: 120 characters.
* Break long lines before operators or after commas when wrapping.

### Braces (1TBS / K&R Style)
* Opening braces `{` must appear on the same line as the declaration or control statement.
* Closing braces `}` must appear on a new line, aligned with the start of the opening statement.
* `else`, `catch`, and `finally` keywords are placed on the same line as the preceding closing brace:
  ```java
  if (condition) {
      // ...
  } else {
      // ...
  }
  ```
* **Mandatory Braces**: All control flow statements (`if`, `else`, `for`, `while`, `do-while`) **must** always use braces `{}` even for single-statement bodies. Single-line statement bodies on the same line as `if` are forbidden.

### Blank Lines
* Use a single blank line to separate methods, constructors, and logical blocks of code within methods.
* Avoid multiple consecutive blank lines.

---

## 3. Statements & Declarations

### Package and Imports
* Every class must be part of a named package (no default package).
* **No wildcard imports** (e.g., `import java.util.*;` is prohibited). Explicitly import individual types.
* Order imports logically: standard Java imports, third-party libraries, followed by project packages. No unused imports.

### Variable Declarations
* Declare one variable per line.
* Declare variables close to their first use and initialize them where declared whenever possible.
* Use **interface types** for variable declarations, field types, parameter types, and return types where applicable:
  ```java
  List<Task> tasks = new ArrayList<>(); // Preferred over ArrayList<Task>
  ```
* Array brackets belong to the type, not the variable name:
  ```java
  String[] parts; // Preferred
  String parts[]; // Prohibited
  ```

### Switch Statements
* Always include a `default` branch in `switch` statements unless all enum values are explicitly handled.
* Document intentional fall-through cases with a comment.

---

## 4. Documentation & Comments

### Javadoc Comments
* All `public` classes, interfaces, enums, records, and `public`/`protected` methods and constructors must have descriptive Javadoc comments.
* Omit redundant Javadoc for trivial getters/setters or standard `@Override` methods when the inherited doc is sufficient.
* Javadoc format:
  * Starts with `/**` on its own line.
  * First sentence must be a concise summary ending with a period `.`.
  * Method summaries should use **third-person declarative verbs** (e.g., `Returns the task list.`, `Executes the command.`, `Parses the input string.`).
  * Include `@param`, `@return`, and `@throws` tags where applicable, indented and preceded by a blank line before the tag block.

### Implementation Comments
* Code should be self-explanatory through good naming and structure.
* Use inline/block comments to explain *why* something is done (rationale, non-obvious design choices, edge cases), not *what* is done.
* Do not leave commented-out code in production files.

