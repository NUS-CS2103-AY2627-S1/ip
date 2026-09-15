---
name: seedu-java-coding-standard
description: Apply and review Java code against the SE-EDU basic and intermediate coding standard for this CS2103 project. Use whenever creating, editing, formatting, or reviewing Java source or tests.
---

# SE-EDU Java Coding Standard

Use the current [SE-EDU basic and intermediate Java standard](https://se-education.org/guides/conventions/java/intermediate.html)
as the source of truth. For topics it does not cover, follow Google Java
Style unless an official course requirement says otherwise.

## Required review

- Use lowercase package names, PascalCase nouns for types, camelCase verbs for
  methods, camelCase variables, and SCREAMING_SNAKE_CASE constants. Treat an
  acronym within a Java identifier as a word, such as `Choo` or `Ui`.
- Name booleans so they read as conditions, preferably with `is`, `has`,
  `was`, `can`, or `should`. Use plural names for collections.
- Indent with four spaces, use K&R braces, always brace loop and conditional
  bodies, and keep lines at or below 120 characters (prefer 110).
- Wrap at readable high-level boundaries: after commas and before operators.
  Indent continuation lines by eight spaces relative to the parent line.
- Keep imports explicit, minimal, and consistently grouped in this order:
  static imports, `java`/`javax`, third-party, then project imports. Separate
  non-empty groups with one blank line and sort within each group.
- Declare variables in the smallest useful scope and initialize them where
  declared when a valid initial value exists. Do not expose mutable fields.
- Separate logical units with blank lines. Write comments in English using
  American spelling; explain intent rather than restating code.
- Add descriptive Javadocs to public production classes and methods, except
  self-evident getters/setters or exact overrides. Start summaries with a
  third-person verb, document meaningful parameters/returns/exceptions, and
  punctuate tag descriptions. Test code is exempt from mandatory Javadocs.

Before finishing a Java change, inspect every changed line for these rules,
run `git diff --check`, compile with Java 25, and run the relevant Gradle and
project UI tests. Preserve behavior unless the user requested a behavior
change.
