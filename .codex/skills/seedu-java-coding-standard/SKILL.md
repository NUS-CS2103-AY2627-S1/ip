---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding standard to Java source in this project.
---

# SE-EDU Java coding standard

Apply this skill to all Java source under `src/main/java` and `src/test/java`.
Use the [SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html)
as the authoritative reference. Use the Google Java Style Guide only for topics
not covered by the SE-EDU standard.

## Naming

- Use lowercase package names; use the project or group name as the root package.
- Use PascalCase nouns for classes and enums, and camelCase verbs for methods.
- Use camelCase for variables and SCREAMING_SNAKE_CASE for constants.
- Use English and avoid uppercase abbreviations inside names (`openDvdPlayer`, not `openDVDPlayer`).
- Give large-scope variables descriptive names; short names such as `i`, `j`, and
  `k` are reserved for small-scope scratch variables and nested-loop indices.
- Name booleans with prefixes such as `is`, `has`, or `was`; use `setFound(boolean
  isFound)`-style setters. Use plural names for collections.
- Give related constants a common prefix.
- Test methods may use `featureUnderTest_testScenario_expectedBehavior` with
  underscores.

## Layout and statements

- Use four spaces for indentation and K&R braces. Keep lines at or below 120
  characters, preferably below 110; wrap at readable, higher-level boundaries.
- Break after commas and before operators, keep a method name attached to its
  opening parenthesis, and indent wrapped lines by eight additional spaces.
- Put spaces around operators, after reserved words and commas, around binary or
  ternary colons, and after semicolons in `for` statements.
- Separate logical units in a block with one blank line.
- Put every class in a package and keep import ordering consistent. List imports
  explicitly; never use wildcard imports. Keep imports minimal and ordered by the
  project's formatter configuration.
- Attach array brackets to the type (`int[] values`).
- Initialize variables at declaration when possible and declare them in the
  smallest scope possible.
- Do not expose class variables publicly, except constants or behavior-free data
  classes.
- Always use braces for `for`, `while`, `do-while`, `if`, and `else` bodies,
  including single-statement bodies. Put conditional bodies on separate lines.
- Use the documented `switch` forms. Add `// Fallthrough` whenever a case
  intentionally continues without `break`.
- Follow the documented `try-catch-finally` layout.

## Comments and Javadoc

- Write comments in English using American spelling and no local slang.
- Add descriptive Javadoc to every public class and public method, except
  getters/setters, overriding methods whose inherited documentation applies
  exactly, and test classes or methods.
- Start a Javadoc summary with a short sentence such as `Returns ...`, `Adds ...`,
  or `Creates ...`. Put a blank line before tags, punctuate tag descriptions, and
  keep the Javadoc directly above its declaration.
- Document non-obvious private members and methods when doing so improves
  understanding. Keep comments indented with the code they describe.

Keep behavior unchanged when making style-only changes. Before handing off Java
changes, run the project's formatter and relevant Gradle validations.
