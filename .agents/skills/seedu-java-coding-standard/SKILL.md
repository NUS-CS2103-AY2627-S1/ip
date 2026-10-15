---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding standard when creating, modifying, reviewing, or documenting Java code in this project.
---

# SE-EDU Java Coding Standard

Use this skill for every Java source or test change in this repository. Treat the
[SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html)
as authoritative. For topics it does not cover, follow the Google Java Style Guide.

## Required Conventions

- Put every class in a logical, lower-case package rooted at `serina`.
- Use English noun-based `PascalCase` names for classes and enums, verb-based `camelCase` names for methods,
  `camelCase` names for variables, and `SCREAMING_SNAKE_CASE` names for constants.
- Name booleans so they read as boolean conditions, normally with prefixes such as `is`, `has`, `can`, or
  `should`. Use plural names for collections.
- Prefer descriptive names for members and other large-scope variables. Short iterator or temporary names are
  acceptable only when their scope is small and their meaning is obvious.
- Indent with 4 spaces and no tabs. Keep lines below 110 characters when practical and never exceed 120
  characters. Indent wrapped lines by 8 spaces relative to the parent line.
- Use K&R braces. Put conditional bodies on separate lines and always enclose loop and conditional bodies in
  braces, including single-statement bodies.
- Use spaces around operators and after commas and semicolons. Separate logical units within a block with a blank
  line where it improves readability.
- List imports explicitly without wildcards. Group and order imports consistently: static imports, Java imports,
  third-party imports, then project imports, with blank lines between groups.
- Attach array brackets to the type. Initialize variables where declared when possible and keep declarations in
  the smallest useful scope. Do not expose mutable class fields publicly.
- Write comments in English using American spelling. Explain intent rather than restating code.
- Add descriptive Javadocs to all public classes and methods, except straightforward getters/setters, exact
  overrides, and test code where the standard permits omission. Start method summaries with a third-person verb
  such as `Returns`, `Adds`, or `Checks`, and punctuate all tag descriptions.
- Name JUnit methods with `featureUnderTest_testScenario_expectedBehavior` when a descriptive test name benefits
  from separators.

## Workflow

1. Inspect surrounding Java code before editing and preserve the repository's established package boundaries.
2. Apply the required conventions while implementing the requested behavior; do not perform unrelated refactors.
3. Audit changed Java files for tabs, wildcard imports, lines over 120 characters, missing braces, weak names, and
   missing public API Javadocs.
4. Run the relevant Gradle tests and Javadoc checks with Java 25. Report any check that could not be run.
