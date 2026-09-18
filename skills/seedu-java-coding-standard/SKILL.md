---
name: seedu-java-coding-standard
description: Apply the SE-EDU intermediate Java coding conventions to Java code in this project.
---

# Seedu Java Coding Standard

Use this skill for all Java implementation, refactoring, review, and test work in this project. The source standard is [SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html); use the Google Java Style Guide for topics not covered there.

## Required conventions

- Put every class in a lower-case package. Keep package names based on the project name and logical groups.
- Use PascalCase for classes and enums, camelCase for variables and methods, and SCREAMING_SNAKE_CASE for constants. Boolean names should read like booleans (`isDone`, `hasData`). Use plural names for collections.
- Use four-space indentation, K&R braces, and explicit imports. Do not use wildcard imports.
- Keep lines at or below 120 characters, with a soft target below 110. Wrap long expressions at readable boundaries and indent continuation lines by eight spaces.
- Initialize variables where they are declared and keep them in the smallest practical scope.
- Always use braces for loop and conditional bodies. Use an explicit `// Fallthrough` comment for intentional switch fall-through.
- Write comments and identifiers in clear English using American spelling. Add descriptive Javadocs to public classes and methods; document non-trivial private methods and fields when their purpose is not obvious.
- For test methods, descriptive names may use the three-part form `featureUnderTest_testScenario_expectedBehavior`.

## Review checklist

Before completing Java work, check package paths, imports, naming, line lengths, braces, variable scope, and Javadocs. Run the project's Java 25 build or tests when practical.
