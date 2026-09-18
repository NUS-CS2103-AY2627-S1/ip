---
name: seedu-java-coding-standard
description: Apply the SE-EDU Java coding standard for this project when creating, editing, reviewing, or formatting Java code.
---

# SE-EDU Java Coding Standard

Use this skill for every Java code change in this project.

Source: https://se-education.org/guides/conventions/java/intermediate.html

## Scope

Follow the SE-EDU Java coding standard, basic + intermediate rules. For topics
not covered by the SE-EDU standard, fall back to the Google Java Style Guide.

## Naming

- Package names must be all lower case.
- The root package should be the project name, followed by logical package
  groups, e.g. `computah.ui`, `computah.storage`.
- Class and enum names must be nouns in PascalCase.
- Variable names must be in camelCase.
- Method names must be verbs in camelCase.
- Constant names must be in SCREAMING_SNAKE_CASE.
- Test method names may use
  `featureUnderTest_testScenario_expectedBehavior()`.
- Boolean variables and boolean methods should read like booleans, preferably
  using prefixes such as `is`, `has`, `was`, `can`, or `should`.
- Collection names should usually be plural.
- Use English names.

## Layout

- Use 4 spaces for indentation. Do not use tabs.
- Keep lines under 120 characters. Prefer wrapping before 110 characters when
  it improves readability.
- Wrapped lines should normally be indented by 8 spaces from the parent line.
- Use K&R braces:
  `if (condition) {`, `while (condition) {`, `public void method() {`.
- Separate logical units within a block with blank lines.

## Statements

- Every class must be in a package.
- Imports must be explicit. Do not use wildcard imports.
- Keep import ordering consistent:
  static imports, blank line, `java`/`javax` imports, blank line, third-party
  imports, blank line, project `computah` imports.
- Attach array specifiers to the type, e.g. `String[] args`.
- Declare variables in the smallest possible scope and initialize them where
  they are declared when reasonable.
- Do not use public variables except constants or simple data-only classes.
- Use braces for all `if`, `else`, `for`, `while`, and `do-while` bodies.
- Put conditionals on their own line.
- In `switch` statements, include an explicit `// Fallthrough` comment when a
  case intentionally falls through.

## Comments

- Comments must be in English, use American spelling, and avoid local slang.
- Write descriptive header comments for all public classes and public methods,
  except simple getters/setters, inherited overrides whose parent Javadoc
  applies exactly, and test methods.
- Use standard Javadoc format with a short first-sentence summary.
- In method Javadocs, start summaries with forms such as `Returns ...`,
  `Adds ...`, `Creates ...`, `Parses ...`, or `Shows ...`.
- When using `@param`, `@return`, or `@throws`, include all relevant entries and
  end descriptions with punctuation.
- Indent comments according to the surrounding code.

## Verification

After Java code changes:

- Run `./gradlew test`.
- Run `./gradlew javadoc` when Javadocs were added or changed.
- Run the project `test-ui` skill if user-facing behavior could be affected, or
  when required by `AGENTS.md`.
