---
name: seedu-java-coding-standard
description: Apply the SE-EDU Java coding standard (basic and intermediate rules) when creating, editing, or reviewing Java code in this project.
---

# SE-EDU Java Coding Standard

Use the [SE-EDU Java coding standard (basic + intermediate)](https://se-education.org/guides/conventions/java/intermediate.html)
for all Java code in this project. For topics it does not cover, follow the
[Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).

## Apply the standard

- Use lower-case package names. Use PascalCase nouns for classes and enums,
  camelCase verbs for methods, camelCase for variables, and SCREAMING_SNAKE_CASE
  for constants. Keep acronyms lower-case within names and use English names.
- Name booleans so they read as booleans, preferably with `is`, `has`, or `was`.
  Use plural names for collections and short iterator names only for small scopes.
- Indent with four spaces and never tabs. Keep lines at or below 120 characters,
  aiming for 110; indent wrapped lines eight spaces beyond their parent. Break
  after commas and before operators, including `.`. Keep method names attached
  to their opening parenthesis.
- Use K&R braces. Always brace loop and conditional bodies and put their bodies
  on separate lines. Mark intentional switch fall-through with `// Fallthrough`.
- Put every class in a package. Keep imports consistently ordered, explicit,
  minimal, and free of wildcards. Attach array brackets to the type.
- Declare variables in the smallest useful scope and initialize them at
  declaration when a valid value is available. Keep behavior-bearing fields
  non-public; public constants are allowed.
- Separate logical units with one blank line and use consistent spaces around
  operators and after reserved words, commas, and `for` semicolons.
- Write comments in English with American spelling and indent them with the code.
  Add descriptive Javadoc to every class and public method except getters,
  setters, exact overrides, and test code. Start summaries with a third-person
  verb such as “Returns” or “Adds”; punctuate tag descriptions and either
  document all parameters or omit all self-explanatory parameters.

When reviewing or changing Java, inspect the full touched file rather than only
the edited lines. Preserve behavior unless the user asks for a behavioral change.
