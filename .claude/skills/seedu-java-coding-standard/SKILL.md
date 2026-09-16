---
name: seedu-java-coding-standard
description: Java coding standard for this project (SE-EDU intermediate conventions -- https://se-education.org/guides/conventions/java/intermediate.html). Use whenever writing, reviewing, or editing Java source in this repository.
---

# SE-EDU Java Coding Standard (Intermediate)

Reference checklist for all Java code in this project, based on
https://se-education.org/guides/conventions/java/intermediate.html.
Apply this whenever writing new Java code or editing existing Java code.
When it conflicts with older code already in the repo, prefer fixing the
old code to match this standard over copying its style forward.

## Naming

- **Packages**: all lower case, e.g. `eve.task`, `eve.command`.
- **Classes/enums**: nouns in `PascalCase`, e.g. `TaskList`, `CommandWord`.
- **Methods**: verbs in `camelCase`, e.g. `getName()`, `computeTotalWidth()`.
  - Test methods: `featureUnderTest_testScenario_expectedBehavior()`, e.g.
    `toIndex_negative_throws()`. The scenario/behavior parts can be
    shortened or omitted when there's nothing more useful to say.
- **Variables**: `camelCase`.
- **Constants** (`static final`): `ALL_UPPER_CASE_WITH_UNDERSCORES`, e.g.
  `MAX_ITERATIONS`. Related constants share a common prefix, e.g.
  `COLOR_RED`, `COLOR_GREEN`.
- **Abbreviations/acronyms**: not all-caps when part of a name --
  `exportHtmlSource()`, not `exportHTMLSource()`.
- **Booleans**: name so they read like a yes/no question --
  `isSet`, `hasData`, `wasOpen`, `boolean canEvaluate()`.
- **Collections**: plural names -- `Collection<Point> points`, `int[] values`.
- **Scope vs. name length**: short names (`i`, `j`, `k`, `c`, `d`) only for
  small-scope scratch variables (loop counters, etc.); everything with a
  larger scope gets a descriptive name.
- All names in English.

## Layout and formatting

- 4-space indentation, no tabs.
- Line length: soft limit ~110 chars, hard limit 120.
- Wrapped continuation lines: indent 8 spaces (double the normal indent).
- K&R ("Egyptian") braces -- opening brace at the end of the line, never on
  its own line:
  ```java
  while (!done) {
      doSomething();
  }
  ```
- One blank line between logical units within a method/block.
- Whitespace: space after keywords (`while (true) {`), space after commas
  (`doSomething(a, b, c)`), spaces around binary operators (`a = b + c;`).
- Switch statements: explicit `// Fallthrough` comment on any case that
  intentionally has no `break`.
- Loop and conditional bodies are always wrapped in `{ }`, even for a
  single statement -- no bodyless `if`/`for`/`while` on one line.
- Put the condition of an `if`/`while`/`for` on its own line, never fused
  with the following statement on one line.

## Statements

- Every class belongs to a package (no default-package classes).
- Import ordering: static imports, then `java.*`, then `javax.*`, then
  `org.*`, then `com.*`, then everything else; alphabetical within each
  group; one blank line between groups.
- No wildcard imports (`import java.util.*;`) -- always import classes
  explicitly.
- Array specifiers attach to the type, not the variable: `int[] a`, not
  `int a[]`.
- Declare and initialize variables at the point of first use, in the
  smallest scope possible -- not all at the top of the method.
- A class's fields are never `public` unless the class is a pure data
  class with no behavior (this doesn't apply to `public static final`
  constants).

## Comments and Javadoc

- All comments in English.
- Header (Javadoc) comments are required on every non-private class and
  every non-private method. They may be omitted for: simple
  getters/setters, an `@Override` method whose inherited Javadoc already
  describes it exactly, and test classes/methods.
- Format:
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
  - `/**` on its own line; first sentence is a short summary.
  - Method summaries start with a third-person verb ("Returns...",
    "Adds..."), not the imperative ("Return...", "Add...").
  - `@param`/`@return`/`@throws` are all-or-nothing per parameter/return:
    if one parameter needs a description, describe all of them.
  - `@return` can be omitted when the return value is obvious or the
    method returns nothing.
  - Blank line between the description and the `@param`/`@return`/`@throws`
    block; no blank line between the Javadoc block and the class/method
    it documents.
  - A short one-liner is fine for simple members:
    `/** Number of connections to this database */`.
- Comments are indented to match the code they describe; trailing
  comments on the same line as code are fine:
  `process("ABC"); // process a dummy String first`.

## References

- se-education.org's own intermediate Java conventions page (source of
  this checklist) defers to the Google Java Style Guide for anything it
  doesn't cover.
