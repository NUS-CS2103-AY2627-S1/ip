---
name: seedu-java-coding-standard
description: Enforce the SE-EDU Java Coding Standard (Basic + Intermediate rules) for all Java source files in the project. Covers naming, layout, statements, and Javadoc comment rules from https://se-education.org/guides/conventions/java/intermediate.html.
---

# SE-EDU Java Coding Standard (Basic + Intermediate)

All Java code in this project must strictly comply with the [SE-EDU Java Coding Standard (Intermediate)](https://se-education.org/guides/conventions/java/intermediate.html). For any topics not explicitly covered in this specification, follow the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).

---

## 1. Naming Conventions

- **Packages**:
  - All lower case, dot-separated (e.g. `braun`, `braun.ui`, `braun.storage`, `braun.task`).
  - Never use `edu.nus.comp.*` or uppercase characters.
- **Classes & Enums**:
  - Must be nouns or noun phrases in `PascalCase` (e.g. `DateTimeUtil`, `Task`, `Storage`, `BraunException`).
- **Variables & Fields**:
  - Written in `camelCase` (e.g. `description`, `filePath`, `scanner`).
  - Variables with large scope must have descriptive, longer names; small scratch variables with local scope can be short (`i`, `j`, `k` for iterators, `c` for char).
  - Plural form for names representing collections or arrays (e.g. `tasks`, `lines`).
- **Constants**:
  - `SCREAMING_SNAKE_CASE` (e.g. `DIVIDER`, `INDENT`, `DISPLAY_DATE_FORMAT`).
  - Associated constants should share a common prefix (e.g. `COLOR_RED`, `COLOR_GREEN`).
- **Methods**:
  - Verbs or verb phrases in `camelCase` (e.g. `parseDate()`, `markAsDone()`, `toFileFormat()`).
  - Test methods follow three-part format: `featureUnderTest_testScenario_expectedBehavior()` (e.g. `parse_validIsoDateTime_parsedSuccessfully()`).
- **Abbreviations & Acronyms**:
  - Treat acronyms as regular words in camelCase/PascalCase; do not make them all-caps (e.g. `parseIsoDateTime()`, not `parseISODateTime()`; `Ui`, not `UI`).
- **Booleans**:
  - Variables and methods must sound like booleans with a prefix: `is`, `has`, `was`, `can`, `should` (e.g. `isDone`, `hasTime()`).
  - Boolean setters must follow `setFound(boolean isFound)`.
- **Language**:
  - All names and identifiers must be in English.

---

## 2. Layout & Formatting

- **Indentation**:
  - Exactly 4 spaces per indentation level. Never use tabs.
- **Line Length**:
  - Maximum 120 characters (soft limit: 110 characters).
  - Wrap lines when exceeding limits.
- **Wrapped Lines Indentation**:
  - Continuation lines must use **8 spaces** (twice the normal 4-space indent) relative to the parent line:
    ```java
    setText("Long line split "
            + "into two parts.");
    ```
- **Line Breaking Rules**:
  - Break after a comma `,`.
  - Break before an operator (`+`, `-`, `*`, `/`) or operator-like symbol (`.`, `&`, `|`).
  - A method/constructor name must stay attached to the open parenthesis `(` that follows it.
  - Prefer higher-level breaks to lower-level breaks.
- **Braces (Egyptian / K&R Style)**:
  - Opening brace `{` on the same line as declaration/statement.
  - Closing brace `}` on a line by itself (or aligned before `else`, `catch`, `finally`).
- **Loops & Conditionals**:
  - The loop and conditional body must **always** be wrapped in curly braces `{ ... }`, even for single statements.
  - The condition must be on a separate line; never inline a single-line body:
    ```java
    // Good:
    if (isDone) {
        doCleanup();
    }

    // Bad:
    if (isDone) doCleanup();
    ```
- **If-Else Structure**:
  ```java
  if (condition) {
      statements;
  } else if (anotherCondition) {
      statements;
  } else {
      statements;
  }
  ```

---

## 3. Statements

- **Package and Imports**:
  - Single-class imports only. **No wildcard imports** (e.g., never use `import java.util.*;`).
  - Unused imports must be deleted.
- **Variable Declarations**:
  - One variable per declaration line.
  - Initialize variables where declared, inside the smallest enclosing scope possible.
- **Class Fields Access**:
  - Class variables/fields should never be declared `public` unless constants (`static final`) or pure data classes with no behavior.

---

## 4. Comments & Javadoc

- **Language & Tone**:
  - Written in English using American spelling.
- **Javadoc Requirements**:
  - Header comments for all classes and public/protected methods.
  - Header comments for non-trivial private methods.
  - Can be omitted for trivial getters/setters, test methods, or `@inheritDoc` overrides.
- **Javadoc Format**:
  - Opening `/**` on its own line.
  - First sentence is a concise summary in 3rd-person singular present tense verb (`Returns ...`, `Adds ...`, `Parses ...`, not `Return` or `Returning`).
  - Blank line between description and tag section.
  - Tags (`@param`, `@return`, `@throws`) must include a descriptive phrase ending with punctuation (`.`).
  - No blank line between the Javadoc block and the class/method header.
