---
name: seedu-java-coding-standard
description: Apply the SE-EDU Java coding standard for this project when writing, reviewing, or editing Java source or test code.
---

# SE-EDU Java Coding Standard

Use this skill for all Java code changes in this repository.

Follow the SE-EDU Java coding standard, basic + intermediate version:
https://se-education.org/guides/conventions/java/intermediate.html

## Required Checks

- Put every class in a package under the `friday` root package.
- Keep package names lowercase and class names in PascalCase.
- Use camelCase for variables and methods.
- Use SCREAMING_SNAKE_CASE for constants.
- Use explicit imports only; do not use wildcard imports.
- Keep import ordering consistent: static imports first when present, then project imports, Java library
  imports, and third-party imports, with blank lines between groups.
- Use 4 spaces for indentation; do not use tabs.
- Keep lines at or below 120 characters, and prefer shorter wrapped lines where practical.
- Use K&R braces: opening braces stay on the same line as the declaration or statement.
- Always use braces for loop and conditional bodies, even for one-line bodies.
- Put conditionals on separate lines; do not write one-line `if` statements.
- Surround operators and binary/ternary colons with spaces.
- Follow normal spacing after reserved words, commas, and semicolons in `for` statements.
- Declare variables in the smallest practical scope and initialize them where they are declared.
- Keep class variables non-public unless they are constants or true data-class fields.
- Write comments in English, use American spelling, and avoid local slang.
- Write descriptive Javadocs for all public classes and public methods, except simple getters/setters,
  test code, or overrides where the inherited Javadoc applies exactly.
- Do not add comments that merely restate obvious code.

## Validation

Before finishing Java edits, run relevant tests or compilation. Also check:

```bash
git diff --check
awk 'length($0) > 120 { print FILENAME ":" FNR ":" length($0) }' $(find src/main/java src/test/java -name '*.java' | sort)
```
