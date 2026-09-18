---
name: seedu-java-coding-standard
description: SE-EDU intermediate Java coding-standard checklist (naming, layout, imports, Javadoc). Use whenever writing or reviewing Java code in this project.
---

# SE-EDU Java Coding Standard

Source: https://se-education.org/guides/conventions/java/intermediate.html

Apply this to all Java source in the project (`src/main/java` and `src/test/java`),
except where a rule is explicitly scoped to non-test code below.

## Naming

- **Packages**: all lowercase, e.g. `milo.task`. Never an institutional prefix
  (`edu.nus.comp.*`).
- **Classes / enums**: `PascalCase` nouns, e.g. `TaskList`, `Deadline`.
- **Variables**: `camelCase`. Descriptive for long-lived variables; short scratch
  names (`i`, `j`, `k`, `m`, `n` for ints; `c`, `d` for chars) are fine for
  short-lived loop/index variables. Collections use plural names, e.g.
  `List<Task> tasks`.
- **Booleans** (variables and methods): prefix with `is`, `has`, `was`, `can`, or
  `should`, e.g. `isDone`, `hasTime`, `shouldMarkDone`. Boolean setters take the
  form `void setFound(boolean isFound)`.
- **Constants**: `SCREAMING_SNAKE_CASE`, e.g. `MAX_ITERATIONS`. Related constants
  share a common prefix.
- **Methods**: `camelCase` verbs, e.g. `getName()`, `computeTotalWidth()`.
- **No uppercase acronyms inside names**: `exportHtmlSource()`, not
  `exportHTMLSource()`. Applies to class names too, e.g. `Ui` not `UI`.
- **Test methods**: `featureUnderTest_testScenario_expectedBehavior()`, e.g.
  `sortList_emptyList_exceptionThrown()`.
- American spelling throughout, in code and comments.

## Layout & formatting

- 4 spaces per indent level, never tabs. Wrapped continuation lines get 8 spaces
  (double the normal indent).
- Soft line-length limit 110 chars, hard limit 120.
- K&R (Egyptian) brace style — opening brace on the same line, e.g.
  `while (!done) {`.
- Every control structure (`if`, `for`, `while`, etc.) uses braces, even for a
  single statement — never `if (isDone) doCleanup();`.
- Space after Java keywords (`while (true) {`), after commas
  (`doSomething(a, b, c)`), around binary/ternary operators, and after `;` in a
  `for` header.
- One blank line between logical units inside a method body.
- Method form: `public void someMethod() throws SomeException { ... }`.
- Switch statements: `case` labels are indented one level (4 spaces) deeper
  than `switch`, and each case's body one level deeper again — this is what
  the project's Checkstyle config (`config/checkstyle/checkstyle.xml`)
  enforces. Mark an intentional fallthrough with `// Fallthrough`.
- Array brackets attach to the type, not the variable: `int[] a`, not `int a[]`.

## Imports & code organization

- One class (or a small tightly-related family) per file; every class lives in a
  package.
- Never use a wildcard import (`import java.util.*;`) or reference a type by its
  fully-qualified name inline (`java.util.List<String>`) — always import the
  type explicitly and use its simple name.
- Import order, each group internally alphabetical: static imports, `java.*`,
  `javax.*`, third-party (`org.*`, `com.*`, ...), then this project's own
  packages (`milo.*`) last.
- Initialize variables at the point of declaration, in the smallest scope that
  works.
- A class field is `public` only for a pure data class with no behavior, or a
  constant; otherwise keep it `private`/`protected` and expose access through
  methods.

## Javadoc

- Every non-private class and non-private method needs a header comment,
  **except**: getters/setters, an overridden method whose parent Javadoc already
  applies unchanged, and test classes/methods (their descriptive method names
  carry that role instead).
- A non-trivial private method (real logic, not a one-line delegation) should
  also get one.
- Format:
  ```java
  /**
   * Returns [short summary sentence, verb-first: "Returns...", "Adds...", not
   * "Return..." or "Returning..."].
   * [Optional further detail.]
   *
   * @param paramName Description, ending with a period.
   * @return Description of the return value.
   * @throws ExceptionType Condition that triggers it.
   */
  ```
- `@param` for either all parameters or none; `@return` omitted when the method
  is `void` or the return value is self-evident from the summary.
- For an override that needs to add detail beyond the inherited contract, use
  `{@inheritDoc}` plus the extra notes rather than repeating the parent's text.
- A single-line field comment is fine: `/** Brief description */ private int field;`.
