---
name: seedu-java-coding-standard
description: "Apply the SE-EDU intermediate Java coding standard when writing, refactoring, or reviewing Java in this project."
---

# SE-EDU Java Coding Standard

Use this skill for Java implementation, refactoring, or code-review work in this repository when style compliance matters. Do not use it for non-Java work or to add a formatter, linter, or dependency unless the user explicitly asks.

The authoritative reference is the [SE-EDU Java coding standard (basic + intermediate)](https://se-education.org/guides/conventions/java/intermediate.html). For matters not covered there, use the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).

## Apply the standard

- Preserve the task's requested behavior and the repository's established conventions. A style-only task must not change behavior.
- Use lowercase package names; PascalCase nouns for classes and enums; camelCase verbs for methods; camelCase variables; and SCREAMING_SNAKE_CASE constants.
- Prefer descriptive names for long-lived variables. Use plural names for collections and `is`/`has`/`can`-style names for booleans. Test method names may use `featureUnderTest_testScenario_expectedBehavior`.
- Use four spaces for indentation, K&R braces, braces for every loop and conditional body, and one space around operators and after commas.
- Keep lines at or below 120 characters. Wrap for readability, normally after commas or before operators, with continuation indentation eight spaces beyond the parent line.
- Keep imports explicit (never wildcard imports), minimal, and consistently ordered. Keep every class in a package.
- Declare variables in the smallest practical scope and initialize them at declaration when a valid initial value exists. Do not expose mutable class fields publicly.
- Separate logical units in a block with one blank line where it improves readability. Use explicit `// Fallthrough` comments for intentional switch fallthrough.
- Write comments in English with American spelling. Add descriptive Javadoc to classes and public methods, except simple getters/setters, test code, and overrides whose inherited Javadoc applies exactly.

## Verify

After style changes, inspect only the affected Java files for indentation, imports, line length, braces, and naming. Run the project's relevant Gradle checks or tests when they are available. Report any deliberate deviation from the standard rather than silently broadening the task.
