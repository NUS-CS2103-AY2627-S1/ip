---
name: seedu-java-coding-standard
description: Apply the SE-EDU Java coding standard to all Java code in this project.
---

# SE-EDU Java coding standard

Follow the SE-EDU [Java coding standard](https://se-education.org/guides/conventions/java/index.html)
for every Java source and test file in this repository. Use the Google Java Style Guide for
topics not covered by SE-EDU.

## Required rules

- Use lowercase package names; use English, PascalCase nouns for classes and enums.
- Use camelCase for variables and verb-based methods. Use SCREAMING_SNAKE_CASE for constants.
- Name booleans with prefixes such as `is`, `has`, `was`, `can`, or `should`.
- Use plural names for collections. Iterator variables may use `i`, `j`, or `k` in small loop scopes.
- Use four spaces, never tabs, and keep lines at 120 characters or fewer (prefer under 110).
- Use K&R braces. Always use braces for loops and conditionals, including single statements.
- Keep method names attached to `(`, wrap long lines at readable boundaries, and indent wrapped
  lines by eight spaces relative to the parent line.
- Put every class in a package and use explicit, consistently ordered imports.
- Put array brackets next to the type, initialize variables at declaration when practical, and
  keep variables in the smallest possible scope.
- Preserve encapsulation: do not expose mutable class state through public fields. Constants are
  exempt, and simple data classes may expose fields when appropriate.
- Separate logical units in a block with one blank line and use spaces around operators and after
  commas.
- Write comments and Javadocs in English using American spelling. Public classes and methods need
  descriptive header comments, and non-trivial private methods need them as well.
- Javadocs start with a concise sentence, use correctly formatted tags, and have no blank line
  between the documentation block and its declaration.
- Use the test naming form `featureUnderTest_testScenario_expectedBehavior()` where underscores
  improve test readability.

Before completing Java changes, run the repository's existing Checkstyle and test tasks and fix
violations rather than suppressing them.
