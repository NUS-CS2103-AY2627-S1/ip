---
name: seedu-java-coding-standard
description: "Apply the SE-EDUCATION basic and intermediate Java coding standard to Java source and tests in this project."
---

# Seedu Java Coding Standard

Use this skill for every Java change in this repository, including production code,
tests, examples, and build entry-point references. The authoritative source is the
[SE-EDUCATION Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html);
use the Google Java Style Guide for topics not covered there.

## Required rules

- Use lowercase package names. Use PascalCase nouns for classes and enums, camelCase
  for variables and verb-based methods, and SCREAMING_SNAKE_CASE for constants.
- Keep names in English. Do not uppercase abbreviations or acronyms inside names;
  for example, prefer `Am` and `openDvdPlayer` to `AM` and `openDVDPlayer`.
  Boolean names should read as predicates (`is`, `has`, `can`, `should`, or `was`).
  Collection names should be plural.
- Use four spaces for indentation, K&R braces, spaces around operators and after
  commas, and a blank line between logical units. Keep lines at or below 120
  characters, wrapping long lines with an eight-space continuation indent and
  breaking at readable boundaries.
- Put every class in a package and keep imports explicit and consistently ordered.
  Never use wildcard imports. Keep array brackets attached to the type (`int[]`).
- Initialize variables at declaration when a valid value is available and keep each
  declaration in the smallest necessary scope. Do not expose class fields publicly,
  except constants or intentionally behavior-free data classes.
- Always use braces for `if`, `else`, `for`, `while`, `do`, and `switch` bodies, even
  for one statement. Put conditional bodies on separate lines. Mark intentional
  switch fall-through with `// Fallthrough`.
- Write English, American-spelling comments. Add descriptive Javadoc to every class,
  public constructor, and public method, except getters/setters, test code, and
  overrides whose inherited documentation applies exactly. Javadocs need a concise
  first-sentence summary, aligned `*` lines, and `@param`, `@return`, and `@throws`
  tags when they add value.

## Completion checklist

Before finishing a Java change:

1. Inspect changed files for package/name/import/layout/control-flow violations.
2. Preserve behavior unless the user requests a behavioral change.
3. Run the project’s Java 25 build/tests and report any remaining issue.
