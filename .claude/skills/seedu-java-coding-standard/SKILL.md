---
name: seedu-java-coding-standard
description: Enforces the SE-EDU intermediate Java coding standard (https://se-education.org/guides/conventions/java/intermediate.html) for every Java file in this project — naming, layout/indentation, line length, brackets, statement form, and Javadoc rules. Always consult this before writing any new Java code in this repo, and use it as the checklist when reviewing, editing, or fixing an existing .java file here, or when asked to check "coding standard", "code style", "Java convention", or "checkstyle"-type compliance. This is a project-wide mandate (see AGENTS.md), not optional style guidance.
---

# SE-EDU Java Coding Standard

This project follows the SE-EDU intermediate Java convention
(https://se-education.org/guides/conventions/java/intermediate.html) for
every `.java` file under `src/`. Apply these rules whenever writing new
Java code or editing existing Java code in this repo — don't wait to be
asked to "check the style."

## Naming

- **Packages**: all lowercase (`echo.task`, not `echo.Task`).
- **Classes/enums**: nouns, PascalCase (`Task`, `TaskType`).
- **Variables**: camelCase (`taskList`, not `task_list`).
- **Constants**: `ALL_UPPERCASE_WITH_UNDERSCORES` (e.g. `MAX_ITERATIONS`).
  Related constants share a common prefix (`COLOR_RED`, `COLOR_GREEN`).
- **Methods**: verbs, camelCase (`getName()`, `computeTotal()`).
- **Abbreviations/acronyms**: not all-caps inside a name — `exportHtmlSource()`,
  not `exportHTMLSource()`.
- **All names in English.**
- **Scope-based length**: a variable with a large scope needs a long,
  descriptive name; a short-lived scratch variable (a loop counter, a
  one-line temp) can be short — `i`/`j`/`k` for ints, `c`/`d` for chars.
  `j`/`k` are reserved for *nested* loops, not the outer one.
- **Booleans** read like yes/no questions: `isDone`, `hasData`, `wasOpen`.
  A boolean setter still reads that way: `setDone(boolean isDone)`.
- **Collections** are plural: `List<Task> tasks`, not `taskList` unless
  it's genuinely a single wrapped concept (a class named `TaskList` is
  fine — that's a type name, not a collection variable name).

## Layout

- **Indentation**: 4 spaces, never tabs.
- **Line length**: soft limit 110, hard limit 120 characters.
- **Wrapped lines**: indent the continuation 8 spaces relative to the
  start of the statement (double the normal 4-space unit). Break
  *before* an operator (`+`, `.`, `|`), not after:
  ```java
  System.out.println("some long string "
          + "continued here");
  ```
  Prefer breaking at the highest-level operator, not the innermost one.
- **Brackets**: K&R / Egyptian style — opening brace stays on the same
  line as the statement, never on its own line:
  ```java
  while (!done) {
      doSomething();
  }
  ```
- **`switch`**: `case` is indented one level deeper than `switch`, and
  the case body one level deeper still:
  ```java
  switch (condition) {
      case ABC:
          statements;
          break;
      default:
          statements;
          break;
  }
  ```
  A `case` that intentionally falls through needs an explicit
  `// Fallthrough` comment where the `break` would otherwise go.
- **Whitespace within statements**: space around binary operators
  (`a = b + c;`), space after Java keywords before `(` (`while (true)`,
  not `while(true)`), space after commas, space after `;` in a `for`
  header. No space before `;` or between a method name and its `(`.
- **Blank lines** separate logical units within a method/block — group
  related statements, put one blank line between groups, not one blank
  line per statement and not zero blank lines for a long block.

## Statements

- **Every class in a package** — no default-package classes.
- **Import order**: static imports first, then `java.*`, then third-party
  groups, then this project's own `echo.*` imports last — each group
  separated by a blank line, alphabetical within a group.
- **No wildcard imports** (`import java.util.*;` is not allowed) — list
  every imported class explicitly.
- **Array specifiers attach to the type, not the variable**:
  `int[] a`, not `int a[]`.
- **Initialize variables where declared**, in the smallest scope that
  works. Prefer a `switch` *expression* over declare-then-assign-in-each-
  case when every branch just produces a value.
- **No public fields** on a class with behavior (only plain data-holder
  classes may have public fields, and constants are exempt from this
  rule) — use private fields with methods, or `public static final` for
  true constants.
- **Loop bodies and single-statement conditionals always get `{ }`**,
  even for one line — no `if (x) doThing();` on one line, no braceless
  `for`.
- **The conditional goes on its own line**: `if (isDone) {` then the
  body on the next line — never `if (isDone) doCleanup();`.

## Comments (Javadoc)

- All comments in English.
- **Every non-private class and method needs a header comment** (this
  project's own AGENTS.md already asks for this more broadly — this
  convention is what governs the comment's *format*). Getters/setters,
  overridden methods whose parent doc already applies, and test
  classes/methods may skip it.
- **Format**:
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
  ```
  Opening `/**` on its own line; first sentence is a short summary
  (starts with a verb like "Returns...", "Creates...", "Adds...", not
  "Return..."); a blank `*` line between the description and the
  `@param`/`@return`/`@throws` block; each `@param` description ends
  with punctuation; no blank line between the comment and the
  class/method it documents.
- `@return` can be omitted for `void` methods or when the return value
  is self-evident from the summary sentence; `@param` can be omitted
  when every parameter is already self-explanatory from its name or is
  covered in the main description (all-or-nothing — don't document some
  params and skip others).
- A trivial one-line class member can use the compact single-line form:
  `/** Number of connections to this database */`.
- **Comment indentation matches the code it's attached to** — a comment
  inside a block is indented with that block, not with the line above or
  below it out of sync.

## When fixing a violation

Prefer the smallest change that brings a file into compliance without
altering behavior — this is a style pass, not a refactor. If a fix would
require behavior changes (e.g. restructuring a switch to eliminate a
`default`-less exhaustiveness issue), flag it instead of changing
behavior silently.
