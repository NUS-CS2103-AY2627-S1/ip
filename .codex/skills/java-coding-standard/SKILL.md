---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding conventions to code in this project, including naming, layout, imports, encapsulation, control flow, and Javadoc comments.
---

# SE-EDU Java Coding Standard

Apply this skill to all Java production and test code in this repository. The authoritative source is the [SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html); use the Google Java Style Guide for topics not covered there.

## Required conventions

- Keep `src/main/java` as the source root and put every class in a lower-case package. Use the project root package `jason` and logical subpackages such as `jason.task` and `jason.command`.
- Use PascalCase nouns for class and enum names, camelCase verbs for methods, camelCase variables, and SCREAMING_SNAKE_CASE constants. Use English names and boolean names that read as booleans (`is...`, `has...`, `can...`). Test method names may use `featureUnderTest_testScenario_expectedBehavior()`.
- Use four spaces for indentation, K&R braces, explicit consistently ordered imports, and line lengths of at most 120 characters. Prefer shorter lines and wrap at readable boundaries.
- Initialize variables at declaration where practical, keep them in the smallest useful scope, use braces for every conditional and loop body, and place conditional bodies on separate lines.
- Keep class fields private unless they are constants. Prefer clear encapsulation through methods.
- Add descriptive Javadoc header comments to public classes and methods, including a short first-sentence summary. Document non-trivial private methods and intentional fall-through comments where applicable.
- Separate logical units with one blank line and keep comments in English using American spelling.

## Applying changes

When editing existing code, make the smallest behavior-preserving formatting or design change needed to comply. Check imports, package declarations, visibility, braces, line lengths, and public Javadocs. Run the project's tests after code changes.
