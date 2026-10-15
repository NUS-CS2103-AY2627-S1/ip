---
name: seedu-java-coding-standard
description: Apply the SE-EDU Java coding standard (basic and intermediate rules) when creating, editing, refactoring, or reviewing Java code in this project.
---

# SE-EDU Java Coding Standard

Use the authoritative [SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html) for every Java change or review in this repository. Apply Google Java Style only where the SE-EDU standard is silent.

## Review checklist

Check all touched Java code, not just newly added lines.

- Use lowercase package names, PascalCase noun class and enum names, camelCase verb method names, camelCase variables, and SCREAMING_SNAKE_CASE constants.
- Name booleans so they read as booleans, collections in the plural, and all identifiers in English.
- Indent with four spaces and never tabs. Keep lines below the 110-character soft limit and never exceed 120 characters.
- Use K&R braces. Always brace loop and conditional bodies, including single statements.
- Indent wrapped lines eight spaces beyond their parent. Break after commas and before operators where practical.
- Separate logical units with blank lines without adding decorative or excessive whitespace.
- Put every class in a package. Use explicit imports and keep their ordering consistent.
- Declare variables in the smallest useful scope and initialize them at declaration when a valid value is available.
- Keep mutable class variables non-public. Constants may be public when appropriate.
- Add `// Fallthrough` before every intentional switch fallthrough.
- Write comments in English using American spelling.

## Javadoc

This project strengthens the source standard's documentation rule:

- Add descriptive Javadoc headers to every non-private class, constructor, and method.
- Add descriptive Javadoc to non-trivial private methods.
- For an override whose inherited contract applies exactly, use `/** {@inheritDoc} */`.
- Start the summary with a third-person verb such as `Returns`, `Creates`, or `Executes`.
- Separate the summary from tags with a blank line. Document all parameters or none, and punctuate every `@param`, `@return`, and `@throws` description.
- Keep trivial private constructors, obvious private fields, and test methods undocumented unless a comment adds useful intent.

## Verification

Before finishing a Java change:

1. Inspect every touched Java file for the checklist above.
2. Run `git diff --check` and review the complete Java diff.
3. Run the project's Java 25 unit and UI tests required by `AGENTS.md`.
4. Report any deliberate exception to the standard instead of silently ignoring it.
