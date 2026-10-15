---
name: seedu-java-coding-standard
description: The mandatory Java coding standard for this project, derived from the se-education.org intermediate Java conventions. Apply when writing, reviewing, or refactoring any Java code in this repository so that naming, layout, imports, comments/Javadoc, statements, and class design all conform.
---

# se-education Java coding standard (intermediate)

This is the authoritative Java coding standard for this project. It is derived
from the se-education.org intermediate-level Java conventions
(https://se-education.org/guides/conventions/java/intermediate.html). All Java
code in this repository — both `src/main/java` and `src/test/java` — must follow
it. These rules are graded, so apply them while writing code, not only at the
refactoring increments.

When a topic is not covered here, fall back to the source guide above, then to
the Google Java Style Guide. When in doubt, favor readability and consistency
with the surrounding code.

## How to use this skill

1. Before writing or editing Java, skim the relevant section(s) below.
2. After editing, self-check the change against the **Review checklist** at the
   end. Fix any violation before considering the work done.
3. Git commit and branch-naming conventions are **not** part of this skill; they
   live in [AGENTS.md](../../../AGENTS.md) and
   [docs/style-guide.md](../../../docs/style-guide.md).

---

## 1. Naming

- **Packages** — all lowercase, no underscores (e.g. `noms.ui`, `noms.task`).
  Use the project name followed by logical subdivisions.
- **Classes and enums** — PascalCase nouns (`Task`, `TaskList`, `CommandType`).
- **Methods** — camelCase verbs (`getDescription()`, `parseTask()`,
  `computeTotalWidth()`).
- **Variables** — camelCase (`taskNumber`, `fullCommand`). Larger scope → longer,
  more descriptive names; small scope may be brief.
- **Constants** (`static final`) — `UPPER_SNAKE_CASE` (`MAX_ITERATIONS`,
  `DISPLAY_FORMAT`). Related constants share a common prefix.
- **Booleans** — read like a yes/no question: prefix with `is`/`has`/`was`/`can`/
  `should` (`isDone`, `hasData`, `canEvaluate`). Boolean setter form:
  `void setFound(boolean isFound)`.
- **Collections** — plural names (`tasks`, `values`, `points`).
- **Abbreviations/acronyms are NOT all-uppercase mid-name** — `exportHtmlSource()`
  not `exportHTMLSource()`; `taskId` not `taskID`.
- **Loop/scratch indices** — `i, j, k` (integers) or `c, d` (chars) are fine for
  short scopes; `j`/`k` only for nested loops. Anything with a larger scope gets a
  descriptive name.
- **Test methods** — `featureUnderTest_testScenario_expectedBehavior()`
  (e.g. `parse_unknownCommand_throwsException()`). The scenario and/or expected
  parts may be omitted when obvious.
- **Language** — all names in English.

## 2. Layout and formatting

- **Indentation** — 4 spaces, never tabs.
- **Line length** — soft limit under 110 chars; hard limit 120.
- **Line wrapping** — wrapped continuation lines indent **8 spaces** (double the
  normal indent). Break *after* commas and *before* operators (including `.`, and
  `|` in multi-catch). Keep a method/constructor name attached to its opening
  parenthesis. Prefer breaking at higher syntactic levels.
- **Braces** — K&R / "Egyptian" style: opening brace on the same line as the
  statement.
  ```java
  if (condition) {
      doSomething();
  } else {
      doOther();
  }
  ```
- **Whitespace within statements**:
  - Surround binary operators with spaces: `a = (b + c) * d;`
  - A reserved word is followed by a space: `while (true) {`, `if (x) {`
  - A comma is followed by a space: `doSomething(a, b, c);`
  - `for`-statement semicolons are followed by a space.
- **Blank lines** — separate logical units within a block with a single blank
  line. No runs of multiple blank lines.
- **Switch** — indent `case` labels one level (4 spaces) inside the `switch`,
  and the statements under each `case` one level deeper again, as the
  se-education guide's example shows:
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
  Arrow-form `switch` is also acceptable. When a fall-through is intentional,
  mark it `// Fallthrough`.

## 3. Imports

- **No wildcard imports** — list every imported class explicitly
  (`import java.util.List;`, never `import java.util.*;`).
- **Consistent ordering**, grouped with a blank line between groups:
  1. static imports
  2. `java.*`
  3. `javax.*`
  4. `org.*`
  5. `com.*`
  6. everything else (e.g. this project's own `noms.*`), and `javafx.*`
- **Every class belongs to a package** — put a `package` declaration at the top of
  every file. (This project already uses `noms.*` packages.)

## 4. Types, variables, and statements

- **Array brackets attach to the type**: `int[] values`, never `int values[]`.
- **Declare variables in the smallest possible scope, and initialize them where
  declared.**
- **Class fields are never `public`** unless the class is a pure data class with no
  behavior. Use accessor methods instead. (Constants — `public static final` — are
  exempt.)
- **Always brace loop and conditional bodies**, even a single statement:
  ```java
  for (int i = 0; i < n; i++) {
      sum += values[i];
  }
  ```
  Never `if (isDone) doCleanup();` and never a braceless one-line loop.
- **The controlled statement goes on its own line**, below the `if`/`for`/`while`
  — never on the same line as the condition.

## 5. Comments and Javadoc

- **English, American spelling** — write `initialize`, `color`, `recognized`,
  `centralize` (not `initialise`, `colour`, `recognised`, `centralise`). Avoid
  slang. This applies to comments, Javadoc, **and** identifiers.
- **Javadoc is required on**:
  - every public class/enum, and
  - every nontrivial public method.
- **Javadoc may be omitted for**: plain getters/setters, overrides whose inherited
  Javadoc already applies exactly (use `{@inheritDoc}` if you want to extend it),
  and test classes/methods.
- **Javadoc format**:
  - `/**` on its own line; each subsequent line starts with an aligned `*` and a
    space; no blank line between the Javadoc block and the member it documents.
  - The **first sentence is a short summary** (it becomes the summary-table entry).
    For methods, phrase it as `Returns ...`, `Adds ...`, `Parses ...` — not an
    imperative — e.g. `Returns this task's description.`
  - Leave **one blank line between the description and the tag section**
    (`@param`/`@return`/`@throws`).
  - `@param` — include for **all** parameters or **none**; each description ends
    with punctuation. May be omitted only when every parameter is self-explanatory
    or already covered in the description.
  - `@return` — may be omitted when the method returns nothing or the return is
    obvious from the summary.
  - A single-line field Javadoc is fine: `/** The task description. */`
- **Comment indentation** matches the surrounding code. Trailing comments are
  allowed (`process(dummy); // warm up the cache first`).

## 6. Class design

- Prefer the simplest design sufficient for the requirement; note a more advanced
  alternative briefly only when it is genuinely relevant.
- Keep fields `private` (or `protected` for inheritance) and expose behavior
  through intention-revealing methods.
- Keep related knowledge in one place (e.g. all command-format parsing in the
  parser) rather than scattering it.

---

## Review checklist

Run through this after any Java edit:

- [ ] Package declared; imports explicit, grouped, and ordered (no wildcards).
- [ ] Names follow the conventions (nouns/verbs, camelCase/PascalCase,
      `UPPER_SNAKE_CASE` constants, `is/has` booleans, plural collections, no
      all-caps acronyms).
- [ ] 4-space indent, K&R braces, lines ≤ 120 chars, 8-space continuation indent.
- [ ] Every loop/conditional body is braced and on its own line.
- [ ] `int[] x` array style; variables initialized in the smallest scope.
- [ ] No `public` non-constant fields.
- [ ] Public classes and nontrivial public methods have well-formed Javadoc
      (summary sentence, blank line before tags, all-or-no `@param`).
- [ ] All comments/Javadoc/identifiers use English with American spelling.
- [ ] Blank lines separate logical units; no stray multi-blank runs.
