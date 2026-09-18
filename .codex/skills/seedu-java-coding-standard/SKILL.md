---
name: seedu-java-coding-standard
description: Follow the SE-EDU basic plus intermediate Java coding standard for Java code in this CS2103/T iP project.
metadata:
  short-description: SE-EDU Java coding standard
---

# SE-EDU Java Coding Standard

Use this skill whenever you create, edit, review, or explain Java code in this project. Follow the SE-EDU Java coding standard at
https://se-education.org/guides/conventions/java/intermediate.html. For Java topics not covered there, use the Google Java style guide as fallback.

## Project Fit

- Keep every production and test class in a package under the lower-case project root `sophon`.
- Prefer simple, course-friendly Java over clever or speculative abstractions.
- Preserve existing behavior unless the user asks for a behavior change.
- Keep code in `src`; do not move source files to a different layout.

## Naming

- Use lower-case package names.
- Name classes and enums with PascalCase nouns.
- Name methods with camelCase verbs.
- Name variables with camelCase.
- Name constants in `SCREAMING_SNAKE_CASE`.
- Do not capitalize whole acronyms inside identifiers, e.g. prefer `exportHtmlSource` over `exportHTMLSource`.
- Name booleans so they read like booleans, usually with prefixes such as `is`, `has`, `was`, `can`, or `should`.
- Use plural names for collections.
- Keep very short scratch names such as `i` only for small scopes.

## Layout

- Indent with 4 spaces and no tabs.
- Keep Java lines at or below 120 characters, aiming below 110 when practical.
- When wrapping, indent continuation lines by 8 spaces from the parent line and prefer readability over blind auto-formatting.
- Use K&R braces, with the opening brace on the same line.
- Separate logical units in a block with one blank line when it improves readability.
- Surround operators and ternary colons with spaces.
- Put a space after Java reserved words, commas, and semicolons in `for` statements.

## Statements

- Use explicit imports; do not use wildcard imports.
- Keep imports ordered consistently, with static imports first, then ordinary imports grouped logically.
- Attach array brackets to the type, e.g. `int[] values`.
- Declare variables in the smallest reasonable scope and initialize them where they are declared when possible.
- Do not expose public fields unless the class is a behavior-free data holder; constants are fine.
- Always use braces for loop and conditional bodies, including single-statement bodies.
- Put conditional bodies on separate lines.
- In `switch` statements, include an explicit `// Fallthrough` comment for intentional fallthrough.

## Comments

- Write comments in English, using American spelling and avoiding local slang.
- Add useful Javadoc/header comments for all classes and public methods, except simple getters/setters, exact inherited overrides, and test classes or test methods.
- Start method Javadocs with a short third-person summary such as `Returns ...`, `Saves ...`, or `Creates ...`.
- Leave no blank line between a Javadoc block and the declaration it documents.
- Use `@param`, `@return`, and `@throws` only when they add value; if one parameter is documented, document all parameters.
- End `@param`, `@return`, and `@throws` descriptions with punctuation.
- Keep comments aligned with the code they describe.
- Remove commented-out code instead of leaving it behind.
