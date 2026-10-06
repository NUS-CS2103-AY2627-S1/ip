---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding conventions to Java code in this project.
metadata:
  short-description: Enforce the project Java coding standard
---

# SE-EDU Java coding standard

Use this skill for every Java source or test change in this project. The
canonical reference is the [SE-EDU Java coding standard (basic +
intermediate)](https://se-education.org/guides/conventions/java/intermediate.html).
Use the linked Google Java Style Guide for topics that the SE-EDU reference
does not cover.

## Mandatory rules

- Keep package names lowercase; use PascalCase for class and enum names,
  camelCase for variables and methods, and SCREAMING_SNAKE_CASE for constants.
- Use names that are clear, English, and appropriately descriptive. Boolean
  names should read like boolean values, and collection names should be plural.
  Keep abbreviations lowercase within names (for example, `openDvdPlayer`).
- Use four spaces for indentation, K&R braces, braces around every loop and
  conditional body, and spaces around operators, commas, and Java keywords.
- Keep lines at or below 120 characters (prefer below 110). For wrapped lines,
  indent continuation lines by eight spaces beyond the parent line and break at
  readable boundaries such as commas or before operators.
- Keep imports explicit and consistently ordered. Initialize variables at the
  narrowest practical scope, avoid public mutable class fields, and separate
  logical units in a block with one blank line.
- Add descriptive English Javadoc header comments to every class and every
  public or otherwise non-private method in this project. Getters/setters and
  exact overrides may use inherited documentation when appropriate; document
  non-trivial private methods. Start each summary with an action such as
  `Returns`, `Creates`, `Adds`, or `Converts`, use American spelling, and put
  `@param`/`@return`/`@throws` descriptions in a separate paragraph when they
  add useful information.
- Put comments on their own line, aligned with the code, and document
  intentional switch fallthrough with `// Fallthrough`.

## Applying the standard

Prefer the smallest change that makes the code compliant without changing
behavior. Apply naming changes consistently to package declarations, source
directories, imports, configuration, and tests. Run the relevant Gradle tests
after code changes using Java 25, and inspect the final diff for line-length,
naming, import-order, brace, and documentation violations.
