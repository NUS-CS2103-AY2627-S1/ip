---
name: seedu-java-coding-standard
description: Apply the required SE-EDU basic and intermediate Java coding rules when editing or reviewing Java in this repository.
---

# SE-EDU Java Coding Standard

Follow the [SE-EDU basic and intermediate Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html).
Use the Google Java Style Guide only for topics the SE-EDU standard does not cover.

## Required checks

- Use lowercase package names, PascalCase class and enum names, camelCase variables and verb-based methods, and SCREAMING_SNAKE_CASE constants.
- Give large-scope variables descriptive names, boolean names a boolean prefix, and collections plural names.
- Indent with 4 spaces, use K&R braces, and keep lines below 110 characters where practical and never above 120 characters.
- Wrap lines at readable high-level breaks with 8 additional spaces.
- Always use braces around loop and conditional bodies and keep the condition on its own line.
- List imports explicitly and keep their ordering consistent.
- Declare variables in the smallest useful scope and initialize them where practical.
- Keep fields non-public unless they are constants or belong to a behavior-free data class.
- Write English Javadoc header comments for public classes and methods, except obvious getters, exact overrides, and test code.
- Write non-trivial private method comments when their contract is not clear from the implementation.

Before completing a Java change, inspect all changed lines for these rules and run the project checks with Java 25.
