---
name: seedu-java-coding-standard
description: Apply the SE-EDU intermediate Java coding conventions to all Java code in this project.
---

# SE-EDU Java coding standard

Apply this skill to every Java source and test file in this repository. Use the
[SE-EDU intermediate Java conventions](https://se-education.org/guides/conventions/java/intermediate.html)
as the source of truth when a rule is not summarized below.

## Naming

- Use `UpperCamelCase` for classes and interfaces and `lowerCamelCase` for methods and variables.
- Use `UPPER_SNAKE_CASE` for constants. Give related constants a shared prefix.
- Name booleans with a question-like prefix such as `is`, `has`, `can`, `should`, or `was`; use
  the same form in boolean setters.
- Use plural names for collections. Reserve `i`, `j`, and `k` for iterators and nested loops.

## Layout and statements

- Use four spaces for indentation, never tabs. Keep lines at most 120 characters, aiming for
  110 or fewer; wrapped lines use an additional eight spaces of indentation.
- Use K&R braces. Put the method or constructor name next to its opening parenthesis and break
  long expressions after commas or before operators (including a chained dot).
- Surround operators, keywords, commas, colons, and `for` semicolons with the required spaces.
  Separate logical units in a block with one blank line.
- Put braces around every loop and conditional body, including single-statement bodies, and put
  single-statement conditional bodies on their own lines.
- Indent `switch` cases one level inside the switch. Add an explicit `// Fallthrough` comment for
  intentional fall-through.
- Put every class in a package, keep imports explicit and consistently ordered, and attach array
  brackets to the type (`String[]`, not `String []` or `String variable[]`).
- Initialize variables at declaration when possible and keep declarations in the smallest useful
  scope. Do not expose mutable class fields publicly.

## Comments and documentation

- Write comments in English using American spelling. Prefer comments that explain intent or
  behavior rather than narrating obvious code.
- Add a descriptive Javadoc header to every public class and public method. Getters/setters,
  exact overrides, and test classes or methods may omit it.
- Start a method summary with a present-tense verb such as `Returns`, `Creates`, `Adds`, or
  `Checks`. Keep `/**` on its own line, align the `*` markers, leave no blank line before the
  declaration, and include useful `@param`, `@return`, and `@throws` descriptions with
  punctuation.
- Add concise documentation to non-obvious fields and non-public methods when their purpose is
  not clear from their names.

## Review workflow

Before finishing a Java change, review the changed files for the rules above, especially line
length, import order, braces, boolean names, switch indentation, and public documentation.
Preserve behavior while making style-only changes. If behavior or console output changes, update
the relevant JUnit tests and `test/ui-test-plan.md`, then run the project `test-ui` skill.
