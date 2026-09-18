---
name: seedu-java-coding-standard
description: Apply and review the SE-EDU basic and intermediate Java coding standard for every Java code change in this project.
---

# SE-EDU Java Coding Standard

Apply the [SE-EDU Java coding standard (basic + intermediate)](https://se-education.org/guides/conventions/java/intermediate.html)
to all Java code created, edited, or reviewed in this project. Use the
[Google Java Style Guide](https://google.github.io/styleguide/javaguide.html) for topics the SE-EDU
standard does not cover.

## Required checks

- Use lowercase package names, PascalCase nouns for classes and enums, camelCase verbs for methods,
  camelCase for variables, and SCREAMING_SNAKE_CASE for constants.
- Keep names and comments in English. Name booleans to read as boolean values and collections in
  the plural. Do not capitalize an acronym when it is part of a longer identifier.
- Indent with 4 spaces. Prefer lines below 110 characters, never exceed 120 characters, and indent
  wrapped lines by 8 additional spaces.
- Use K&R braces. Put conditional bodies on separate lines and enclose every conditional and loop
  body in braces.
- Use conventional whitespace around operators and after keywords, commas, and semicolons. Separate
  logical units within a block with a blank line.
- Put every class in a package. List imports explicitly and order them consistently: static imports,
  Java platform imports, third-party imports, then project imports, with blank lines between groups.
- Attach array brackets to the type. Declare variables in the smallest practical scope and initialize
  them at declaration when a valid value is available. Keep fields non-public except constants or
  fields in behavior-free data classes.
- Add descriptive Javadoc to every public class and public method, except straightforward getters,
  setters, exact overrides, and test code. Keep Javadoc summaries grammatical and document parameters,
  return values, and thrown exceptions when that information adds value.
- Add `// Fallthrough` before an intentional fall-through switch case.

## Workflow

Before completing any Java code change, inspect all affected Java files against these rules and fix
violations introduced or exposed by the change. Run the project's applicable automated tests after
style corrections to confirm that behavior remains unchanged.
