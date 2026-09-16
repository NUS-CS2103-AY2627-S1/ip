---
name: seedu-java-coding-standard
description: >-
  Apply the SE-EDU Java coding standard, basic plus intermediate, when writing,
  editing, or reviewing Java code in this project.
metadata:
  short-description: Follow SE-EDU Java style
---

# SE-EDU Java Coding Standard

Use this skill whenever creating, editing, or reviewing Java code in this
project. Follow the SE-EDU Java coding standard, basic plus intermediate, from:

https://se-education.org/guides/conventions/java/intermediate.html

For topics not covered by the SE-EDU standard, follow the Google Java Style Guide.

## Required Project Style

- Use lowercase package names rooted in the project name, then logical groups.
- Use PascalCase nouns for classes and enums.
- Use camelCase verbs for method names.
- Use camelCase for variables and SCREAMING_SNAKE_CASE for constants.
- Name booleans so they read as booleans, preferably with prefixes such as `is`,
  `has`, `was`, `can`, or `should`.
- Use plural names for collections.
- Keep variables in the smallest reasonable scope and initialize them where
  declared when a real initial value exists.
- Use explicit imports only; never use wildcard imports.
- Attach array brackets to the type, for example `String[] args`.
- Use 4 spaces for indentation and no tabs.
- Keep lines at or below 120 characters, with 110 characters as the soft target.
- Wrap lines for readability: break after commas, before operators, and keep
  method names attached to `(`.
- Use K&R braces, with opening braces on the same line as the construct.
- Always use braces for loop and conditional bodies, including single-statement
  bodies.
- Put conditionals on their own lines.
- Include `// Fallthrough` when a switch case intentionally falls through.
- Separate logical units in a block with a blank line.
- Write comments in English, use American spelling, and avoid local slang in code
  comments.
- Write descriptive Javadocs for public classes and public methods, unless an
  allowed exception applies: trivial getters/setters, exact inherited override
  docs, or test-only code.
- Format Javadocs with a short first sentence, aligned leading `*`, useful
  `@param`, `@return`, and `@throws` tags, and no blank line between the comment
  and declaration.

When changing existing code, prefer a focused style fix near touched code over
broad unrelated reformatting.
