---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding standard to all Java code in this project.
---

# Seedu Java Coding Standard

Use this skill whenever you create, modify, review, or test Java code in this repository. It is based on the [SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html); use that page and the Google Java Style Guide for topics not covered here.

## Required conventions

- Put every class in a lowercase, project-rooted package. Use PascalCase nouns for classes and enums, camelCase verbs for methods, camelCase variables, and `SCREAMING_SNAKE_CASE` constants. Keep acronyms in names mixed case (for example, `exportHtmlSource`). Use English and boolean-sounding names such as `isOpen` or `hasData`; use plural names for collections.
- Use four spaces for indentation, K&R braces, and braces around every conditional and loop body. Keep lines at or below 120 characters, preferably below 110; wrapped lines are indented by eight spaces relative to their parent. Put spaces around operators, after commas, and after Java reserved words such as `if` and `for`. Separate logical units with one blank line.
- Order imports consistently, list imported classes explicitly, and remove unused imports. Attach array brackets to the type (`String[] values`). Initialize variables when declared when a valid value is available, and keep declarations in the smallest practical scope. Do not expose mutable class fields publicly; use methods for access.
- Add descriptive English Javadoc to every public class and public method, except getters/setters, exact overrides whose parent documentation applies, and test code. Start summaries with an action such as `Returns`, `Adds`, or `Creates`; include useful `@param`, `@return`, and `@throws` tags, with punctuation and no blank line between the Javadoc and declaration.
- Make intentional `switch` fall-through explicit with a `// Fallthrough` comment. Keep comments indented with the code and use American spelling.

## Review workflow

When reviewing changes, inspect both production and test Java files for the conventions above. Prefer the smallest behavior-preserving cleanup; do not change user-visible behavior merely to reformat code. If a required cleanup changes behavior, update the UI test plan and user guide as part of the same change, then run the project-specific `test-ui` skill.
