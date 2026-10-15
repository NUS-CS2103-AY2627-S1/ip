---
name: seedu-java-coding-standard
description: Coding standard rules for Java code in SE-EDU / CS2103 projects based on basic and intermediate conventions.
---

# SE-EDU Java Coding Standard (Basic + Intermediate)

Enforce these Java coding conventions across all code in this project. Reference: https://se-education.org/guides/conventions/java/intermediate.html

## 1. Naming Conventions
- **Packages**: All lower case (e.g., `dave.parser`, `dave.task`). School project root names should match the project name (never `edu.nus.comp.*`).
- **Classes and Enums**: Nouns in `PascalCase` (e.g., `TaskList`, `DateTimeParser`, `Command`).
- **Variables and Parameters**: In `camelCase` (e.g., `isComplete`, `itemNumber`).
- **Constants**: All uppercase with words separated by underscores (`SCREAMING_SNAKE_CASE`, e.g., `DEFAULT_FILE_PATH`).
- **Methods**: Verbs in `camelCase` (e.g., `parseCommand()`, `showTaskList()`).
- **Test Methods**: Use the three-part format: `featureUnderTest_testScenario_expectedBehavior()` (e.g., `parse_invalidFormat_throwsDaveCommandException()`).
- **Abbreviations and Acronyms**: Treat as regular words, do not uppercase all letters (e.g., `exportHtmlSource()`, not `exportHTMLSource()`).
- **Language**: English only, using American spelling.
- **Scope & Length**: Variables with large scope have descriptive names; scratch variables with small scope may be short (`i`, `j`, `c`, `d`).
- **Booleans**:
  - Sound like booleans using prefixes such as `is`, `has`, `was`, `can`, `should` (e.g., `isDone`, `hasTime`).
  - Getter for boolean must be prefixed with `is` or `has` (e.g., `boolean isDone()`, `boolean hasTime()`).
  - Setter methods for boolean variables must be of the form `void setFound(boolean isFound)` or `void setDone(boolean isDone)`.
- **Collections**: Plural form should be used for names representing collections or arrays (e.g., `List<Task> tasks`, `String[] lines`).

## 2. Layout & Formatting
- **Indentation**: 4 spaces (never tabs).
- **Line Length**: Soft limit of 110 characters, hard limit of 120 characters. Wrap long lines at logical boundaries.
- **Indentation for Wrapped Lines**: 8 spaces (twice normal indentation) from parent line.
- **Line Breaks**:
  - Break after a comma.
  - Break before an operator or operator-like symbol (`+`, `-`, `.`, `|`, `&`).
  - Method or constructor name stays attached to the open parenthesis `(`.
  - Prefer higher-level breaks over lower-level breaks.
- **Braces (Egyptian / K&R Style)**:
  - Opening brace `{` on the same line as declaration/statement.
  - Closing brace `}` on a new line matching statement indentation.
- **Statements Structure**:
  - `if (condition) { ... } else if (condition) { ... } else { ... }`
  - Body of loop (`for`, `while`, `do-while`) must always be enclosed in `{ }`, even if single statement.
  - Single-statement conditionals must always be enclosed in `{ }`, placed on separate lines.
  - Switch statement:
    ```java
    switch (condition) {
    case ABC:
        statements;
        // Fallthrough
    case DEF:
        statements;
        break;
    default:
        statements;
        break;
    }
    ```
    Include explicit `// Fallthrough` comment when omitting `break`.
  - Try-catch: `try { ... } catch (Exception e) { ... } finally { ... }`
- **Whitespace**:
  - Around binary operators, after commas and semicolons, after control flow keywords (`if`, `while`, `for`, `switch`).
  - One blank line separating logical units within a block.

## 3. Package and Import Statements
- Every class must belong to a package.
- Ordering of import statements must be consistent.
- Explicit imports only: never use wildcard imports (`import java.util.*;` is prohibited).

## 4. Types & Variables
- Array specifier attached to type, not variable (e.g., `int[] numbers`, not `int numbers[]`).
- Initialize variables where declared, in the smallest scope possible.
- Class variables must never be declared `public` unless the class is a data class with no behavior (excluding constants).

## 5. Comments & Javadoc
- All comments written in English with American spelling.
- Header comments mandatory for all classes and public methods (except simple getters/setters, overrides with identical behavior, and test methods).
- First sentence in method header comments must be a short summary starting with 3rd-person singular verb: `Returns ...`, `Parses ...`, `Adds ...` (not `Return` or `Returning`).
- Javadoc format:
  ```java
  /**
   * Short summary in 3rd person singular form.
   *
   * @param x Description of x.
   * @return Description of return value.
   * @throws SomeException If condition occurs.
   */
  ```
- Subsequest `*` aligned with the first, single space after `*`.
- Empty line between description and `@param`/`@return`/`@throws` tags.
- Punctuation (period) behind each tag description.
- No blank line between Javadoc block and the method/class declaration.
- Single line member Javadoc allowed: `/** Description */ private int count;`.
- Indent comments relative to their position in code.
