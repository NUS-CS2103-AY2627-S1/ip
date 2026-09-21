---
name: seedu-java-coding-standard
description: Apply the SE-EDU intermediate Java coding standard to all production and test Java edits in the Margit project.
---

# SE-EDU Java Coding Standard

Use this skill for every Java production-code or test-code change in this project. Apply the SE-EDU intermediate standard at <https://se-education.org/guides/conventions/java/intermediate.html>.

## Naming and packages

- Keep every class in the `margit` package hierarchy. Package names are lowercase; package groups reflect responsibility, such as `margit.ui` and `margit.task`.
- Use PascalCase nouns for classes and enums, camelCase verbs for methods, and camelCase for variables.
- Name boolean variables and methods with `is`, `has`, `can`, `should`, or similar boolean prefixes where practical.
- Use plural names for collections and `UPPER_SNAKE_CASE` for constants.
- Name tests using `featureUnderTest_testScenario_expectedBehavior` when a descriptive camelCase name would be too long.

## Layout and code structure

- Use four-space indentation, K&R braces, braces for every loop and conditional body, and explicit imports (never wildcard imports).
- Keep source lines at or below 120 characters; wrap at logical boundaries and indent continuation lines by eight spaces relative to the enclosing scope.
- Put related statements in small logical blocks separated by a blank line.
- Keep declarations in the smallest practical scope and initialize them where declared.

## Documentation

- Write Javadoc headers for all non-private classes and methods, except straightforward getters/setters and overrides whose inherited documentation applies exactly.
- Document non-trivial private methods. Describe what a method guarantees rather than repeating implementation details.
- Start Javadoc summaries with a third-person verb such as `Returns`, `Adds`, `Parses`, or `Displays`. Add `@param`, `@return`, and `@throws` tags when they clarify behavior.
- Write all comments in English using American spelling.

## Before finishing

- Review changed Java files for naming, package placement, line length, imports, braces, and Javadoc coverage.
- Run the relevant Gradle tests. Update tests when behavior changes, following the project's high-value-method coverage target.
