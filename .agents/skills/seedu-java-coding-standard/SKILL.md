---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding standard to all Java code in this project.
---

# SE-EDU Java Coding Standard

Use this skill whenever you create, modify, review, or refactor Java code in this
repository. It is based on the [SE-EDU Java coding standard (basic +
intermediate)](https://se-education.org/guides/conventions/java/intermediate.html).
For topics not covered here, follow the [Google Java Style
Guide](https://google.github.io/styleguide/javaguide.html).

## Required conventions

- Put every class in a lowercase package named for the project and its logical
  component. Use nouns in `PascalCase` for classes and enums, `camelCase` for
  variables and verb-based methods, and `SCREAMING_SNAKE_CASE` for constants.
- Keep names in English. Do not uppercase abbreviations inside identifiers;
  for example, use `exportHtmlSource`, not `exportHTMLSource`.
- Give boolean variables and methods names that read as booleans, usually with
  `is`, `has`, `was`, `can`, or `should`. Use plural names for collections and
  longer names for values with wider scope. `i`, `j`, and similar short names
  are reserved for small-scope iterator variables, with `j`/`k` for nested
  loops.
- Use four spaces for indentation, K&R braces, and a hard line limit of 120
  characters (prefer fewer than 110). Wrap continuation lines with an extra
  eight spaces and break at readable boundaries, usually after commas or before
  operators. Keep method/constructor names attached to their opening `(`.
- Put spaces around operators, after commas, and after Java reserved words
  before `(`. Separate logical units in a block with one blank line.
- Use braces for every `if`, `else`, `for`, `while`, `do`, and `switch` body,
  including one-statement bodies. Keep `else` on the closing-brace line. Mark
  intentional switch fall-through with `// Fallthrough`.
- Order imports consistently, list imported classes explicitly, and never use
  wildcard imports. In this project, keep static imports first, followed by
  standard-library imports, third-party imports, and project imports, with
  blank lines between groups.
- Attach array brackets to the type (`String[] values`). Initialize variables
  at declaration when a valid value is available and keep declarations in the
  smallest possible scope. Do not expose class variables publicly, except for
  constants or behavior-free data classes.
- Write comments in English with American spelling and no local slang. Add a
  descriptive Javadoc header to every class and public method, except getters,
  setters, correctly inherited overridden-method documentation, and test
  classes/methods. Start Javadoc with a short summary, then add a blank line
  before `@param`/`@return`/`@throws` tags when tags are needed; punctuate tag
  descriptions.

## Before finishing a Java change

Review every changed Java file against the conventions above. In particular,
check names, import groups, braces, line lengths, array declarations, public
fields, and public API/class Javadocs. Run the project’s Java 25 build/tests when
available, and do not change behavior merely to make formatting compliant.
