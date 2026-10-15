---
name: seedu-java-coding-standard
description: The se-edu Java coding standard (Basic + Intermediate levels) for this project. Use whenever writing, editing, or reviewing any .java file in this repository - naming, layout, statement style, and comment rules all apply to new and modified code, not just new files.
---

# se-edu Java Coding Standard

Source: https://se-education.org/guides/conventions/java/intermediate.html

This is the coding standard for all Java code in this project. Apply it to
every `.java` file you write or touch - not just brand-new files.

## Naming

- **Packages**: all lower case, no underscores.
- **Classes/enums**: nouns in `PascalCase` (`Task`, `TaskList`).
- **Variables**: `camelCase` (`taskIndex`).
- **Constants** (`static final`): `SCREAMING_SNAKE_CASE` (`MAX_ITERATIONS`).
- **Methods**: verbs in `camelCase` (`getName()`, `computeTotalWidth()`).
  Test methods may instead use
  `featureUnderTest_testScenario_expectedBehavior()`.
- **Abbreviations are not upper-cased**: `exportHtmlSource()`, not
  `exportHTMLSource()`.
- Names are English only.
- Variable name length should track scope: long names for large-scope
  variables, short names (`i`, `j`, `k` for ints; `c`, `d` for chars) are
  fine for small, short-lived scratch variables - `j`/`k` only inside
  nested loops.
- **Booleans** use an `is`/`has`/`was`/`can` prefix: `isDone`, `hasLicense()`,
  `canEvaluate()`; a boolean setter still names the parameter after the
  state, e.g. `void setFound(boolean isFound)`.
- **Collections** are named in the plural: `Collection<Point> points`,
  `int[] values`.
- Related constants share a common prefix: `COLOR_RED`, `COLOR_GREEN`.

## Layout

- Indent with 4 spaces, never tabs.
- Line length: soft limit 110 chars, hard limit 120.
- A wrapped line is indented 8 spaces (two levels) past its parent line;
  break after a comma, or before an operator (including `.`) - never split
  a method/constructor name from its opening `(`.
- K&R/Egyptian brackets: the opening `{` stays on the same line as the
  statement that introduces it, for classes, methods, `if`/`else`,
  loops, `switch`, and `try`/`catch`/`finally` alike.
- `switch` cases that intentionally fall through get a `// Fallthrough`
  comment; every other case ends in `break` (or `return`).
- One blank line between logical units within a block (with a comment
  introducing each block where it isn't obvious).
- Whitespace inside a line: a space around binary operators and after
  every comma and every reserved word (`while (true) {`, not
  `while(true){`).

## Statements

- Every class lives in a package (no default-package classes).
- Imports are explicit, never wildcarded (`import java.util.List;`, not
  `import java.util.*;`), grouped in this order with a blank line between
  groups: static imports, `java.*`, `javax.*`, `org.*`, `com.*`,
  `javafx.*`.
- Array brackets attach to the type, not the variable: `int[] a`, not
  `int a[]`.
- Declare and initialize a variable at the point of first use, in the
  smallest scope that works - don't hoist declarations to the top of a
  method "just in case."
- A class field is `public` only on a pure data class with no behaviour;
  every other class keeps its fields `private` (or `protected` for a
  base class's subclasses) and exposes behaviour through methods.
  Constants are exempt.
- Loop and conditional bodies are always wrapped in `{ }`, even a
  single statement - no bodyless `if (x) doThing();` or
  `for (...) sum += x;` on one line.
- The condition of an `if` goes on its own line, never inlined with the
  body.

## Comments

- Comments are in English, American spelling, no local slang.
- Every non-private class and non-private method gets a header (Javadoc)
  comment. Exceptions: simple getters/setters, an `@Override` whose
  parent Javadoc already applies exactly (use `{@inheritDoc}` or omit),
  and test classes/methods.
- Javadoc shape:
  ```java
  /**
   * Returns lateral location of the specified position.
   * If the position is unset, NaN is returned.
   *
   * @param x X coordinate of position.
   * @param zone Zone of position.
   * @return Lateral location.
   * @throws IllegalArgumentException If zone is <= 0.
   */
  public double computeLocation(double x, int zone) throws IllegalArgumentException {
  ```
  - `/**` opens on its own line; every following line's `*` lines up with
    the one above it, with a space after the `*`.
  - The first sentence is a short, standalone summary (it's what shows up
    in the summary table) - phrase a method's as "Returns...",
    "Sends...", "Adds...", not "Return..." or "Returning...".
  - A blank line separates the description from the `@param`/`@return`/
    `@throws` block.
  - `@param` tags are either present for every parameter or omitted
    entirely (unless the names are self-explanatory or already covered
    in the description); each ends with a period.
  - `@return` can be dropped for a `void` method, or when the return
    value is already obvious from the description.
  - No blank line between the closing `*/` and the class/method it
    documents.
- No comments purely restating what the code already says.

## What this project already does well

Most of this codebase already follows this standard (4-space indent,
K&R brackets, explicit imports, small-scope declarations, Javadoc on
public members). When adding or editing code, match the surrounding
style and this document; when the two ever disagree, this document wins.
