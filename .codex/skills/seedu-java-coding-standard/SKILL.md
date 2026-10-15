---
name: seedu-java-coding-standard
description: Apply the SE-EDU intermediate Java coding standard when creating, editing, or reviewing Java code in this project.
---

# SE-EDU Java Coding Standard

Apply this skill to every Java source or test change in this repository. Follow
the [SE-EDU Java coding standard (basic + intermediate)](https://se-education.org/guides/conventions/java/intermediate.html).
For subjects that it does not cover, use the Google Java Style Guide.

## Names and documentation

- Keep package names lowercase; use English nouns in PascalCase for types and
  English camelCase verbs for methods.
- Use camelCase variables, SCREAMING_SNAKE_CASE constants, and plural names for
  collections. Keep local variables in the smallest useful scope.
- Name boolean variables and methods as predicates (`is`, `has`, `can`,
  `should`, or `was`); boolean setters use `setX(boolean isX)`.
- Write clear American-English Javadoc for public classes and public methods,
  except simple getters/setters, tests, and exact overrides. Start method
  summaries with a third-person verb such as “Returns” or “Adds”. Include
  useful `@param`, `@return`, and `@throws` tags with punctuation.

## Layout and statements

- Indent with four spaces, never tabs. Keep lines at 120 characters or fewer;
  aim for 110 or fewer. Wrap after commas and before operators, using an extra
  four-space continuation indent where appropriate.
- Use K&R braces and put every `if`, `else`, loop, and other control-statement
  body on its own braced block, even when it has one statement.
- Use one space around binary/ternary operators and after commas, and separate
  logical units with one blank line.
- Use explicit imports only. Keep imports minimal and in a consistent order;
  keep each static-import block separate from non-static imports.
- Attach array brackets to the type, initialize variables at declaration when a
  valid value is available, and do not expose mutable public fields.
- Mark intentional fall-through in a traditional `switch` with `// Fallthrough`.

## Before finishing

Review the changed Java files for these rules, including tests. Preserve the
application's behavior unless the request explicitly changes it.
