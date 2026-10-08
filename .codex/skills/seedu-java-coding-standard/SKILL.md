---
name: seedu-java-coding-standard
description: Follow the SE-EDU intermediate Java coding standard for Java and JUnit code in this repository, and use the Google Java Style Guide only for topics the SE-EDU guide does not cover.
---

# SE-EDU Java Coding Standard

Use this skill whenever you create or edit Java source or JUnit tests in this repository.

The source of truth is the SE-EDU Java intermediate coding standard:
[Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html)

## Scope

Apply these rules to code under `src/main/java` and `src/test/java`.

For topics not covered by the SE-EDU guide, follow the Google Java Style Guide as the fallback, matching the guidance given on the SE-EDU page.

## Project-specific expectations

- Keep every class in a package rooted at `rudra`.
- Use package names in lowercase and group classes by responsibility.
- Keep class and enum names in PascalCase.
- Keep method and variable names in camelCase.
- Keep constant names in SCREAMING_SNAKE_CASE.
- Use boolean names that read like booleans, such as `isExit`, `hasTime`, or `wasDone`.
- Use plural names for collections such as `tasks` and `fields`.

## Layout and formatting

- Use 4-space indentation and K&R braces.
- Keep lines within the 120-character hard limit and prefer staying below 110 when practical.
- When wrapping, break at readable higher-level boundaries and indent wrapped lines consistently.
- Keep import ordering consistent.
- Separate logical units in methods with a blank line when it improves readability.

## Comments and documentation

- Use JavaDoc for non-private classes and methods, and for non-trivial private methods when the intent is not obvious.
- Keep comments focused on the code’s purpose or rationale, not on how the code was generated.
- Remove or rewrite meta comments about tool usage when touching a file.

## Tests

- Follow the same coding standard in JUnit tests.
- Test method names may use underscores in the `featureUnderTest_testScenario_expectedBehavior` style when that improves clarity.

## Working style

- Prefer the smallest code change that brings the touched code into compliance.
- If a file already violates the standard in a few obvious places and you are editing that file, clean up the nearby violations when it is safe to do so.
