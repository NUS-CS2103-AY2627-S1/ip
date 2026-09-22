---
name: seedu-java-coding-standard
description: >-
  Enforces the SE-EDU Java Coding Standard (Basic + Intermediate) based on se-education.org guidelines.
  Mandates naming conventions, formatting, statement structure, Javadoc comments, and clean code layout.
---

# SE-EDU Java Coding Standard (Basic + Intermediate)

Reference: [SE-EDU Java Coding Standard](https://se-education.org/guides/conventions/java/intermediate.html)

This skill provides mandatory coding conventions and style rules for Java projects in SE-EDU.

---

## 1. Naming Conventions

* **Packages**: All lowercase alphanumeric characters, dot-separated (e.g. `oz.task`, `oz.storage`).
* **Classes & Interfaces**: Nouns or noun phrases written in `PascalCase` (e.g. `TaskList`, `TaskDateTime`, `OzException`).
* **Methods**: Verbs or verb phrases written in `camelCase` (e.g. `toDisplayString()`, `findTasksOn()`, `checkIndex()`).
  * **Test Methods**: Follow the pattern `featureUnderTest_testScenario_expectedBehavior()` (e.g. `delete_outOfBoundsIndex_exceptionThrown()`).
* **Variables & Parameters**: Written in `camelCase` (e.g. `taskList`, `targetDate`, `storageFile`).
  * Avoid non-standard abbreviations (use `description` instead of `desc`, `argument` instead of `arg`).
  * Avoid single-letter variable names except for standard loop indices (`i`, `j`, `k`).
* **Constants (`static final`)**: Written in `SCREAMING_SNAKE_CASE` (e.g. `COMMAND_PATTERN`, `DISPLAY_DATE_FORMAT`).
* **Booleans**: Name booleans as assertions or questions returning boolean values (e.g. `isDone`, `hasTime`, `occursOn`).

---

## 2. Layout & Formatting

* **Indentation**: Exactly 4 spaces per indentation level. Do not use tab characters (`\t`).
* **Line Length**:
  * **Soft limit**: 110 characters.
  * **Hard limit**: 120 characters.
* **Indentation for Wrapped Lines (Continuation Indent)**: 8 spaces (twice the standard indentation) relative to the start of the wrapped line.
* **Brace Style (1TBS / K&R)**:
  * Opening brace `{` appears on the same line as the declaration or statement.
  * Closing brace `}` starts on a new line, aligned with the statement header.
  * `else`, `catch`, `finally` appear on the same line as the preceding closing brace: `} else {`, `} catch (...) {`, `} finally {`.
* **Single-line blocks**: Always use braces `{}` for `if`, `else`, `for`, `while`, `do` blocks even when the body contains only one statement.

---

## 3. Statements & Declarations

* **One Declaration Per Line**: Each variable declaration must declare only one variable.
* **Array Notation**: Array brackets must attach to the type, not the variable (e.g. `String[] args`, not `String args[]`).
* **Imports**:
  * No wildcard imports (`import java.util.*;` is forbidden; use explicit imports like `import java.util.ArrayList;`).
  * Group imports cleanly: standard Java packages first, followed by third-party packages, followed by project-specific packages.
* **Switch Statements**:
  * Every switch branch must terminate with `break`, `return`, `throw`, or a clear `// fall through` comment.
  * A `default` branch is required.

---

## 4. Comments & Documentation

* **Javadoc**:
  * Mandatory for all classes, interfaces, enums, records.
  * Mandatory for all public and protected methods, constructors, and fields.
  * Mandatory for non-trivial private methods.
  * Include `@param`, `@return`, and `@throws` tags where applicable.
* **Single blank lines**: Use single blank lines between method definitions and logical code blocks. Never place consecutive blank lines.
