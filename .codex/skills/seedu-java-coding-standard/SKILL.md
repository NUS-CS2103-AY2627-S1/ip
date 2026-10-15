---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding standard to Java design, implementation, review, and refactoring tasks in this project.
---

# SE-EDU Java Coding Standard

Apply the SE-EDU Java basic and intermediate standard to every Java file in this
repository. Use the linked standard as the source of truth; use the Google Java
Style Guide for topics it does not cover.

## Rules

- Use lowercase package names, PascalCase noun names for classes/enums, camelCase
  variable and verb method names, and SCREAMING_SNAKE_CASE constants. Keep names
  in English, expand acronyms in names, use plural collection names, and use
  boolean names such as `isOpen`, `hasData`, or `canEvaluate`.
- Use four spaces, K&R braces, spaces around operators/reserved words/commas,
  blank lines between logical units, and a 120-character hard line limit (aim
  for 110). Wrap after commas or before operators with eight additional spaces.
  Keep method names attached to `(` and put each `if`, loop, and conditional body
  in braces on separate lines.
- Put every type in a package. Keep imports explicit and consistently ordered:
  static imports, `java`/`javax`, third-party imports, then project imports.
  Attach array brackets to the type. Initialize variables at declaration and in
  the smallest possible scope; never expose mutable class fields publicly.
- Use braces for every loop and conditional, include `// Fallthrough` for an
  intentional switch fallthrough, and format try/catch/finally blocks in K&R
  style.
- Write English American-spelling comments. Add descriptive Javadocs to every
  public class and public method, except getters/setters, exact overrides, and
  test classes/methods. Start method summaries with an active verb such as
  “Returns”, “Adds”, or “Creates”; document parameters, return values, and
  exceptions when they add value.

## Workflow

Before changing Java code, inspect names, imports, layout, braces, variable scope,
visibility, and public API documentation. Preserve behavior, update tests when
behavior changes, and run the project tests after refactoring. Review the diff for
accidental semantic changes and any remaining lines over 120 characters.
