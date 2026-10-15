---
name: seedu-java-coding-standard
description: Enforce the SE-EDU Java intermediate coding standard for the project. Use this skill to check code against the mandated style rules or format it appropriately.
---

# SE-EDU Java Coding Standard (Intermediate)

This project strictly follows the SE-EDU Java Coding Standard (Intermediate). For any rules not explicitly covered, refer to the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html). 

## Naming
- **Packages:** All lowercase (e.g., `vector.ui`).
- **Classes & Enums:** Nouns in PascalCase (e.g., `TaskList`, `VectorException`).
- **Methods:** Verbs in camelCase (e.g., `execute()`, `parse()`).
- **Variables:** camelCase (e.g., `isExit`, `filePath`).
- **Constants:** ALL_UPPERCASE with underscores separating words (SCREAMING_SNAKE_CASE).
- **Booleans:** Prefix with `is`, `has`, `was`, etc., so they read like normal English (e.g., `isDone`).
- **Collections:** Use plural forms for arrays and collections (e.g., `tasks`, `points`).
- **Iterators:** Use `i`, `j`, `k` etc. for simple nested loops.
- **Language:** All names and comments must be written in English.

## Layout & Formatting
- **Indentation:** 4 spaces per indent level (do NOT use tabs).
- **Line Length:** Soft limit of 110 characters, hard limit of 120 characters. Wrapped lines should indent by 8 spaces.
- **Braces (K&R Style):** Curly braces are mandatory for all `if`, `else`, `for`, `while`, and `do-while` blocks, even if they only contain a single line. 
- **Method/Constructor Names:** Stays attached to the open parenthesis `(` that follows it.
- **Blank Lines:** Separate logical units of code within a block with a single blank line.
- **Spaces:** Operators and commas must be followed/surrounded by spaces. `if`, `while`, `for`, `switch` etc. must be followed by a space before `(`.

## Statements
- **Package & Import:** Every class must be in a package.
- **Wildcard Imports:** Avoid wildcard imports entirely (e.g., `import java.util.*;`). All imports must explicitly specify the class (e.g., `import java.util.ArrayList;`).
- **Variables:** Initialize variables where declared, and keep their scope as small as possible. No public class variables unless the class is a pure data struct.
- **Switch Statements:** Always include a `// Fallthrough` comment when omitting a `break` in a `case` statement.

## Comments
- **Header Comments:** Write descriptive header (Javadoc) comments for all classes, public methods, and non-trivial private methods.
  - Exclude basic getters/setters, test methods, or strictly inherited methods using `@inheritDoc`.
  - Format Javadocs with the opening `/**` on a separate line.
  - The first sentence should start in a verb form such as "Returns...", "Gets...", "Constructs...".
  - Include `@param`, `@return`, and `@throws` only if they add value.
- **Indentation:** Comments should be indented relative to their position in the code.
