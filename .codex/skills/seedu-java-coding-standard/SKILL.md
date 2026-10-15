---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic-plus-intermediate Java coding standard to all Java code in this project.
---

# Seedu Java Coding Standard

Use this skill whenever you create, edit, review, or refactor Java production code or tests in this
repository. It is based on the [SE-EDU Java coding standard (basic + intermediate)](
https://se-education.org/guides/conventions/java/intermediate.html). For topics not covered here,
follow the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).

## Naming

- Use lowercase package names. Use the project or group name as the package root; do not use
  `edu.nus.comp.*` for student code.
- Name classes and enums with nouns in `PascalCase`; name methods with verbs in `camelCase`.
- Name variables in `camelCase`, constants in `SCREAMING_SNAKE_CASE`, and collections with plural
  names. Keep abbreviations and acronyms in normal mixed case, such as `openDvdPlayer()`.
- Use English consistently. Give booleans names that read as predicates (`isOpen`, `hasData`,
  `canEvaluate`) and use `setFound(boolean isFound)` for boolean setters.
- Test methods may use `featureUnderTest_testScenario_expectedBehavior()` naming.

## Layout and statements

- Use four spaces for indentation, never tabs. Keep lines at most 120 characters, aiming for less
  than 110. Wrap long lines at readable boundaries, preferably after commas or before operators,
  and indent wrapped lines by an additional eight spaces.
- Use K&R braces. Always wrap loop and conditional bodies in braces, including one-line bodies.
  Put a conditional body on its own line.
- Surround operators with spaces, put a space after commas and Java reserved words, and separate
  logical units in a block with one blank line.
- Put every class in a package and list imports explicitly. Keep import ordering consistent.
- Organize class declarations as documentation, declaration, static fields, instance fields,
  constructors, then methods. Put access modifiers first in a modifier list.
- Attach array brackets to the type (`int[] values`). Initialize variables at declaration when a
  valid initial value is available and keep variables in the smallest possible scope. Avoid
  unnecessary `this`; use it when a field is shadowed.
- In `switch` statements, do not indent `case` labels. Add an explicit `// Fallthrough` comment
  for intentional fall-through cases.

## Comments and documentation

- Write comments and Javadocs in English, using one consistent spelling convention and no local
  slang.
- Add descriptive Javadocs to public classes and public methods. This may be omitted for getters,
  setters, exact overrides whose parent documentation applies, and test classes or methods.
- Start a Javadoc block with `/**` on its own line, write a short first-sentence summary, leave a
  blank line before tags, punctuate parameter descriptions, and keep the block directly above its
  declaration. Document meaningful exceptions with `@throws`.

Before finishing a Java change, check the edited files against these rules and preserve behavior
unless the user explicitly requests a functional change.
