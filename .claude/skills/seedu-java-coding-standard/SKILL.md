---
name: seedu-java-coding-standard
description: The SE-EDU intermediate Java coding standard for this project (naming, layout, statements, comments). Use whenever writing, editing, or reviewing Java source in this repository, and when auditing existing code for style compliance.
---

# SE-EDU Java Coding Standard (Intermediate)

Source: https://se-education.org/guides/conventions/java/intermediate.html

Apply this to every `.java` file you write or edit in this project (`src/main` and `src/test`). When reviewing existing code, check it against this list and fix violations.

## Naming

- **Packages**: all lower case, no institutional prefixes (e.g. `yapbot.task`, not `edu.nus.comp.yapbot.task`).
- **Classes/enums**: nouns in `PascalCase` (e.g. `TaskList`, not `taskList`).
- **Variables**: `camelCase` (e.g. `taskList`).
- **Constants**: `SCREAMING_SNAKE_CASE` (e.g. `MAX_CAPACITY`). Give related constants a common prefix (e.g. `TYPE_TODO`, `TYPE_DEADLINE`).
- **Methods**: verbs in `camelCase` (e.g. `getName()`, `computeTotalWidth()`).
  - Test methods: `featureUnderTest_testScenario_expectedBehavior()`, e.g. `sortList_emptyList_exceptionThrown()`.
- **Abbreviations/acronyms**: not all-uppercase when part of a name — `exportHtmlSource()` not `exportHTMLSource()`, `Ui` not `UI`.
- **All names in English.**
- **Scope-appropriate name length**: large-scope variables get long, descriptive names; small-scope scratch variables (loop indices, temp values) can be short (`i, j, k, m, n` for ints; `c, d` for chars).
- **Booleans**: name so they read as a boolean — prefix `is`/`has`/`was` (`isDone`, `hasData`, `wasOpen`).
- **Collections**: plural names (`Collection<Point> points`, `int[] values`).
- **Iterator variables**: `i`, `j`, `k`, ... (nest with `j`, `k` for inner loops).

## Layout

- **Indentation**: 4 spaces, never tabs.
- **Line length**: soft limit <110 chars, hard limit 120 chars.
- **Wrapped lines**: indent 8 spaces (twice normal) more than the parent line; break after a comma, before an operator; keep a method/constructor name attached to its `(`.
- **Braces**: K&R/Egyptian style — opening brace on the same line.
  ```java
  while (!done) {
      doSomething();
      done = moreToDo();
  }
  ```
- **Switch statements with fallthrough**: an explicit `// Fallthrough` comment is required on any `case` that has no `break`/`return` and falls into the next case.
- **Whitespace**: spaces around binary operators, after keywords (`while (`, `if (`), after commas.
- **Blank lines**: separate logical units within a block with one blank line.

## Statements

- **Package declaration**: every class belongs to a package (no default package).
- **Imports**: explicit only, never wildcards (`import java.util.List;`, not `import java.util.*;`). Order: static imports, then `java.*`, `javax.*`, `org.*` (alphabetical), `com.*` (alphabetical), then this project's own packages (alphabetical) — each group separated by a blank line, alphabetical within a group.
- **Arrays**: specifier attached to the type, not the variable — `int[] a`, not `int a[]`.
- **Variables**: initialize at the point of declaration where possible; declare in the smallest scope that needs them.
- **Class variables**: never `public` unless the class is a pure data class with no behavior (constants are the exception — `public static final` is fine).
- **Loops**: always brace the body, even for a single statement.
- **Conditionals**: the condition goes on its own line; always brace the body, even for a single statement — never `if (isDone) doCleanup();` on one line.

## Comments

- **English**, American spelling.
- **Header/Javadoc comments required** on: all public classes, all public methods. Optional on: trivial getters/setters, overridden methods that just delegate (use `{@inheritDoc}` for those), test classes/methods (their descriptive names carry the documentation instead — see the naming convention above).
- **Javadoc format**: `/**` on its own line; first sentence is a short summary starting with a verb (`Returns...`, `Adds...`), not `Return...`/`Returning...`; blank line between the description and the `@param`/`@return`/`@throws` block; no blank line between the Javadoc block and the class/method it documents.
- **Inline comments**: indent to match the surrounding code; trailing comments are fine (`process(x); // process a dummy value first`).

For anything not covered here, defer to the Google Java Style Guide.
