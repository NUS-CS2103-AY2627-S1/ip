---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding standard to Java code in this project.
---

# SE-EDU Java Coding Standard

Use this skill whenever you create, edit, or review Java source or test code in this repository.

Follow the [SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html), which combines the basic and intermediate rules. For topics the guide does not cover, use the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).

Before handing off Java changes, check the following:

- Keep package names lowercase; use PascalCase nouns for classes and enums, camelCase verbs for methods, camelCase variables, and `SCREAMING_SNAKE_CASE` constants. Use English names and boolean-sounding names such as `isOpen` or `hasData` where appropriate.
- Use 4 spaces for indentation, K&R braces, spaces around operators and after commas, blank lines between logical units, and a hard line limit of 120 characters. Indent wrapped lines by an additional 8 spaces and place breaks for readability.
- Put every class in a package, order imports consistently, and list imported classes explicitly rather than using wildcards.
- Attach array brackets to the type, initialize variables at declaration where practical, keep declarations in the smallest scope, and keep class variables non-public except constants or genuine data classes.
- Always brace loop and conditional bodies. Include `// Fallthrough` for intentional fall-through in a switch statement.
- Add descriptive Javadoc to public classes and public methods, except getters/setters, applicable overrides, and test code. Use a short first-sentence summary and document parameters, return values, and exceptions when they add value.
- Keep comments in English with American spelling and indent them with the surrounding code.

Preserve behavior while correcting style. Prefer small, focused edits over broad reformatting, and review tests as well as production code.
