---
name: seedu-java-coding-standard
description: The project's mandated Java coding standard (SE-EDU intermediate conventions, https://se-education.org/guides/conventions/java/intermediate.html), covering naming, layout, statements, and comments. This is required for all Java code in this repository - consult it before writing or editing any .java file (main or test), and whenever the user asks about code style, formatting, naming conventions, coding standards, or requests a style/convention review.
---

# SE-EDU Java Coding Standard (Intermediate)

Source: https://se-education.org/guides/conventions/java/intermediate.html

Apply this checklist whenever writing or editing Java code in this repository.
Where it conflicts with something already in the codebase, prefer fixing the
codebase to match this standard rather than matching the existing style,
unless the user says otherwise.

## Naming

- **Packages**: all lower case (e.g. `alzara.task`).
- **Classes/enums**: nouns, `PascalCase` (e.g. `TaskList`).
- **Variables**: `camelCase` (e.g. `taskIndex`).
- **Constants** (`static final`): `UPPER_SNAKE_CASE` (e.g. `MAX_ITERATIONS`).
- **Methods**: verbs, `camelCase` (e.g. `computeTotalWidth()`).
  - Test methods: `featureUnderTest_testScenario_expectedBehavior()`, e.g.
    `sortList_emptyList_exceptionThrown()`.
- **Abbreviations/acronyms**: not all-caps when part of a name -
  `exportHtmlSource()`, not `exportHTMLSource()`.
- **Language**: English, since the code is for an international audience.
- **Scope vs. name length**: long names for large-scope variables; short
  names (`i, j, k, m, n` for ints; `c, d` for chars) are fine for
  small-scope scratch variables, since the reader doesn't have to remember
  them for long.
- **Booleans**: name so they read like booleans, using `is`/`has`/`was`/etc.
  prefixes - `isVisible`, `hasData`, `boolean canEvaluate()`.
- **Collections**: plural names - `Collection<Point> points;`, `int[] values;`.
- **Associated constants**: share a common prefix so they read as a group -
  `COLOR_RED`, `COLOR_GREEN`, `COLOR_BLUE`.

## Layout

- **Indentation**: 4 spaces, never tabs.
- **Line length**: soft limit ~110 chars, hard limit 120. Wrap longer lines.
- **Wrapped-line indentation**: 8 spaces (double the normal 4).
- **Line breaks**: break after a comma; break before an operator; keep a
  method/constructor name attached to its opening parenthesis; prefer
  breaking at a higher syntactic level over a lower one.
- **Brackets**: K&R/Egyptian style - opening brace stays on the same line as
  the keyword, e.g.:
  ```java
  while (condition) {
      statements;
  }
  ```
- **Switch statements**: traditional `case`/`break` is fine. If a case
  intentionally falls through (no `break`), add an explicit `// Fallthrough`
  comment - leaving it out unintentionally is a common bug.
- **Whitespace inside statements**: spaces around binary operators
  (`a = (b + c) * d;`), after reserved words (`while (true) {`), after
  commas (`doSomething(a, b, c);`), and after `;` in a `for` header.
- **Blank lines**: separate logical units within a block with one blank line.

## Statements

### Package and imports
- Every class must declare a `package`.
- Import ordering must be consistent within a file (e.g. static imports,
  then `java.*`, `javax.*`, `org.*`, `com.*`).
- Import classes explicitly - never a wildcard (`import java.util.*;`).
  Explicit imports double as documentation of what a file depends on.

### Types
- Array specifiers attach to the type, not the variable:
  `int[] a = new int[20];`, not `int a[] = new int[20];`.

### Variables
- Initialize a variable where it's declared, and declare it in the smallest
  scope that needs it - don't declare everything at the top of a method.
- Never make a class field `public` unless the class is a plain data class
  with no behaviour (constants are exempt from this rule).

### Loops
- Always wrap the loop body in `{ }`, even for a single statement - omitting
  braces is error-prone.

### Conditionals
- The conditional's body goes on its own line, never `if (x) doThing();` on
  one line - this matters for IDE debugger breakpoints.
- Single-statement conditional bodies still get `{ }` - omitting them invites
  subtle bugs when a second statement is added later without adding braces.

## Comments

- Write comments in English, with American spelling.
- **Every public class and public method needs a descriptive header
  comment** (Javadoc), so a caller never has to read the implementation to
  know what it does. Getters/setters, `@Override`s whose parent Javadoc
  already applies exactly, and test classes/methods may skip it.
- **Javadoc format**:
  - Opening `/**` on its own line.
  - First sentence is a short summary (Javadoc puts it in the summary
    table) - phrase it as "Returns ...", "Adds ...", not "Return ...".
  - Continuation `*` aligned with the first; a space after each `*`.
  - Blank line between the description and the `@param`/`@return`/`@throws`
    block.
  - `@return` can be omitted for `void` methods or when the return value is
    obvious; `@param` can be omitted only if *every* parameter is
    self-explanatory - otherwise every parameter needs one.
  - No blank line between the Javadoc block and the class/method it
    documents.
  - A single-line form is fine for simple field-level comments, e.g.
    `/** Number of connections to this database */`.
- Indent comments to match the code they describe; a short trailing comment
  on the same line as a statement is fine (`process(s); // dummy first pass`).
