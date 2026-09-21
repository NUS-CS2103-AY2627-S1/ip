---
name: seedu-java-coding-standard
description: Checklist for writing or reviewing Java code in this project against the SE-EDU intermediate Java coding standard (naming, layout, statements, comments).
---

# SE-EDU Java coding standard

Use this skill whenever Java code is added or modified in this project. It condenses the
basic and intermediate rules from
[SE-EDU's Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html)
into a practical checklist. When in doubt, prefer the linked standard over this summary.

## Naming

- **Packages**: all lowercase, dot-separated logical groups (e.g. `keef.task`, `keef.storage`),
  not reversed-domain style.
- **Classes / enums**: nouns in `PascalCase` (e.g. `Deadline`, `Command`).
- **Variables**: `camelCase` (e.g. `taskList`, `byDateTime`).
- **Constants** (`static final`): `SCREAMING_SNAKE_CASE` (e.g. `DISPLAY_DATE_FORMAT`). Group
  related constants with a common prefix.
- **Methods**: verbs in `camelCase` (e.g. `parseDeadline()`, `getDescription()`).
- **Test methods**: `featureUnderTest_testScenario_expectedBehavior()`; the scenario or
  expected-behaviour part may be dropped when not needed (e.g. `parseOnDate_emptyOrInvalidDate_exceptionThrown()`).
- **Booleans**: prefix `is`/`has`/`was`/`can` for variables and accessors (e.g. `isDone`,
  `hasTime()`); setters read `void setDone(boolean isDone)`, not `void setIsDone(...)`.
- **Collections**: name with the plural form of the element (e.g. `List<Task> tasks`, not
  `taskList` for a bare list field).
- **Abbreviations/acronyms**: never all-caps inside a name — `parseHtml()`, not `parseHTML()`.
- All identifiers in English.
- Scale name length to scope: short names (`i`, `j`) are fine for a tight loop; anything with
  wider scope needs a descriptive name.

## Layout

- Indent with 4 spaces; never tabs.
- Line length: soft limit 110 characters, hard limit 120. Wrap anything over the hard limit.
- When wrapping, indent the continuation line 8 spaces deeper than the line being continued
  (double the normal 4-space indent), and break **before** the operator (including `.`, `+`,
  `&&`, `||`) rather than after it, e.g.:

  ```java
  System.err.println("Storage file appeared corrupted; moved to " + backupPath
          + " and starting with empty task list.");
  ```

- Braces: K&R style — opening brace on the same line as the declaration/statement, `else`/
  `catch`/`finally` on the same line as the preceding closing brace.
- `switch` statements: an arrow-form `case LABEL -> ...;` (as already used in this project) is
  preferred; if a classic fallthrough `case` is ever used, mark it with an explicit
  `// Fallthrough` comment.
- Whitespace: one space after `if`/`for`/`while`/`catch`/`switch` before the `(`; spaces around
  binary operators (`a = b + c`); a space after every comma; no space before a comma or
  semicolon.
- Blank lines: use exactly one blank line to separate logical sections within a method or
  class; don't stack multiple blank lines.

## Statements

- Every class lives in a package (no default-package classes).
- Import order, each group separated by a blank line, no wildcard imports:
  1. static imports
  2. `java.*`
  3. `javax.*`
  4. third-party libraries (alphabetical by root package)
  5. this project's own packages (`keef.*`)
- Array type markers go on the type, not the variable: `String[] parts`, never `String parts[]`.
- Declare and initialize a variable at the smallest scope that needs it — don't hoist a
  declaration to the top of a method "for tidiness" if it's only used in one branch.
- No public non-`static final` fields; expose state through methods (getters, etc.) instead.
  Constants (`public static final`) are the only public fields allowed.
- Loops and conditionals always use braces, even for a single-statement body — no bare
  `if (x) return;`.

## Comments

- Javadoc (`/** ... */`) is required on every non-private class and every non-private method,
  and on any private method whose behaviour or purpose is not obvious from its name and
  signature (e.g. a parsing helper with several fallback formats).
- Javadoc can be omitted on:
  - trivial getters/setters (a one-line pass-through with no extra behaviour);
  - overridden methods where the parent Javadoc already applies unchanged
    (use `{@inheritDoc}` if a short addendum is needed);
  - test classes and test methods (the method name carries the intent instead).
- Form, matching the style already used in this codebase:
  - opening `/**` on its own line, first sentence a short summary starting with a third-person
    verb ("Returns...", "Creates...", "Adds...", not "Return..." or "To create...");
  - a blank Javadoc line between the description and the `@param`/`@return`/`@throws` block;
  - `@param` for every parameter (or none at all — don't document some and skip others),
    `@return` when the method returns something non-obvious, `@throws` for every checked (and
    any notable unchecked) exception the caller can observe;
  - each tag description ends with a period, matching the existing files in this repo.
