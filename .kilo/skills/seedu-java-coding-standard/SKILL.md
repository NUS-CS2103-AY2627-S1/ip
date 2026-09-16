---
name: seedu-java-coding-standard
description: SE-EDU intermediate Java coding standard rules that must be followed for all Java code in this project.
---

# SE-EDU Java Coding Standard (Intermediate)

All Java code in this project must follow the SE-EDU intermediate coding standard. Use Google Java Style Guide for topics not covered here.

## Naming

- Packages: all lower case (e.g. `bags.ui`, `bags.task`). Root package should be the group/project name, not `edu.nus.*`.
- Classes/enums: nouns in PascalCase.
- Variables/methods: camelCase verbs.
- Constants: SCREAMING_SNAKE_CASE.
- Acronyms in names: treat as words, not all-caps (e.g. `openDvdPlayer`, not `openDVDPlayer`).
- Booleans: prefix with `is`, `has`, `was`, `can`, `should` (e.g. `isDone`, `hasTasks`).
- Collections: use plural names (e.g. `tasks`, `records`).
- Iterators: `i`, `j`, `k` for nested loops.
- Scope-based length: long names for large scope, short names for small/scratch scope.

## Layout

- Indentation: 4 spaces (no tabs).
- Line length: soft limit 110, hard limit 120. Wrap at commas, before operators, after open parenthesis.
- Wrapped line indentation: 8 spaces from parent line.
- Brackets: K&R/Egyptian style (opening brace on same line).
- Method definitions: `public void someMethod() throws Exception {`
- Blank lines: separate logical units within a block.
- Array specifiers: attached to type, not variable (`String[] args`, not `String args[]`).

## Statements

- **Package/Import**: every class in a package; imports ordered consistently (java -> third-party -> project); imported classes listed explicitly (no wildcards).
- **Types**: array specifiers on type.
- **Variables**: initialize where declared; smallest scope possible; never public class variables (except constants/data classes).
- **Loops**: always use curly braces, even for single statements.
- **Conditionals**: condition on its own line; always use curly braces.

## Comments

- All comments in English (American spelling).
- Javadoc required for all public classes and public methods. May be omitted for: getters/setters, overridden methods with identical parent Javadoc, and test classes/methods.
- Javadoc format:
  - Opening `/**` on its own line.
  - First sentence: short summary starting with a verb (`Returns...`, `Adds...`, `Creates...`).
  - `*` aligned with opening; space after each `*`.
  - Empty line between description and `@param`/`@return`/`@throws`.
  - No blank line between comment block and method/class.
  - `@return` omitted if obvious or `void`.
  - `@param` for all parameters or none.
- Comments indented to match code position.
- Remove AI-generated/editor comments before committing.
