---
name: seedu-java-coding-standard
description: Apply and review this project's Java code against the SE-EDU basic and intermediate Java coding standard. Use whenever creating, editing, or reviewing Java code in this repository.
---

# SE-EDU Java Coding Standard

Follow the [SE-EDU basic and intermediate Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html). Use the Google Java Style Guide only for topics the SE-EDU standard does not cover.

When writing or reviewing Java code:

- Use English names; lowercase packages; PascalCase noun class and enum names; camelCase verb method names and variables; SCREAMING_SNAKE_CASE constants; and boolean names that read as booleans.
- Use plural names for collections and keep variable names proportional to their scope.
- Indent with 4 spaces, use K&R braces, target at most 110 characters per line, and never exceed 120 characters. Indent wrapped continuations 8 spaces beyond their parent line and break after commas or before operators.
- Use consistent, explicit, minimal imports. Put every class in a package and attach array brackets to the type.
- Initialize variables at declaration when a valid value is available, declare them in the smallest useful scope, and preserve encapsulation for class fields.
- Always use braces and separate lines for loop and conditional bodies. Format `switch` and `try` statements as shown by the standard and mark intentional fallthrough explicitly.
- Use conventional whitespace and blank lines to separate logical units.
- Write comments in English with American spelling, indent them with their code, and follow the standard's Javadoc format when documentation is in scope. Do not add documentation that the task explicitly assigns to a separate increment.

Preserve behavior during style-only work unless a specific standard violation requires a behavior change. Run the project's required tests and whitespace checks after edits.
