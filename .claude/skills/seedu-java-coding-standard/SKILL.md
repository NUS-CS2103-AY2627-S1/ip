---
name: seedu-java-coding-standard
description: Reference checklist for the SE-EDU intermediate Java coding standard (naming, layout, statements, comments/Javadoc). Use when writing, reviewing, or auditing Java code in this project, to ensure it follows the mandated style.
---

# SE-EDU Java Coding Standard (Intermediate)

Source: https://se-education.org/guides/conventions/java/intermediate.html

This project's Java code must follow this standard. When writing or editing `.java` files, apply
the rules below; when auditing existing code, check against them and fix violations.

## Naming

- **Packages**: all lower case (e.g. `chud.task`).
- **Classes/enums**: nouns, PascalCase (e.g. `Line`, `TaskList`).
- **Variables**: camelCase (e.g. `line`, `taskCount`).
- **Constants** (`static final`): all uppercase, words separated by underscores (e.g.
  `MAX_ITERATIONS`). Related constants share a common prefix (e.g. `COLOR_RED`, `COLOR_GREEN`).
- **Methods**: verbs, camelCase (e.g. `getName()`, `computeTotalWidth()`).
- **Test methods**: `featureUnderTest_testScenario_expectedBehavior()` (parts may be omitted).
- **Abbreviations/acronyms**: not all-uppercase within a name -- `exportHtmlSource()`, not
  `exportHTMLSource()`.
- **Language**: all names in English.
- **Scope**: long, descriptive names for large-scope variables; short names (even single-letter
  scratch names like `i`, `j`, `k`, `c`, `d`) are fine for small, short-lived scopes (loop
  counters, etc.).
- **Booleans**: prefix with `is`/`has`/`was`/etc. (`isSet`, `hasData`, `boolean hasLicense()`).
- **Collections**: plural names (`Collection<Point> points`, `int[] values`).

## Layout and formatting

- **Indentation**: 4 spaces, never tabs.
- **Line length**: soft limit 110 chars, hard limit 120.
- **Wrapped lines**: indent continuation lines by 8 spaces (double the normal indent). Break
  after commas, before operators (including `.`); keep a method/constructor name attached to its
  opening parenthesis; prefer breaking at a higher syntactic level.
- **Braces**: K&R/Egyptian style -- opening brace on the same line as the statement, never on its
  own line:
  ```java
  public void someMethod() throws SomeException {
      ...
  }

  if (condition) {
      ...
  } else {
      ...
  }
  ```
- **Switch**: traditional form with `break`, or modern arrow form (`case X -> ...`). If a
  traditional `case` intentionally has no `break`, mark it with an explicit `// Fallthrough`
  comment.
- **Whitespace**: spaces around binary operators (`a = (b + c) * d;`), after reserved words
  (`while (true) {`), after commas (`doSomething(a, b, c, d);`).
- **Blank lines**: separate logically distinct sections within a block with exactly one blank
  line.

## Statements

- **Package**: every class must declare a `package`.
- **Imports**: always explicit -- never a wildcard (`import java.util.*`). Keep a consistent
  order (static imports, then `java.*`, then `javax.*`, then third-party, then project packages).
- **Arrays**: the specifier attaches to the type, not the variable -- `int[] a`, not `int a[]`.
- **Variables**: initialize where declared; declare in the smallest scope possible.
- **Public fields**: never `public` unless the class is a pure data class with no behavior
  (constants are exempt from this rule).
- **Loops and conditionals**: always brace the body, even a single statement -- never
  `if (x) doThing();` or a braceless loop body. The conditional/loop header goes on its own line;
  never put the guarded statement on the same line as the header.

## Comments and Javadoc

- Written in English (American spelling).
- **Required** on every non-private class and non-private method, with these exceptions where a
  doc comment is optional: getters/setters, an `@Override` whose behavior exactly matches the
  parent's documented contract (inherits it), and test classes/methods (whose long, descriptive
  names typically make a doc comment redundant).
- Format:
  ```java
  /**
   * Returns lateral location of the specified position.
   * If the position is unset, NaN is returned.
   *
   * @param x X coordinate of position.
   * @param y Y coordinate of position.
   * @return Lateral location.
   * @throws IllegalArgumentException If zone is <= 0.
   */
  ```
  - `/**` on its own line; `*` on each continuation line, aligned, with a space after it.
  - First sentence is a brief summary; for methods, start with a third-person verb ("Returns...",
    "Adds...", "Parses..."), not a gerund ("Returning...").
  - Blank line between the description and the `@param`/`@return`/`@throws` block.
  - `@param`: all parameters or none (omit the whole block only if every parameter is
    self-explanatory from its name).
  - `@return`: omit if the method returns nothing, or the return value is obvious from the
    summary.
  - No blank line between the doc comment and the class/method it documents.
  - A short, single-line form is fine for simple members: `/** Number of open connections. */`.
- Trailing comments are fine for a brief aside (`count++; // skip the header row`).
