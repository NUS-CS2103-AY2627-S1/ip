---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding conventions to Java code in this project.
---

# SE-EDU Java coding standard

Apply this skill to every Java code change in this project. The authoritative reference is the [SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html); use the Google Java Style Guide for topics not covered there.

## Required conventions

- Keep package names lowercase and organize every class under a package.
- Use PascalCase nouns for classes, enums, and records; camelCase verbs for methods; camelCase for variables; and SCREAMING_SNAKE_CASE for constants.
- Name boolean fields and methods with readable prefixes such as `is`, `has`, `can`, or `should` where appropriate.
- Use four-space indentation, K&R braces, explicit imports, and consistent import ordering. Do not use wildcard imports.
- Keep lines at or below 120 characters, wrapping continuation lines with increased indentation and placing breaks at readable boundaries.
- Use braces for every loop and conditional body, including single-statement bodies.
- Separate logical units with blank lines and initialize variables at their declaration when practical.
- Write comments in English using American spelling.

## JavaDoc

- Add header comments to every class and to all non-private methods. Add them to non-trivial private methods as well.
- Start method summaries with an action verb such as “Returns…”, “Adds…”, “Sends…”, “Parses…”, or “Verifies…”. Do not start with `@return` or a gerund such as “Returning”.
- Keep `/**` on its own line, align continuation lines, and separate the summary from `@param`, `@return`, and `@throws` tags with a blank line.
- End parameter descriptions with punctuation and document parameters or return values when the description adds useful information.

## Verification

After Java changes, inspect the touched files for these conventions and run the relevant Gradle tests. Keep test method names in the `featureUnderTest_testScenario_expectedBehavior` form when a scenario-specific name improves clarity.
