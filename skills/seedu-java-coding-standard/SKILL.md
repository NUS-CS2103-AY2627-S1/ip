---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding standard when writing, reviewing, or refactoring Java code in this project.
---

# SE-EDU Java Coding Standard

Use this skill for every Java change in this repository, including tests. The authoritative source is the [SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html). For topics not covered there, follow the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).

## Required rules

- Use lowercase package names; use English names, PascalCase nouns for classes/enums, camelCase for variables and verb methods, and SCREAMING_SNAKE_CASE for constants. Boolean names should read like predicates (`isDone`, `hasTasks`). Collection names should be plural. Test methods may use `featureUnderTest_testScenario_expectedBehavior`.
- Use four spaces, K&R braces, spaces around operators and after commas, one logical unit per blank-line-separated block, and a maximum line length of 120 characters (prefer under 110). Wrap continuation lines with eight additional spaces and break at readable boundaries.
- Keep imports explicit, minimal, and consistently ordered. Put every class in a package. Attach array brackets to the type (`int[] values`).
- Initialize variables at declaration when possible and keep them in the smallest scope. Do not expose mutable class fields publicly; use methods for access. Use `final` for values that do not change.
- Always use braces for loops and conditionals, including single-statement bodies. Keep `else` on the closing-brace line. Mark intentional switch fallthrough with `// Fallthrough`.
- Add descriptive English/American-English Javadoc to every class and public method. Getters/setters, test code, and overriding methods may omit it when the inherited documentation applies exactly. Javadoc summaries should start with forms such as “Returns”, “Adds”, or “Sends”; include a blank line before tags and punctuation in tag descriptions.

## Workflow

1. Inspect the whole affected Java file, including imports, fields, and public API.
2. Apply the simplest style-preserving change; do not alter behavior merely to format code.
3. Check names, visibility, initialization scope, braces, imports, line lengths, and Javadoc.
4. Run the project's tests/build with Java 25 and inspect the diff for accidental behavior changes.
