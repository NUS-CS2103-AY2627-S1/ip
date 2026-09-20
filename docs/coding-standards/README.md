# Coding Standards for This Project

This document records the NUS CS2103/T standards applied to this project and
required for all future changes.

## Authority

The course requires the basic and intermediate rules from the
[SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html).
The course also recommends the
[Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
for topics not covered by the SE-EDU standard. Advanced SE-EDU rules are
optional according to the course's
[Standards/Conventions page](https://nus-cs2103-ay2627-s1.github.io/website/admin/standardsAndConventions.html).

## Java rules to apply to future changes

### Naming

- Use lower-case package names. Do not use `edu.nus.comp` as the project root
  package name.
- Name classes and enums as nouns in `PascalCase`.
- Name variables and methods in `camelCase`; method names should be verbs.
- Name constants in `SCREAMING_SNAKE_CASE`.
- Keep abbreviations and acronyms in normal mixed case, such as `parseUrl`
  rather than `parseURL`.
- Use English and American spelling in names and comments.
- Use plural names for collections and boolean names that read like booleans,
  such as `isDone` or `hasData`.

### Layout and statements

- Use four spaces for indentation, never tabs.
- Keep lines at or below 120 characters, with a soft target below 110.
- Use K&R braces and wrap long continuations with appropriate indentation.
- Separate logical units in a block with one blank line.
- Use explicit imports; do not use wildcard imports.
- Attach array brackets to the type, such as `int[] values`.
- Initialize variables where they are declared and keep them in the smallest
  practical scope.
- Keep class fields non-public to preserve encapsulation, except for constants
  where appropriate.
- Always use braces for loops and conditionals, including single-statement
  bodies.
- Put single-statement conditional bodies on separate lines inside braces.
- Surround operators, commas, and relevant colons with appropriate whitespace.

### Documentation

- Write comments in English, using American spelling and avoiding local slang.
- Add descriptive Javadoc comments to all classes and public methods, except
  the standard exceptions for getters/setters, exact overrides, and test code.
- Start a Javadoc summary with a concise verb such as `Returns`, `Adds`, or
  `Sends`.
- Use `@param`, `@return`, and `@throws` tags when they clarify the API, and
  punctuate parameter descriptions.
- Keep comments focused on what the code is intended to do and why; the code
  itself should make the implementation steps clear.

## Git conventions

For future suggested commit messages, follow the course-linked
[SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html):

- Use an imperative subject line, capitalize its first letter, omit the final
  period, and keep it within 72 characters when possible.
- Use a body for non-trivial changes, separated from the subject by a blank
  line and wrapped at 72 characters.
- Explain what changed and why; the diff shows how.
- Use meaningful kebab-case branch names.

The reusable project skill at
`.codex/skills/seedu-git-standard/SKILL.md` provides the checklist for all
future commits and branches.

## Applying this document

These standards apply to all Java code in this project. The current source is
organized under the lower-case `duchess` package, and future code must preserve
that package structure. The reusable project skill at
`.codex/skills/seedu-java-coding-standard/SKILL.md` provides the operational
checklist for agents making Java changes.
