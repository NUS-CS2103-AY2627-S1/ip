---
name: seedu-java-coding-standard
description: The se-education.org intermediate Java coding standard that all Java in this repository must follow. Consult before writing, editing, or reviewing any .java file in src/main or src/test, and use the checklist to catch naming, layout, import-order, brace-style, and Javadoc violations.
---

# seedu Java coding standard (intermediate)

Canonical source: <https://se-education.org/guides/conventions/java/intermediate.html>

Every `.java` file in `src/main` and `src/test` must follow every rule below.
Treat the checklist as blocking during review.

## Naming

- **Packages**: all lower case — `hu9o.ui`, `hu9o.task`.
- **Classes / enums**: nouns in `PascalCase` — `DeadlineTask`, `CommandType`.
- **Methods**: verbs in `camelCase` — `getTask()`, `computeTotalWidth()`. Never a
  noun phrase: `answerHandler` ✘ → `handleCommand` ✔.
- **Variables**: `camelCase` — `taskCount`, `audioSystem`.
- **Constants** (`static final`): `UPPER_SNAKE_CASE` — `MAX_ITERATIONS`,
  `DATE_FORMAT`. Associated constants share a prefix (`COLOR_RED`, `COLOR_GREEN`).
- **Booleans** read like predicates — `isDone`, `hasLicense()`, `canEvaluate()`.
  Do not use an `is…`/`has…` name for a member that returns a non-boolean.
- **Abbreviations** are not all-caps inside a name — `exportHtmlSource()` ✔,
  `exportHTMLSource()` ✘; `openDvdPlayer()` ✔.
- **Collections** take a plural name — `Collection<Task> tasks`, `int[] values`.
- **Scratch counters** may be `i`, `j`, `k` (`j`/`k` for nested loops only). Wider
  scope ⇒ longer name.
- **Test methods**: `featureUnderTest_scenario_expectedBehavior()` — underscores
  are allowed only here. e.g. `deleteTask_indexOutOfRange_exceptionThrown()`.
- All names in English.

## Layout & formatting

- 4-space indent, never tabs.
- Line length ≤ 120 (aim for ≤ 110).
- Continuation lines are indented 8 spaces (2×) from the parent line. Break
  *before* an operator (`+`, `.`, `&&`), *after* a comma. Keep a method name
  attached to its `(`.
- K&R ("Egyptian") braces: `if (x) {` … `} else {`.
- Every block body is braced, even a single statement — `if (s != null) { read(s); }` ✔,
  `if (s != null) read(s);` ✘. Same for loops.
- The controlled statement goes on its own line, never after the `if` on the same line.
- `switch`: `case` labels indented one level under `switch`; every fall-through
  carries an explicit `// Fallthrough`; always include a `default`.
- One blank line between logical units inside a method; never multiple blank lines.
- Whitespace: `a = (b + c) * d;`, `while (true) {`, `foo(a, b, c);`,
  `for (i = 0; i < n; i++) {`.

## Statements

- Every class in a package; one top-level class per file.
- Imports listed explicitly — never `import x.y.*`.
- Import order is consistent and grouped: static imports first, then `java.*`,
  `javax.*`, third-party, then project packages; one blank line between groups;
  alphabetical within a group.
- Array brackets on the type: `int[] a` ✔, `int a[]` ✘.
- Declare each variable at first use, in the smallest scope, initialised there.
- Non-constant `public` fields only on a pure data class (no behaviour).

## Comments & Javadoc

- Comments in English, American spelling.
- Header Javadoc on every public class and public method — except plain
  getters/setters, methods whose parent Javadoc already applies (use
  `{@inheritDoc}`), and test code.
- The summary is a third-person phrase: "Returns …", "Adds …", "Sends …" — not
  "Return" / "Get" / imperative.
- Form:
  ```
  /**
   * Returns the lateral location of the specified position.
   * If the position is unset, NaN is returned.
   *
   * @param x X coordinate of the position.
   * @param y Y coordinate of the position.
   * @return The lateral location.
   * @throws IllegalArgumentException If zone is <= 0.
   */
  ```
  Blank line between the description and the `@` block. Each `@param` / `@return`
  / `@throws` description ends with a punctuation mark. Document all parameters or
  none. No blank line between the Javadoc and the member it documents.
- Single-line member Javadoc is fine: `/** Number of active connections. */`
- Comments are indented to match the code they describe.
