---
name: seedu-java-coding-standard
description: Java Coding Standard guidelines based on SE-EDU intermediate conventions for CS2103T projects.
---

# SE-EDU Java Coding Standard (Intermediate)

This skill mandates the Java coding conventions based on the [SE-EDU Java Coding Standard](https://se-education.org/guides/conventions/java/intermediate.html).

## 1. Naming Conventions

- **Packages**: Always all lowercase (e.g., `bobo.task`, `bobo.ui`).
- **Classes / Interfaces / Enums**: `PascalCase` (UpperCamelCase), nouns or noun phrases (e.g., `TaskList`, `BoboException`).
- **Methods**: `camelCase`, verbs or verb phrases (e.g., `readCommand()`, `executeCommand()`).
- **Variables / Parameters**: `camelCase`, descriptive names (avoid single-letter names except loop counters `i`, `j`).
- **Constants**: `UPPER_SNAKE_CASE` (e.g., `DISPLAY_DATE_FORMATTER`, `MAX_BUFFER_SIZE`).
- **Booleans**: Name methods and variables as assertions (e.g., `isDone`, `isExit`, `isEmpty()`, `hasPrefix()`).

## 2. Formatting & Layout

- **Indentation**: 4 spaces (never tabs).
- **Line Length**: Limit lines to 120 characters maximum.
- **Braces**: K&R style (opening brace `{` on same line, closing brace `}` on its own line).
- **Spacing**:
  - Space after control keywords (`if (`, `while (`, `for (`, `catch (`).
  - Spaces around binary operators (`+`, `-`, `=`, `==`, `&&`, `||`, etc.).
  - No space between method name and parameter parenthesis (e.g., `parseTaskIndex(input)`).

## 3. Import Statements

- **Explicit Imports Only**: Do NOT use wildcard imports (`import java.util.*;` or `import bobo.task.*;`).
- **Import Ordering**:
  1. Standard Java imports (`java.*`, `javax.*`)
  2. Third-party library imports
  3. Project-specific imports (`bobo.*`)

## 4. Class Structure & Member Ordering

Maintain a consistent order inside Java classes:
1. Constants (`public static final`, `private static final`)
2. Class (static) variables
3. Instance variables (`private`, `protected`)
4. Constructors
5. Public methods
6. Private / Package-private helper methods

## 5. Code Logic & Control Flow

- **Guard Clauses**: Use early returns (`if (invalid) return;`) instead of deeply nested `if-else` blocks.
- **No Magic Numbers / Strings**: Declare named constants for non-obvious literal values.
- **Scope Minimization**: Declare local variables as close to their first usage as possible.
