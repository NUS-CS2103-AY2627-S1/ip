---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding standard to code in this project.
---

# SE-EDU Java coding standard

Apply the [SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html) to every
Java change in this project. Use the Google Java Style Guide for topics not covered by SE-EDU.

## Required checks

- Use lower-case packages, PascalCase nouns for classes/enums, camelCase variables and verb-based methods, and
  SCREAMING_SNAKE_CASE constants.
- Use the project's three-part underscore naming convention for test methods where applicable.
- Use English and American spelling in names and comments; avoid slang.
- Use four spaces, K&R braces, spaces around operators and after commas, and braces for every loop and conditional body.
- Keep lines at or below 120 characters, preferably below 110, with eight-space continuation indentation.
- Keep imports explicit and consistently ordered. Declare arrays as `Type[] name`.
- Initialize variables at declaration when practical and keep them in the smallest possible scope.
- Keep fields non-public unless they are constants or the class is a behavior-free data class.
- Separate logical units in a block with one blank line.
- Add descriptive Javadoc to every class and public method. Omissions are allowed for getters/setters, exact overrides
  whose parent Javadoc applies, and test classes/methods.
- Format Javadoc with a short first-sentence summary, aligned `*` lines, a blank line before tags, and punctuation in
  parameter descriptions. Start method summaries with forms such as `Returns`, `Adds`, or `Creates`.
- Include `// Fallthrough` for intentional switch fall-through.

Before finishing a Java change, review the changed files against this checklist and run the project's applicable tests.
