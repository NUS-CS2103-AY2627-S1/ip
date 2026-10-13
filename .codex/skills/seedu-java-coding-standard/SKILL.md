---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding conventions to generated or modified Java code in this project.
metadata:
  short-description: Apply SE-EDU Java conventions
---

# SE-EDU Java coding standard

Use the [SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html) for every Java file generated or modified in this repository. Use the Google Java Style Guide for topics the SE-EDU standard does not cover.

Before handing off code, review the complete diff and correct these project-relevant rules:

- Use lowercase package names, PascalCase nouns for classes, camelCase verbs for methods and variables, and SCREAMING_SNAKE_CASE constants. Use English names; boolean names should read as predicates.
- Use four spaces (never tabs), K&R braces, spaces around operators and after commas, one logical unit per blank-line-separated block, and a hard line limit of 120 characters (prefer less than 110). Wrapped lines use an additional eight spaces of indentation.
- Keep imports explicit and consistently ordered. Do not use wildcard imports. Put array brackets on the type.
- Initialize variables at declaration when possible and keep them in the smallest scope. Keep non-constant fields private. Always brace loop and conditional bodies, including single-statement bodies; document intentional switch fallthrough with `// Fallthrough`.
- Add concise English Javadocs to every production class and public method, including constructors, except getters/setters and exact overrides whose inherited documentation applies. Include useful `@param`, `@return`, and `@throws` tags.

When generating or changing code, apply this skill before compiling or testing. Afterward, run the repository's normal Java build/tests and, if CLI behavior changed, follow the project's `test-ui` instructions.
