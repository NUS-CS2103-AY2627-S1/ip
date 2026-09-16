---
name: seedu-java-coding-standard
description: Enforce the SE-EDU basic and intermediate Java coding standard for all Java source and test code in this project.
---

# Seedu Java Coding Standard

Use this skill for every Java change in this repository, including application
code, tests, refactors, and generated snippets. It is based on the
[SE-EDU Java coding standard (basic + intermediate rules)](https://se-education.org/guides/conventions/java/intermediate.html).
For topics not covered here, follow the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).

## Required rules

- Put every class in a named package. Use a lowercase project root such as
  `bobby`, followed by logical subpackages when the project grows.
- Use nouns in `PascalCase` for classes and enums, `camelCase` for variables
  and verb-based methods, and `SCREAMING_SNAKE_CASE` for constants.
- Keep acronyms in normal camel case (`exportHtmlSource`, not
  `exportHTMLSource`). Use English and name booleans with prefixes such as
  `is`, `has`, `was`, or `can`. Use plural names for collections.
- Test method names may use
  `featureUnderTest_testScenario_expectedBehavior` (with later parts
  optional).
- Indent with four spaces, never tabs. Keep lines at or below 120 characters
  and prefer below 110. Continuation lines use an additional eight spaces and
  line breaks should follow logical operators, commas, and readability.
- Use K&R braces. Always brace class, method, conditional, loop, switch, and
  try/catch bodies, including single-statement bodies. Keep `else` on the same
  line as the preceding closing brace and include `// Fallthrough` for
  intentional fall-through cases.
- Surround operators, reserved-word parentheses, commas, and binary/ternary
  colons with the required spaces. Separate logical units in a block with one
  blank line.
- Keep imports consistently ordered, explicit, minimal, and free of wildcard
  imports. Attach array brackets to the type (`String[] args`).
- Initialize variables at declaration where possible and keep them in the
  smallest scope. Do not expose class fields publicly except constants or
  behavior-free data classes.
- Write English comments using American spelling. Add descriptive Javadoc to
  every public class and public method, except getters/setters, exact
  overrides whose inherited Javadoc applies, and test code. Use a short first
  sentence, aligned `*` lines, a blank line before tags, and punctuation in
  parameter/return/throws descriptions. Add comments to non-obvious members
  and behavior.

## Review checklist

Before finishing any Java change:

1. Confirm package declarations and source paths match.
2. Check names, booleans, collection names, visibility, imports, indentation,
   line lengths, braces, whitespace, and initialization.
3. Check public API Javadocs and non-obvious fields/methods.
4. Run the project’s JUnit suite and update/run `test/ui-test-plan.md` when
   behavior or console output changes.
