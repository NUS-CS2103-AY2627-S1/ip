---
name: seedu-java-coding-standard
description: Apply the SE-EDU intermediate Java coding standard when creating, modifying, or reviewing Java source in this project.
---

# Seedu Java Coding Standard

Apply this skill to every Java source-code change and Java coding-standard review in this repository.

Follow the [SE-EDU intermediate Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html). For topics it does not cover, follow the Google Java Style Guide as directed by that standard.

## Required checks

- Use lowercase package names, PascalCase noun names for classes, camelCase verb names for methods, camelCase variable names, and SCREAMING_SNAKE_CASE for constants.
- Name booleans so they read as booleans (`is`, `has`, `can`, `should`, or similar), and use plural names for collections.
- Use four-space indentation, K&R braces, braces for every loop and conditional body, and spaces around operators and after commas.
- Keep lines at 120 characters or fewer; prefer line breaks at readable, higher-level boundaries and indent continuations by eight spaces relative to the parent line.
- Keep imports explicit, minimal, and consistently grouped: static imports, Java imports, then project and third-party imports.
- Keep variables initialized at declaration and in the smallest practical scope. Do not expose mutable class state publicly.
- Write clear American-English comments. Use Javadoc for public classes and public methods unless the standard's getter, setter, override, or test exemptions apply. Include punctuation in Javadoc parameter descriptions.

## Workflow

Before completing a Java change, review the edited file and nearby declarations against these checks. Correct violations that are within the request's scope, without changing behavior solely for stylistic preference. Run the project's required checks after source edits.
