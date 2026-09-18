---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding conventions to Java code in this project, including naming, layout, imports, control flow, variables, encapsulation, and Javadoc.
---

# SE-EDU Java coding standard

Use this skill whenever you create, edit, review, or refactor Java code in this
project. It applies to production code and tests under `src/main/java` and
`src/test/java`.

The source of these conventions is the SE-EDU Java coding standard (basic +
intermediate):
<https://se-education.org/guides/conventions/java/intermediate.html>

## Required conventions

- Put every class in a lower-case package whose root is the project name.
- Use PascalCase nouns for classes and enums, camelCase verbs for methods, and
  camelCase for variables. Use SCREAMING_SNAKE_CASE for constants.
- Keep names in English. Use descriptive names for variables with a large
  scope; short names are acceptable for small-scope scratch variables and
  loop indices.
- Name boolean fields and methods so that they read like boolean values, such
  as `isDone`, `hasData`, or `canExecute`.
- Use four spaces for indentation, never tabs. Keep lines at or below 120
  characters and prefer less than 110. Wrap long lines with an additional
  eight spaces of indentation.
- Use K&R braces, braces around every loop and conditional body, spaces around
  operators, and spaces after commas. Separate logical units in a block with a
  blank line.
- Keep imports explicit and consistently ordered. Do not use wildcard imports.
- Attach array brackets to the type, initialize variables at declaration when
  practical, and keep variables in the smallest possible scope.
- Keep instance fields non-public to preserve encapsulation, except for
  constants and genuine data-only classes.
- Write descriptive English header Javadocs for all public classes and public
  methods. Start the first sentence with a present-tense verb such as
  `Returns`, `Creates`, or `Parses`; document parameters and exceptions when
  they add useful information. Keep comments indented with the code.
- Add an explicit `// Fallthrough` comment when a switch case intentionally
  omits `break`.

## Workflow

1. Inspect the surrounding file and preserve the existing design unless a
   convention requires a change.
2. Apply the conventions above to the changed code and nearby code that is
   directly affected.
3. Check for lines longer than 120 characters, wildcard imports, missing
   packages, unbraced control flow, and missing public API Javadocs.
4. Run the project's Java 25 build and relevant tests before handing off the
   change. If command-line behavior changes, update and run the UI test plan.

