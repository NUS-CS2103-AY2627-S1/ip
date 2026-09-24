---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding conventions to Java code in this project.
---

# SE-EDU Java Coding Standard

Use this skill for every Java implementation or review in this repository. Follow the SE-EDU [Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html), which includes the basic and intermediate rules. Use the Google Java Style Guide for topics not covered there.

Before finishing Java changes, check:

- Package names are lowercase; classes and enums are PascalCase nouns; variables and methods use English camelCase; constants use `SCREAMING_SNAKE_CASE`.
- Boolean names read as booleans (`is`, `has`, `can`, `should`, or `was` where appropriate), and collections use plural names.
- Use four spaces, K&R braces, spaces around operators and after commas, one logical unit per blank-line group, and lines no longer than 120 characters (prefer under 110).
- Put every class in a package and use explicit, consistently ordered imports. Attach array brackets to the type.
- Initialize variables at declaration when practical, keep them in the smallest scope, and use braces for every loop and conditional body.
- Add descriptive English-American Javadoc to public classes and public methods, except getters/setters, exact overrides, and test code. Keep Javadoc formatting and punctuation consistent with the guide.
- Keep fields private unless a data class or constant requires otherwise.

Review the complete linked guide when a rule is ambiguous, and preserve behavior while correcting style.
