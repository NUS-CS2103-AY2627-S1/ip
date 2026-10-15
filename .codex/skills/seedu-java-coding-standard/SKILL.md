---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding conventions to this project’s Java source and tests.
---

# SE-EDU Java Coding Standard

Apply these conventions to all Java changes in this project. Use the linked
SE-EDU standard as the authority for details not listed here:
https://se-education.org/guides/conventions/java/intermediate.html

- Put every class in a named, lowercase package and keep imports explicit and
  consistently ordered.
- Use PascalCase for classes/enums, camelCase for variables/methods, and
  SCREAMING_SNAKE_CASE for constants. Boolean names should use prefixes such
  as `is`, `has`, `can`, or `should`.
- Use four-space indentation, K&R braces, wrapped lines at 120 characters,
  braces around all conditionals and loops, and expanded try/catch blocks.
- Keep variables in the smallest practical scope and initialize them where
  declared. Keep collections plural and class fields encapsulated.
- Add descriptive Javadocs to every class and public method, and to
  non-trivial private methods. Document purpose and observable behavior.
- Use descriptive test names, including
  `featureUnderTest_testScenario_expectedBehavior()` when a longer name is
  needed.
- Do not commit generated binaries or build output such as `*.class`, `*.jar`,
  `target/`, `build/`, or `out/`.

Before finishing a change, inspect the diff for naming, brace/layout,
documentation, package/import, and generated-file violations, then run the
project’s Gradle tests.
