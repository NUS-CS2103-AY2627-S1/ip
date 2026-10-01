---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding standard to Java code in this project.
---

# SE-EDU Java coding standard

Apply this skill to every Java source and test file in this repository. The authoritative
standard is the [SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html);
use the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html) for topics it
does not cover.

Required checks:

- Put every class in a lower-case project package; use PascalCase nouns for types, camelCase
  verbs for methods and camelCase variables, and SCREAMING_SNAKE_CASE constants.
- Use English and American spelling in names and comments. Boolean names should read as
  predicates (`is`, `has`, `can`, `should`, or `was` where appropriate); collection names are
  plural. Test names may use `featureUnderTest_testScenario_expectedBehavior`.
- Use four-space indentation, K&R braces, spaces around operators and after commas, blank lines
  between logical units, and a hard line limit of 120 characters (prefer under 110).
- Use explicit, consistently ordered imports; attach array brackets to the type; initialize
  variables at declaration when practical and keep them in the smallest scope.
- Always brace loop and conditional bodies, and put conditional bodies on separate lines.
  Intentional switch fall-through must have a `// Fallthrough` comment.
- Keep instance fields non-public except for data-only classes; expose behavior through methods.
- Add descriptive Javadoc to public classes and public methods, except getters/setters,
  applicable overrides, and test code. Use a short first-sentence summary and document
  parameters and exceptions when they add value.

Before finishing a Java change, inspect the diff for these rules and run the project's Java and
UI tests as required by `AGENTS.md`.
