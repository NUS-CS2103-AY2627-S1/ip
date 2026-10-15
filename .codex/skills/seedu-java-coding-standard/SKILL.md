---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding standard to Java code in this project.
---

# SE-EDU Java coding standard

Apply this standard to all Java production and test code in this repository:

- Keep every class in a lower-case project package (`anders` and its subpackages); use PascalCase nouns for classes/enums, camelCase verbs for methods, camelCase variables, and `SCREAMING_SNAKE_CASE` constants.
- Use English (American spelling) for names and comments. Boolean names should read as booleans (`is`, `has`, `can`, or `should` where appropriate); collection names should be plural.
- Use four spaces, K&R braces, spaces around operators/keywords/commas, one logical unit per blank-line-separated block, and lines no longer than 120 characters (prefer under 110). Wrapped continuation lines are indented by eight spaces beyond the parent line.
- Keep imports explicit and consistently ordered; never use wildcard imports. Attach array brackets to the type (`String[]`). Initialize variables at declaration when a valid initial value exists and keep them in the smallest possible scope. Do not expose mutable class fields publicly.
- Always brace loop and conditional bodies, even for one statement. Keep `else` on the closing-brace line. Mark intentional switch fall-through with `// Fallthrough`.
- Add descriptive Javadoc to every public class and public method, except getters/setters, exact overrides, and test code. Start method summaries with an action such as `Returns`, `Adds`, or `Creates`; include useful `@param`, `@return`, and `@throws` descriptions.

For details and examples, consult the authoritative guide:
https://se-education.org/guides/conventions/java/intermediate.html

When a topic is not covered, follow the Google Java Style Guide linked by that guide. Preserve behavior while formatting or refactoring, and run the project's tests after changes.
