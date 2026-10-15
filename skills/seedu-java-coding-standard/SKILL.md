---
name: seedu-java-coding-standard
description: Review and edit Java source in this project according to the SE-EDU basic and intermediate coding standard. Use for all project Java code changes, reviews, and formatting work.
---

# SE-EDU Java coding standard

Apply these rules to every Java file in this project. The source standard is:
https://se-education.org/guides/conventions/java/intermediate.html

## Required conventions

- Put every class in a lower-case package.
- Use descriptive names: classes and methods in `UpperCamelCase`/`lowerCamelCase`, constants in `UPPER_SNAKE_CASE`, and boolean names that read naturally such as `isDone` or `hasData`.
- Use four spaces for indentation and K&R braces. Always use braces for loops and conditionals, including single-statement bodies.
- Keep lines at or below 120 characters where possible; prefer below 110. Wrap long expressions after commas or before operators, using eight spaces for continuation indentation.
- Keep import ordering consistent: static imports first, then `java`, `javax`, third-party, and project imports, with blank lines between groups. Use explicit imports, never wildcard imports.
- Initialize variables at declaration when practical and keep declarations in the smallest scope needed. Keep collection names plural and array brackets attached to the type.
- Encapsulate mutable state; public fields are only appropriate for behavior-free data classes or constants.
- Separate logical units in a block with one blank line.
- Add English, American-spelling Javadocs to public classes and public methods. Include a concise first sentence and useful `@param`, `@return`, and `@throws` tags. Getters/setters, overrides whose inherited documentation applies exactly, and test methods may omit them.
- Keep comments indented with the code and use comments to explain intent rather than restating syntax.
- For intentional switch fall-through, add an explicit `// Fallthrough` comment.

## Workflow

When changing Java code, inspect the affected files for violations, make the smallest coherent cleanup, and run the project’s Java 25 build or tests when possible. Do not change behavior merely to satisfy formatting; call out any rule that cannot be applied without a design change.
