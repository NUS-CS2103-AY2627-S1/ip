---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding standard when creating, editing, formatting, or reviewing Java code in this repository.
---

# SE-EDU Java Coding Standard

Follow the authoritative standard at
https://se-education.org/guides/conventions/java/intermediate.html.

For every Java change:

- Use lowercase logical package names, PascalCase type names, camelCase method
  and variable names, and SCREAMING_SNAKE_CASE constants.
- Use 4-space indentation, K&R braces, and explicit braces for every loop and
  conditional body.
- Keep lines below 120 characters, preferably below 110. Indent wrapped lines
  by 8 spaces relative to their parent line and break before operators.
- Indent `case` labels one level inside their `switch` statement and include a
  `break`, arrow rule, or explicit fall-through comment.
- Use explicit imports in a consistent order; never use wildcard imports.
- Declare variables in the smallest practical scope and use boolean names that
  read as predicates, such as `isDone` or `hasTasks`.
- Write English Javadocs for public classes and methods, except self-evident
  getters/setters, exact overrides, and test APIs. Document non-obvious
  parameters, return values, and exceptions.
- Prefer self-explanatory code. Add comments only when they explain intent or
  constraints that the code itself does not make clear.

Before finishing, check line lengths and run the relevant Gradle tests.
