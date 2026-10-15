---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding conventions to code in this project.
---

# SE-EDU Java Coding Standard

Use this skill for all Java code changes in this project. Follow the [SE-EDU Java coding
standard](https://se-education.org/guides/conventions/java/intermediate.html); use the Google
Java Style Guide for topics the SE-EDU guide does not cover.

- Put every class in a logically named, all-lowercase package, with the source path matching it.
- Use PascalCase nouns for classes and enums, camelCase verbs for methods, camelCase variables,
  and SCREAMING_SNAKE_CASE constants. Keep abbreviations and acronyms in mixed case (`Html`,
  not `HTML`). Use plural names for collections and readable boolean names with prefixes such as
  `is`, `has`, or `was`; boolean setters take the form `setFound(boolean isFound)`. Test methods may
  use `featureUnderTest_testScenario_expectedBehavior`.
- Use four-space indentation, K&R braces, consistent explicit imports, and a maximum line length
  of 120 characters. Prefer shorter lines and wrap at readable boundaries, breaking after commas
  or before operators, keeping method names attached to `(`, and indenting wrapped lines by eight
  spaces beyond the parent line. Keep operators, reserved words, commas, and relevant colons
  separated by whitespace.
- Use array specifiers on the type (`int[] values`), initialize variables at declaration when
  practical, and keep them in the smallest useful scope. Short iterator names such as `i`, `j`,
  and `k` are for small iterator scopes, with `j` and `k` reserved for nested loops. Keep fields
  encapsulated; do not expose mutable class variables publicly. Give associated constants a common
  prefix.
- Always use braces for loops and conditionals. Keep `else` on the same line as the preceding
  closing brace, and make intentional switch fallthrough explicit with `// Fallthrough`.
- Write all names and comments in English, using American spelling and avoiding local slang. Add
  descriptive Javadocs to public classes and public methods, except getters/setters, overriding
  methods whose inherited documentation applies, and test code. Begin method summaries with an
  action such as “Returns” or “Adds”; use the required Javadoc spacing, punctuation, and tag order.
- Always put conditional and loop bodies in braces, including single-statement bodies. Separate
  logical units with one blank line and place comments at the indentation level of the code they
  describe.
