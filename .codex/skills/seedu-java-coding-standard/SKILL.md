---
name: seedu-java-coding-standard
description: Apply the SE-EDU intermediate Java coding standard when writing or reviewing Java production or test code in this project.
---

# SE-EDU Java Coding Standard

Use this skill for every Java code addition, modification, or review in this repository. Apply the intermediate rules from [SE-EDU's Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html). For subjects it does not cover, use the Google Java Style Guide.

## Required checks

- Use English names and comments. Use PascalCase for types, camelCase verb names for methods, camelCase variable names, and `UPPER_SNAKE_CASE` for constants.
- Name booleans so they read as booleans (`is`, `has`, `can`, `should`, or `was`), and use plural names for collections.
- Use explicit imports; never use wildcard imports. Keep import ordering consistent in a file.
- Indent with four spaces, use K&R braces, keep lines at or below 120 characters, and wrap long expressions at readable higher-level boundaries.
- Put braces around every loop and conditional body, including single statements. Separate logical units with a blank line.
- Declare variables in the smallest practical scope and initialize them at declaration when a valid initial value exists.
- Do not expose mutable public fields. Constants are the exception.
- Write Javadoc for public classes and public methods unless the SE-EDU exceptions apply (self-explanatory getters/setters, exact overrides, or test code). Use a summary sentence, meaningful tags, and American English.
- Test methods may use `featureUnderTest_testScenario_expectedBehavior` names with underscores.

Before handing off Java changes, inspect the changed files for these rules and run the project-required verification steps in `AGENTS.md`.
