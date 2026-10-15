---
name: seedu-java-coding-standard
description: Apply the SE-EDU Java coding standard to Java source or test code in this project, including reviews and refactorings.
---

# Seedu Java Coding Standard

Apply the [SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html) to all Java code in this repository.

- Keep packages lowercase, class names as PascalCase nouns, variables and methods in camelCase, and constants in `SCREAMING_SNAKE_CASE`.
- Name booleans so that they read as booleans (for example, `isDone`, `hasLoadingError`, and `shouldSave`). Use plural names for collections.
- Use four-space indentation, K&R braces, explicit imports, and lines no longer than 120 characters (aim for 110). Wrap long expressions at readable, higher-level boundaries.
- Always use braces for loop and conditional bodies. Declare and initialize variables in the smallest practical scope.
- Write English comments using American spelling. Add descriptive Javadoc to public classes and public methods, except self-evident getters/setters and exact inherited overrides. Start the summary sentence with a third-person verb such as "Returns", "Adds", or "Displays".
- Give Javadoc `@param`, `@return`, and `@throws` descriptions ending punctuation when those tags add useful information.

When a course requirement conflicts with these conventions, follow the course requirement and keep the smallest possible exception.
