---
name: seedu-java-coding-standard
description: The Java coding standard this project follows (naming, layout/formatting, code organization, comments). Use whenever writing or reviewing Java code in this project — before writing new code, and when checking existing code for compliance.
---

# seedu-java-coding-standard

Reference: https://se-education.org/guides/conventions/java/intermediate.html
(the SE-EDU "Basic + Intermediate" Java coding standard).

## Naming

- **Packages**: all lower case (e.g. `will.task`).
- **Classes/enums**: nouns in PascalCase (e.g. `Deadline`).
- **Variables**: camelCase (e.g. `taskList`).
- **Constants**: SCREAMING_SNAKE_CASE (e.g. `MAX_ITERATIONS`).
- **Methods**: verbs in camelCase (e.g. `getDescription()`).
- **Test methods**: `featureUnderTest_testScenario_expectedBehavior()`
  (later parts may be omitted).
- **Abbreviations/acronyms**: not all-caps inside names — `exportHtml()`,
  not `exportHTML()`.
- **Booleans**: read like a boolean — prefixes such as `is`, `has`,
  `was`, `can` (e.g. `isDone`, `hasData`, `canEvaluate()`).
- **Collections**: plural names (e.g. `Collection<Task> tasks;`).
- **Loop indices**: short names (`i`, `j`, `k`) are fine for
  small-scope scratch variables; `j`/`k` only for nested loops.
- **Associated constants**: share a common prefix (e.g. `COLOR_RED`,
  `COLOR_GREEN`).
- All names in English.

## Layout and formatting

- Indentation: 4 spaces, never tabs.
- Line length: soft limit 110 characters, hard limit 120.
- Wrapped lines: indent by 8 spaces (double the normal indent). Break
  after commas, before operators (including `.`); keep a
  method/constructor name attached to its opening `(`.
- Braces: K&R style — opening brace on the same line as the statement.
- Loop and conditional bodies are always wrapped in `{ }`, even for a
  single statement.
- Whitespace: spaces around binary operators, after commas, after
  keywords like `while`/`for`/`if`; no space before `;` or `,`.
- One blank line between logical units within a block/method.

## Code organization

- Every class belongs to a package — no default-package classes.
- Imports: no wildcard imports — list each imported class explicitly.
  Order: static imports first, then the rest sorted alphabetically by
  fully-qualified name (`java.*` / `javax.*` before third-party before
  this project's own packages is a common grouping, but the binding
  rule is: consistent, alphabetical, no wildcards).
- Arrays: type specifier attaches to the type, not the variable —
  `int[] values;`, not `int values[];`.

## Variables

- Initialize where declared, in the smallest scope possible.
- Class fields are never `public` unless the class is a pure data
  class with no behavior (constants are the exception).

## Comments

- English, American spelling.
- Header (Javadoc) comments are required for every public class and
  every public method, **except**: getters/setters, methods that
  simply override a parent method whose Javadoc already applies
  (`@Override` with no behavior change worth separately documenting),
  and test classes/methods.
- Javadoc format:
  ```
  /**
   * One-sentence summary, ending in a period.
   *
   * More detail if needed.
   *
   * @param name What it is.
   * @return What's returned (omit if obvious from the summary).
   * @throws SomeException When/why it's thrown.
   */
  ```
  Opening `/**` on its own line, a blank `*` line between the
  description and the `@param`/`@return`/`@throws` tags, no blank
  line between the Javadoc and the declaration it documents.

## Applying this in this project

- New code: follow all of the above from the start.
- Existing code: when touched for an unrelated change, bring it into
  compliance if it's a small, safe fix (e.g. reordering an import,
  adding a missing class Javadoc); don't do unrelated drive-by
  rewrites in the same commit as a functional change.
