---
name: seedu-java-coding-standard
description: Apply the SE-EDU Java basic and intermediate coding standard to Java code in this project, especially for package structure, naming, layout, imports, conditionals, loops, and Javadocs.
---

# SE-EDU Java Coding Standard

Follow the SE-EDU Java coding standard, basic plus intermediate rules:
https://se-education.org/guides/conventions/java/intermediate.html

Use the Google Java Style Guide for topics not covered by the SE-EDU standard.

## Required Rules

- Put every class in a package. Keep `src/main/java` and `src/test/java` as source roots; do not turn `src`, `main`, or `java` into packages.
- Use lowercase package names based on this project name, such as `dukey`, `dukey.task`, `dukey.command`, `dukey.parser`, `dukey.storage`, `dukey.ui`, and `dukey.exception`.
- Use PascalCase for classes and enums, camelCase for variables and methods, and SCREAMING_SNAKE_CASE for constants.
- Name boolean variables and boolean methods so they sound boolean, using prefixes such as `is`, `has`, `was`, `can`, or `should` where natural.
- Use plural names for collections.
- Keep imports explicit. Do not use wildcard imports.
- Keep import ordering consistent: static imports first, then standard library imports, then third-party imports, then project imports.
- Use 4 spaces for indentation and K&R braces.
- Keep Java lines at or below 120 characters, with a soft target of 110 characters.
- Surround operators with spaces, put one space after commas, and put one space after Java keywords such as `if`, `for`, `while`, and `catch`.
- Always use braces for loops and conditionals, even when the body has one statement.
- Declare variables in the smallest reasonable scope and initialize them where they are declared when possible.
- Write descriptive Javadoc header comments for public classes and public methods, except simple getters/setters, overriding methods whose inherited Javadoc applies, and test methods.

## Verification

Before finishing Java code changes, run:

```bash
./gradlew test
```

Also scan touched Java files for obvious standard violations such as default-package classes, wildcard imports, missing spaces before `{`, and lines longer than 120 characters.
