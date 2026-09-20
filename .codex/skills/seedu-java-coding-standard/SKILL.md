---
name: seedu-java-coding-standard
description: Review and edit this project's Java code using the SE-EDU basic and intermediate coding conventions.
---

# Seedu Java Coding Standard

Apply this skill to every Java source change in this project, including production and test code. The source guide is [SE-EDU's Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html); use Google's Java style guide for topics that the source guide does not cover.

## Naming

- Put every class in a lowercase package. Name classes and enums as PascalCase nouns.
- Name variables in camelCase and methods as camelCase verbs.
- Name constants in SCREAMING_SNAKE_CASE. Keep abbreviations and acronyms lowercase when they form part of a name.
- Use English names, descriptive names for large-scope variables, and short names only for small-scope scratch variables or loop indices.
- Name booleans so they read as boolean questions, such as `isDone` or `hasTasks`; use `setX(boolean)` for boolean setters.
- Use plural names for collections. In tests, underscores are allowed in the form `featureUnderTest_testScenario_expectedBehavior`.

## Layout and statements

- Use four spaces for indentation and K&R braces.
- Keep lines at or below 120 characters, preferably below 110. Wrap at readable boundaries with eight additional spaces for continuation lines.
- Keep method and constructor names attached to their opening parenthesis.
- Separate logical units in a block with one blank line.
- Use braces for every `if`, `else`, `for`, `while`, and `do-while` body, including one-statement bodies. Use the documented switch and try-catch layouts; mark intentional fall-through with `// Fallthrough`.
- Order imports consistently, list imported classes explicitly, and remove unused imports. Put array brackets on the type (`String[] args`).
- Initialize variables at declaration when practical and keep them in the smallest possible scope. Do not expose mutable class variables publicly.

## Comments

- Write comments in English using American spelling and no local slang.
- Add descriptive header comments to every public class and public method. Getters, setters, and overriding methods may omit a comment only when the inherited documentation applies exactly; otherwise use `@inheritDoc` or document the difference.
- Format JavaDoc with `/**` on its own line, a concise first sentence beginning with a verb such as `Returns` or `Adds`, a blank line before tags, and punctuation in tag descriptions.
- Keep comments focused on what the code is intended to do rather than restating obvious implementation details.

## Review workflow

Before handing off a Java change, inspect the changed files for these rules, preserve existing behavior unless the request requires otherwise, and run the relevant build and tests. If a rule conflicts with an explicit user requirement, follow the user requirement and explain the exception.
