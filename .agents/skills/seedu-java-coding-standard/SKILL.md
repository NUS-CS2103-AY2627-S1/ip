---
name: seedu-java-coding-standard
description: >-
  Enforces the SE-EDU Java Coding Standard (Basic + Intermediate) based on https://se-education.org/guides/conventions/java/intermediate.html.
  Use when writing, refactoring, reviewing, or generating Java code in this project.
---

# SE-EDU Java Coding Standard (Basic + Intermediate)

This skill provides the mandatory Java coding rules and conventions defined by the SE-EDU initiative.

---

## 1. Naming Conventions

### Packages
* All lowercase words (e.g., `penny.command`, `penny.task`, `penny.ui`).
* Root package must be the project name (e.g., `penny`), not `edu.nus.comp.*`.

### Classes, Enums, Interfaces, Records
* Must be nouns in **PascalCase** (e.g., `TaskList`, `Deadline`, `Storage`).
* Acronyms and abbreviations are camel-cased (e.g., `HtmlParser`, `DvdPlayer`, **not** `HTMLParser`).

### Variables and Parameters
* Must be in **camelCase** (e.g., `taskDescription`, `storageFilePath`).
* Variables with large scope must have descriptive names. Short scratch variables (`i`, `j`, `k`, `c`, `d`) are allowed only for small local scopes/loops.
* Plural form for collections/arrays (e.g., `List<Task> tasks`, `String[] parts`).
* **Boolean variables/getters**: Must use prefixes like `is`, `has`, `was`, `can`, `should` (e.g., `isDone`, `hasTime()`, `canEvaluate()`).
* **Boolean setters**: Must follow `void setDone(boolean isDone)`.

### Constants
* Must be in **`SCREAMING_SNAKE_CASE`** (e.g., `MAX_CAPACITY`, `DISPLAY_DATE_FORMATTER`).
* Related constants should share a common prefix (e.g., `COLOR_RED`, `COLOR_GREEN`).

### Methods
* Must be verbs in **camelCase** (e.g., `readCommand()`, `execute()`, `delete()`).

### Test Methods
* May use underscores following the 3-part naming convention:
  `featureUnderTest_testScenario_expectedBehavior()`
  (e.g., `delete_validIndex_taskRemovedSuccessfully()`, `parse_emptyInput_exceptionThrown()`).

---

## 2. Layout & Formatting

### Indentation & Whitespace
* **Indentation**: Exactly 4 spaces (no tabs).
* **Line Wrapping Indentation**: Exactly 8 spaces (twice the standard indentation).
* **Line Length**: Soft limit 110 characters, hard limit 120 characters.
* **Brace Style**: K&R / Egyptian style (`{` at the end of the line, `}` on a new line).
* **Logical Spacing**: Separate distinct logical units with exactly one blank line.
* **Operators & Commas**: Space after commas (`a, b, c`), space around binary operators (`a + b`).
* **Parentheses**: Keep method name attached to opening parenthesis `(` with no space before it (e.g., `computeTotal(a, b)`).

---

## 3. Statements & Control Flow

### Import Statements
* Put every class in an explicit package.
* **No wildcard imports** (e.g., `import java.util.*;` is strictly forbidden). Always import classes explicitly (e.g., `import java.util.List;`).
* Keep import ordering consistent: static imports first, followed by third-party / standard library imports alphabetically.

### Types & Variable Declarations
* Array brackets attach to the type, not the variable (e.g., `int[] values`, **not** `int values[]`).
* Declare variables in the smallest possible scope and initialize them at declaration whenever possible.
* Class fields must never be `public` (except constants or pure data records without behavior).

### Conditionals & Loops
* Always wrap conditional and loop bodies with curly brackets `{ ... }`, even for single-line statements.
* Conditionals must be on a separate line from the condition:
  ```java
  if (isDone) {
      doCleanup();
  }
  ```
* In `switch` statements, if a `case` intentionally falls through, it must have an explicit `// Fallthrough` comment.

---

## 4. Javadoc & Code Comments

### Language & Tone
* All comments and Javadocs must be written in **English** using American spelling.

### Javadoc Structure & Formatting
* Mandatory for all classes, public/protected methods, and constructors (not required for private methods).
* First sentence must be a concise summary starting with a verb in **third-person singular present tense** (e.g., `Returns the...`, `Adds a...`, `Parses the...`, `Constructs a...`).
* Leave an empty line between description and `@param` / `@return` / `@throws` tags.
* Parameter and return descriptions must end with punctuation (period).
* No blank line between the Javadoc closing `*/` and the class/method declaration.
* Single-line Javadoc `/** Description. */` is allowed for simple fields.

```java
/**
 * Executes the task deletion command.
 *
 * @param tasks The task list from which the task will be deleted.
 * @param ui The user interface for rendering user feedback.
 * @param storage The storage handler for saving tasks.
 * @throws PennyException If the index is out of bounds.
 */
public void execute(TaskList tasks, Ui ui, Storage storage) throws PennyException {
    // ...
}
```
