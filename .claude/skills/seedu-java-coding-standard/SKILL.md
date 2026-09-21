---
name: seedu-java-coding-standard
description: The SE-EDU intermediate Java coding standard for this project. Use whenever writing or reviewing any Java code in this repo — naming, layout/formatting, statements, and comments/Javadoc rules, condensed from https://se-education.org/guides/conventions/java/intermediate.html.
---

# seedu-java-coding-standard

All Java code in this project (`src/main/java`, `src/test/java`) must
follow this standard. Apply it while writing new code, and check for it
when reviewing or editing existing code.

## Naming

- Packages: all lowercase (e.g. `pdd.task`).
- Classes/enums: `PascalCase` nouns.
- Variables: `camelCase`.
- Constants: `SCREAMING_SNAKE_CASE`.
- Methods: `camelCase` verbs (e.g. `getName()`).
- JUnit test methods: `featureUnderTest_testScenario_expectedBehavior()`.
- Abbreviations/acronyms in names: not all-uppercase (`exportHtmlSource()`,
  not `exportHTMLSource()`).
- All names and comments: in English.
- Variable name length: longer names for large-scope variables; short
  names (`i`, `j`, `k`) acceptable for small-scope scratch variables —
  nested loops use `j`, `k` only (not repeating `i`).
- Booleans: prefix with `is`/`has`/`was` (e.g. `isDone`, `hasData`); a
  boolean setter still reads `void setFound(boolean isFound)`.
- Collections: plural names (e.g. `List<Task> tasks`).
- Related constants: share a common prefix (e.g. `COLOR_RED`, `COLOR_GREEN`).

## Layout & formatting

- Indentation: 4 spaces, never tabs.
- Line length: soft limit 110 chars, hard limit 120 chars.
- Wrapped continuation lines: indent 8 spaces (2x normal).
- Line breaks in wrapped expressions: after commas, before operators and
  dots.
- Method name stays attached to its opening parenthesis: `foo(`, not `foo (`.
- Braces: K&R/Egyptian style — opening brace on the same line as the
  declaration/keyword, for classes, methods, if/else, loops, switch,
  and try/catch alike.
- `switch` cases that intentionally fall through without `break` need an
  explicit `// Fallthrough` comment.
- Operators surrounded by spaces (`a = b + c`); reserved words followed
  by a space (`while (true)`); commas and semicolons followed by a
  space; colons surrounded by spaces except in `switch` case labels.
- One blank line between logical blocks of code.

## Statements

- Every class belongs to a package (no default-package classes).
- No wildcard imports (`import java.util.*`) — always import explicitly.
- Array specifiers attach to the type, not the variable: `int[] a`, not
  `int a[]`.
- Variables: initialize where declared; declare in the smallest scope
  that works.
- Class variables (fields) are never `public`, unless the class is a
  pure data class with no behavior.
- Loop and conditional bodies are always wrapped in `{ }`, even for a
  single statement.
- Conditions read on their own line, separate from the action they guard.

## Comments & Javadoc

- All non-private classes and methods get a Javadoc header comment.
  Exceptions: getters/setters, exact-match overridden methods (can use
  `{@inheritDoc}` instead), and test code.
- `/**` opens on its own line; subsequent `*` are aligned with it; a
  space follows each `*`.
- First sentence: a short summary starting with a verb (`Returns`,
  `Adds`, `Parses`, ...).
- Blank line between the description and the `@param`/`@return` section,
  if any.
- `@param`: document all parameters, or none if they're all
  self-explanatory from the summary — don't mix.
- `@return`: omit if the method returns nothing, or if the return value
  is obvious from the summary.
- No blank line between the Javadoc block and the declaration it documents.
- A short, single-fact doc can be a one-liner: `/** Returns the task's description. */`.
- Comment indentation matches the surrounding code.

## When something isn't covered

Match the existing style in the file/package you're editing rather than
introducing a new convention, so the codebase stays internally
consistent.
