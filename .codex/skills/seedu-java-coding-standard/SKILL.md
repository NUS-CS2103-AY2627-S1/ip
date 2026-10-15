---
name: seedu-java-coding-standard
description: Apply the SE-EDU Java intermediate coding standard to Java source and tests in this Nob project.
---

# SE-EDU Java Intermediate Coding Standard

Use this skill for every Java source or JUnit test change in this project. Apply the rules in the [SE-EDU Java intermediate coding standard](https://se-education.org/guides/conventions/java/intermediate.html). For topics not covered there, follow the Google Java Style Guide.

## Names and structure

- Use lowercase package names; use PascalCase nouns for classes and camelCase verbs for methods.
- Use camelCase for variables, SCREAMING_SNAKE_CASE for constants, and plural names for collections.
- Name boolean variables and methods with a boolean prefix such as `is`, `has`, `was`, `can`, or `should`.
- Keep variables in the smallest practical scope and initialise them at declaration where possible.
- Keep fields non-public unless they are constants or belong to a data-only class.

## Layout and statements

- Indent with four spaces; do not use tabs. Keep lines at 110 characters where practical and never exceed 120 characters. Indent continuations by eight spaces from the parent line.
- Use K&R braces and always use braces for loop and conditional bodies.
- Put one blank line between logical units in a block. Use spaces around operators and after commas.
- Keep imports explicit, minimal, and consistently ordered. Attach array brackets to the type.
- Use an explicit `// Fallthrough` comment for an intentional switch fall-through.

## Comments and Javadoc

- Write comments in clear American English, without local slang.
- Add descriptive Javadoc to every class and public method, except ordinary getters/setters, exact overrides, and test code.
- Start Javadoc summaries with a third-person verb such as `Returns`, `Adds`, or `Displays`.
- Put `/**` on its own line, leave no blank line before the declaration, and include a blank line before Javadoc tags.
- When tags add useful information, document every parameter, return value, and thrown exception consistently. End parameter descriptions with periods.
- Use short single-line Javadoc only for simple fields or members; document non-trivial private methods when their purpose is not obvious.

## Before finishing

Review edited Java and test files for these rules. Run the project's required Java 25 Gradle tests after code changes, and run the planned UI tests when the project instructions require them.
