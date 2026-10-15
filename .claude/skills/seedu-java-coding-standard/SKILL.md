---
name: seedu-java-coding-standard
description: Java coding standard for this project, based on the SE-EDU intermediate Java coding standard (se-education.org/guides/conventions/java/intermediate.html). Apply to all Java code written or reviewed in this project.
---

# SE-EDU Java Coding Standard

This project follows the [SE-EDU intermediate Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html). Apply every rule below to all new and edited Java code in `src/`. For anything not covered here, fall back to the Google Java Style Guide.

## Naming

- **Packages**: all lowercase, e.g. `tblade.task`.
- **Classes / enums**: nouns in `PascalCase`, e.g. `Deadline`, `TaskType`.
- **Variables**: `camelCase`. Give large-scope variables descriptive names; short names (`i, j, k, m, n` for integers, `c, d` for characters) are fine for small-scope scratch/loop variables (nested loops: `i`, `j`, `k`).
- **Constants**: `SCREAMING_SNAKE_CASE`, e.g. `MAX_TASKS`. Related constants share a common prefix.
- **Methods**: verbs in `camelCase`, e.g. `getDescription()`, `parseTaskIndex()`.
  - **Test methods**: three-part `featureUnderTest_testScenario_expectedBehavior()`, e.g. `add_atCapacity_throwsTBladeException()`. Parts may be omitted when a part doesn't apply.
- **Booleans**: prefix variables/methods with `is`, `has`, `was`, `can`, or `should`, e.g. `isDone`, `isRunning`.
- **Collections**: use plural names, e.g. `List<Task> tasks`.
- **Abbreviations/acronyms**: never all-caps inside a name — `exportHtmlSource()`, not `exportHTMLSource()`.
- All names in English.

## Layout

- 4-space indentation, no tabs. Wrapped continuation lines indent 8 spaces.
- Line length: soft limit ~110 chars, **hard limit 120 chars** — never exceed 120.
- Break long lines after commas or before operators (including `.`); keep a method/constructor name attached to its opening `(`.
- **K&R (Egyptian) braces**: opening brace on the same line as the statement.
- **Braces are mandatory** on every `if`/`else`/`for`/`while`/`do-while` body, even single-statement ones. Put the condition and body on separate lines — never `if (x) doThing();` on one line.
- `} else if (...) {` / `} else {` on one line (closing brace, `else`, opening brace together).
- `switch`: every case must end in `break;`/`return`/`throw`, or an explicit `// Fallthrough` comment. `case` labels are indented one level (4 spaces) in from `switch`, and each case's body is indented one further level from `case` — not aligned flush with `switch`.
- Whitespace: space around binary operators, space after Java keywords (`while (`, `if (`), space after commas, space after `;` in a `for` header.
- One blank line between logical units inside a block.

## Statements

- Every class lives in a package (no default package).
- **No wildcard imports** — import each class explicitly.
- **Import order** (matches `config/checkstyle/checkstyle.xml`'s `CustomImportOrder`), blank line between each group, alphabetical within a group:
  1. static imports
  2. `java.*` / `javax.*`
  3. third-party `org.*`
  4. everything else — third-party `com.*`, `javafx.*`, and this project's own `tblade.*` packages all share this one catch-all group (no blank line between them)
- Array specifiers attach to the type, not the variable: `String[] args`, not `String args[]`.
- Initialize variables where declared; declare in the smallest scope that works.
- Class fields are never `public` unless the class is a pure data holder with no behavior (a `record` component is fine — it's not a raw public field).
- Loop and conditional bodies always use `{ }`, regardless of statement count.

## Comments / Javadoc

- Comments in English (American spelling).
- Header (Javadoc) comments required on every public class and public/protected method. Getters/setters, overrides whose parent Javadoc already applies exactly, and test classes/methods may skip it.
- Javadoc block format:
  ```java
  /**
   * Short summary sentence, verb-first (Returns ..., Creates ..., Adds ...).
   *
   * @param name what it is
   * @return what's returned (omit if void or obvious)
   * @throws SomeException when it's thrown
   */
  ```
  `/**` and `*/` each on their own line, `*` aligned and followed by a space, a blank line between the summary and the `@param`/`@return`/`@throws` block.
- `@param` for every parameter (all or none), `@return` unless void/obvious, `@throws` for every checked exception the method declares.
- `@inheritDoc` is fine for an override that just repeats the parent's contract.

## When applying this skill

- When **writing new code**: follow every rule above as you write, don't fix it up afterward.
- When **reviewing/auditing existing code**: check naming, line length (`awk 'length > 120'`), brace style, import order/wildcards, and Javadoc presence/format; fix violations, and leave already-compliant code untouched.
- Checkstyle (`config/checkstyle/checkstyle.xml`) mechanically enforces the objectively-checkable subset of this standard and runs automatically on `./gradlew check`/`build` and via the pre-commit hook (see README.md). Run `./gradlew checkstyleMain checkstyleTest` after editing Java code to catch anything a manual read misses (e.g. exact indentation depth, line-wrap placement) — it's a backstop, not a replacement for applying the rules above while writing.
