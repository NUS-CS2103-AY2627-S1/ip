---
name: seedu-java-coding-standard
description: Follow the SE-EDU Java coding standard for this iP project.
---

# SE-EDU Java Coding Standard

Use this skill whenever Java code in this project is created, edited, reviewed, or explained. The project source root is `src/main/java`; do not treat `src`, `main`, or `java` as Java packages.

## Naming

- Use lowercase package names. The root package should identify the project, such as `larper`.
- Use `PascalCase` nouns for classes and interfaces.
- Use `camelCase` verbs or verb phrases for methods.
- Use `camelCase` nouns for variables and parameters.
- Use `SCREAMING_SNAKE_CASE` for constants.
- Name booleans with forms such as `is`, `has`, `can`, `should`, or `was`.
- Use English names and avoid abbreviations unless they are widely understood.

## Layout And Formatting

- Indent with 4 spaces. Do not use tabs.
- Keep lines within 120 characters.
- Use K&R braces: opening braces stay on the same line for classes, methods, conditionals, loops, and `try` blocks.
- Always use braces for `if`, `else`, `for`, `while`, and `do-while` bodies.
- Put spaces around binary operators and after commas.
- Use blank lines to separate package/import declarations, fields, constructors, methods, and logical blocks.
- Attach array brackets to the type, e.g. `String[] args`.

## Imports And Packages

- Every production class must be in an explicit package under `src/main/java`.
- Use explicit imports only. Do not use wildcard imports.
- Keep imports ordered consistently: Java standard library imports first, then project imports.

## Code Structure

- Declare variables in the smallest practical scope, close to first use.
- Avoid public mutable fields. Use private fields with methods unless a constant is being exposed.
- Keep classes focused on one responsibility.
- Prefer simple, readable code over unnecessary abstractions.

## Comments

- Write comments in English, use American spelling, and avoid local slang.
- Add descriptive Javadocs for all public production classes and public production methods.
- Javadocs may be omitted for getters/setters, test classes/methods, and overridden methods when the inherited documentation applies exactly.
- Start method Javadocs with a short behavior summary such as `Returns ...`, `Adds ...`, `Creates ...`, or `Shows ...`.
- Include `@param`, `@return`, and `@throws` tags only when they add useful information, and punctuate each description.

## Verification

- After Java code changes, update JUnit tests so the project continues to cover the top ~50% highest-value methods.
- After console behavior changes, update `test/ui-test-plan.md`.
- After every code update, run Gradle/JUnit tests and invoke the project-specific `$test-ui` skill.

Source standard: https://se-education.org/guides/conventions/java/intermediate.html
