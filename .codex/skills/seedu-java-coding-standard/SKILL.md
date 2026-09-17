---
name: seedu-java-coding-standard
description: Apply the SE-EDU intermediate Java coding conventions to production and test Java code in this project.
---

# SE-EDU Java Coding Standard

Use this skill for every Java code change, review, or refactor in this project.
The rules are based on the [SE-EDU intermediate Java coding conventions](https://se-education.org/guides/conventions/java/intermediate.html).

## Naming

- Use lowercase package names.
- Use PascalCase nouns for classes and enums.
- Use camelCase for methods and variables.
- Use SCREAMING_SNAKE_CASE for constants.
- Write identifiers in English. Treat acronyms as words, such as `exportHtmlSource`.
- Name boolean methods and variables with a predicate prefix such as `is`, `has`, or `can`.
- Use plural names for collections.
- Use `i`, `j`, and similar short names only for small iterator or nested-loop scopes.
- Give related constants a common prefix.

## Layout

- Use 4 spaces for indentation. Never use tabs.
- Keep lines below 110 characters where practical. Never exceed 120 characters.
- For wrapped lines, indent 8 spaces more than the parent line.
- Break after commas and before operators when wrapping. Keep a method or constructor name attached to its opening parenthesis.
- Use K&R braces. Put conditional bodies on separate lines.
- Separate logical units in a block with one blank line.
- Surround operators with spaces and put spaces after keywords, commas, and `for` semicolons.

## Statements and data

- Put every class in a package.
- Keep import ordering consistent and use explicit imports. Do not use wildcard imports.
- Attach array brackets to the type, such as `int[] values`.
- Declare variables in the smallest valid scope and initialize them at declaration when a valid value exists.
- Keep fields non-public, except constants and behavior-free data classes.
- Always use braces for loops and conditionals, including single-statement bodies.
- Use the documented forms for `if`, loops, `switch`, and `try`/`catch` blocks.
- Add `// Fallthrough` for intentional fallthrough in a traditional `switch` statement.

## Comments and Javadoc

- Write comments in English with American spelling.
- Add descriptive Javadoc to every public class and public method. Getters, setters, tests, and exact overrides may omit it.
- Start a Javadoc summary with a verb such as `Returns`, `Creates`, `Adds`, or `Displays`.
- Keep a blank line between the description and tags. End parameter and tag descriptions with punctuation.
- Include all useful `@param` tags, or none when the parameter names are fully self-explanatory.
- Use `{@inheritDoc}` when an override reuses and extends inherited documentation.

## Completion check

Before completing a Java change, inspect all changed Java files for these rules,
preserve behavior unless the user requests a behavior change, and run the
project's required JUnit and console UI checks.
