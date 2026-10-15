---
name: seedu-java-coding-standard
description: The SE-EDU intermediate Java coding standard this project follows for all Java code (naming, layout, comments). Use whenever writing or editing any .java file in this repo, or reviewing Java code for style.
---

# SE-EDU Java Coding Standard

Full reference: https://se-education.org/guides/conventions/java/intermediate.html

This project follows that standard for all Java code. Summary of the rules
that matter day-to-day:

## Naming

- Packages: all lower case, `projectname.logicalgroup` style (this project
  uses a single `coco` package).
- Classes/enums: nouns in `PascalCase` (`Task`, `Command`).
- Methods: verbs in `camelCase` (`getStatusIcon()`, `parseDeadline()`).
- Variables: `camelCase`.
- Constants (`static final`): `UPPER_SNAKE_CASE` (`DISPLAY_FORMAT`).
- Test methods: `featureUnderTest_testScenario_expectedBehavior()`, e.g.
  `parseDeadline_emptyDate_exceptionThrown()`.
- Don't uppercase abbreviations inside names: `exportHtmlSource()`, not
  `exportHTMLSource()`.
- Boolean-returning methods/variables get an `is`/`has`/`was`/`can`/`should`
  prefix.
- Collections get plural names.
- Loop index variables: `i`, `j`, `k`, with `j`/`k` reserved for nested
  loops.
- All names in English.

## Layout

- 4-space indentation, no tabs.
- Line length: soft limit 110 chars, hard limit 120.
- Wrapped lines indent 8 spaces (double the normal indent) from the parent
  line.
- K&R braces: opening brace on the same line as the statement; `else` on
  the same line as the preceding closing brace.
- Braces are mandatory for every `if`/`for`/`while` body, even a single
  statement.
- Method signature: `public void someMethod() throws SomeException {` —
  keep the name attached to `(`.
- A `switch` case that intentionally falls through needs an explicit
  `// Fallthrough` comment.
- Ternary expressions: single line, or three-line form with the `?` and
  `:` starting their own lines:
  ```java
  String arg = input.length() > commandWord.length()
          ? input.substring(commandWord.length()).trim()
          : "";
  ```
- One blank line between logical blocks within a method.
- Array brackets attach to the type, not the variable: `int[] a`.
- Whitespace: spaces around operators and after keywords/commas
  (`if (x)`, `foo(a, b)`); no space between a method name and its `(`.

## Imports

- Every class is in a package (no default-package classes).
- No wildcard imports — always import explicitly.
- Import order: static imports first, then `java.*`, `javax.*`, `org.*`,
  `com.*`, `javafx.*`, each group alphabetized, with a blank line between
  groups.

## Variables

- Initialize where declared, in the smallest scope possible.
- Class fields are never `public` unless the class is a pure data class
  (constants are exempt from this rule).

## Comments / JavaDoc

- English, American spelling.
- Header (JavaDoc) comments required on all public/non-private classes
  and methods. Omissible for getters/setters, and for overridden methods
  whose parent JavaDoc already applies (just keep `@Override`, no repeated
  comment) — see `Task.toString()` vs. its overrides for the pattern this
  project follows.
- Format:
  ```java
  /**
   * Returns lateral location of the specified position.
   * If the position is unset, NaN is returned.
   *
   * @param x X coordinate of position.
   * @return Lateral location.
   * @throws IllegalArgumentException If zone is <= 0.
   */
  ```
  - First sentence is a short summary (it shows up in generated method
    tables).
  - Method comments describing what a method does are phrased as
    "Returns...", "Sends...", etc. — not imperative ("Return...").
  - Blank line between the description and the `@param`/`@return`/`@throws`
    block.
  - Either all parameters get `@param`, or none do.
  - `@return` can be omitted when the return value is obvious from the
    summary.
  - A trivial one-liner can use the single-line form: `/** Description */`.
- Test classes/methods don't need JavaDoc.

## Applying this

Any new or edited `.java` file in this repo should already comply with the
above. When editing existing code, fix nearby style issues you notice, but
don't do a drive-by rewrite of unrelated code just to chase full compliance.
