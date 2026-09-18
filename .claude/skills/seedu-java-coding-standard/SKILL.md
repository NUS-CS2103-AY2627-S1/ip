---
name: seedu-java-coding-standard
description: The SE-EDU Java coding standard (basic + intermediate) this project's Java code must follow. Load before writing or reviewing any Java code in this repository.
---

# SE-EDU Java Coding Standard

Source: https://se-education.org/guides/conventions/java/intermediate.html
("Use the Google Java style guide for any topic not covered here.")

This is a **project-specific standard**: every `.java` file in this
repository must comply, and any new Java code added from now on must
comply too. When reviewing or writing Java code, check it against every
rule below.

## Naming

- **Packages**: all lower case (e.g. `bot.task`). No `edu.nus.comp.*`-style
  names — use this project's own root package.
- **Classes/enums**: nouns in PascalCase (e.g. `Task`, `TaskList`).
- **Variables**: camelCase (e.g. `taskCount`).
- **Constants** (`static final`): SCREAMING_SNAKE_CASE (e.g. `MAX_ITERATIONS`).
  Associated constants should share a common prefix (e.g. `COLOR_RED`,
  `COLOR_GREEN`) so they sort together and read as a group.
- **Methods**: verbs in camelCase (e.g. `getName()`, `computeTotal()`).
  Test methods may use `featureUnderTest_testScenario_expectedBehavior()`
  (the second and/or third part can be omitted if not applicable).
- **Abbreviations/acronyms**: not all-uppercase when part of a name —
  `exportHtmlSource()`, not `exportHTMLSource()`.
- **English only**, American spelling, no local slang — the code is for an
  international audience.
- **Scope-based length**: long names for large-scope variables; short
  names (`i, j, k, m, n`, `c`, `d`) are fine for small-scope scratch
  variables/indices.
- **Booleans**: name so they read like booleans — `isSet`, `isVisible`,
  `hasData`, `wasOpen`; boolean methods as `hasLicense()`, `canEvaluate()`.
  Prefer an `is`/`has`/`was` prefix. A boolean setter is
  `void setFound(boolean isFound)`.
- **Collections**: use the plural form — `Collection<Point> points`,
  `int[] values` — not a singular or vague name.
- **Iterator variables**: `i`, `j`, `k`, ... — `j`/`k` only for nested loops.

## Layout

- **Indentation**: 4 spaces, never tabs.
- **Line length**: soft limit 110 chars, hard limit 120 — slightly over
  110 is fine, never exceed 120.
- **Wrapped lines**: indent the continuation 8 spaces (twice the normal 4)
  more than the parent line's indentation.
- **Line-break placement**: break after a comma; break before an operator
  (including `.`, `&` in bounds, `|` in multi-catch); keep a method/
  constructor name attached to its `(`; prefer higher-level breaks (e.g.
  break at `+` between big sub-expressions, not inside one); for a
  wrapped ternary, either put the whole thing on one line or put `?` and
  `:` each on their own indented line.
- **Braces**: K&R/Egyptian style — opening brace at the end of the line,
  never on its own line:
  ```java
  while (!done) {
      doSomething();
  }
  ```
- **`switch`**: `case` labels are indented one level (4 spaces) in from
  `switch`, and statements one further level in from `case`:
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
  Add an explicit `// Fallthrough` comment on any `case` that
  intentionally has no `break`.
- **Whitespace**: space around binary operators, after a reserved word
  before `(`, after commas, and around `:`/`;` in a `for` — e.g.
  `a = (b + c) * d;`, `while (true) {`, `doSomething(a, b, c);`,
  `for (i = 0; i < 10; i++) {`.
- **Blank lines**: one blank line between logical units within a block
  (each such unit is often introduced by a comment).

## Statements

- **Packages**: every class belongs to a package (already the case here —
  keep it that way for any new class).
- **Imports**: always explicit — never `import java.util.*;`. Keep a
  consistent order: this project's convention is `java.*`/`javax.*`
  imports first (alphabetized), a blank line, then this project's own
  `bot.*` imports (alphabetized).
- **Arrays**: the `[]` attaches to the type, not the variable —
  `int[] a = new int[20];`, never `int a[] = ...`.
- **Variable initialization**: initialize at declaration and declare in
  the smallest possible scope. It's fine to leave a variable
  uninitialized at declaration only when a valid value genuinely isn't
  available yet (e.g. it's set inside a subsequent `try`/`catch` or
  `switch`) — never initialize to a placeholder/phony value instead.
- **Field visibility**: never make a class field `public` unless the
  class is a pure data class with no behavior (constants are exempt).
- **Loop/conditional bodies**: always wrap in `{ }`, even for a single
  statement — never write a bodyless `for (...) stmt;` or
  `if (cond) stmt;` on one line.
- **Conditionals**: the condition's controlled statement(s) go on their
  own line(s), never `if (isDone) doCleanup();` on one line.

## Comments

- **Header (Javadoc) comments are required** for every public class and
  public method. They **may be omitted** for:
  1. Getters/setters,
  2. An overriding method, provided the parent method's Javadoc applies
     to it exactly as-is,
  3. Classes/methods used only for testing.
- **Javadoc form**:
  ```java
  /**
   * Returns lateral location of the specified position.
   * If the position is unset, NaN is returned.
   *
   * @param x X coordinate of position.
   * @param y Y coordinate of position.
   * @throws IllegalArgumentException If zone is <= 0.
   */
  public double computeLocation(double x, double y) throws IllegalArgumentException {
  ```
  - Opening `/**` on its own line; subsequent `*` aligned with it; a
    space after each `*`.
  - First sentence is a short summary (Javadoc puts it in the summary
    table) and starts with a third-person verb — `Returns ...`,
    `Adds ...` — not `Return`/`Returning`.
  - Blank line between the description and the `@param`/`@return`/
    `@throws` block.
  - End each `@param` description with a period.
  - Omit `@return` when the method returns nothing or the return value
    is obvious from the rest of the comment.
  - `@param`s are all-or-nothing: include one for every parameter, or
    omit them all (only when every parameter name is self-explanatory or
    already covered in the main description).
  - For an overridden method with slightly different behavior from the
    parent, `{@inheritDoc}` can pull in and extend the parent's Javadoc.
  - A single-line field comment is fine: `/** Number of open connections. */`.
- **Comment indentation** must match the code it describes — never
  outdented or flush-left inside an indented block. A trailing comment on
  the same line as code (`process(x); // dummy value`) is fine.
