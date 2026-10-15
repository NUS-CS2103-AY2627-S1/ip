---
name: seedu-java-coding-standard
description: Apply the SE-EDU Java coding standard when editing Java sources in this project.
---

# SE-EDU Java Coding Standard

Use these project conventions when creating or editing Java code:

- Keep package names lowercase; use PascalCase for classes and camelCase for
  methods and variables. Name booleans with `is`, `has`, `can`, or similar.
- Use four spaces for indentation, K&R braces, and braces for every loop and
  conditional body, even when it has one statement.
- Keep lines at 120 characters or fewer where practical. Wrap at readable,
  higher-level boundaries.
- Separate logical sections with one blank line. Use one space around binary
  operators and after commas.
- List imports explicitly and keep their ordering consistent. Do not use
  wildcard imports.
- Keep fields non-public unless they are constants. Use uppercase names with
  underscores for constants.
- Add descriptive JavaDoc to public classes and public methods, except simple
  getters, setters, and overrides whose inherited documentation applies.
- Name JUnit tests with the optional
  `featureUnderTest_testScenario_expectedBehavior` convention.
