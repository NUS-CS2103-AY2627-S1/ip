---
name: seedu-java-coding-standard
description: Apply the SE-EDU Java coding standard to Java source and test code in this project.
---

# SE-EDU Java Coding Standard

Follow the SE-EDU Java coding standard for this project:
https://se-education.org/guides/conventions/java/intermediate.html

Use the Google Java Style Guide for topics not covered by SE-EDU.

## Project Rules

- Put every class in a package under the project root package.
- Use lower-case package names and PascalCase class names.
- Use camelCase for variables and methods.
- Use SCREAMING_SNAKE_CASE for constants.
- Use boolean names that read like booleans, such as `isDone`, `hasTasks`,
  or `shouldExit`.
- Use explicit imports. Do not use wildcard imports.
- Use 4 spaces for indentation and K&R braces.
- Keep lines under 120 characters.
- Initialize variables where declared when that keeps the value meaningful.
- Keep variables in the smallest reasonable scope.
- Wrap all conditionals and loops in braces.
- Write comments in English using American spelling and no local slang.
- Write JavaDoc header comments for public classes and public methods unless
  the method is a getter/setter, an override whose parent JavaDoc applies
  exactly, or test code where JavaDoc does not add value.
- For JavaDoc, use a short first sentence, include useful `@param`, `@return`,
  and `@throws` tags, and end parameter descriptions with punctuation.

When editing existing code, preserve behavior unless the user asks for a
behavioral change.
