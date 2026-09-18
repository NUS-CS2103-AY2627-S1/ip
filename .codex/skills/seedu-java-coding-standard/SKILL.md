---
name: seedu-java-coding-standard
description: Apply the SE-EDU Java Coding Standard (basic and intermediate rules) when creating, modifying, or reviewing Java code in this project. Use for Java production and test code, code-style reviews, and Javadoc checks.
---

# SE-EDU Java Coding Standard

Follow the [SE-EDU Java Coding Standard (basic + intermediate)](https://se-education.org/guides/conventions/java/intermediate.html). For topics it does not cover, use the Google Java Style Guide.

## Apply

- Use lowercase package names; PascalCase nouns for types; camelCase verbs for methods; camelCase for variables; and `SCREAMING_SNAKE_CASE` for constants.
- Name boolean variables and methods with a boolean prefix such as `is`, `has`, `can`, `should`, or `was`. Use plural names for collections. Test names may use `featureUnderTest_testScenario_expectedBehavior`.
- Indent with four spaces, never tabs. Keep lines at 120 characters or fewer (prefer 110). Use readable wrapping: break after commas and before operators; indent continuations eight spaces beyond the parent indentation.
- Use K&R braces. Put spaces around operators, after commas and semicolons in `for` statements, and after Java keywords. Separate logical units with one blank line.
- Keep import ordering consistent and list imports explicitly; do not use wildcard imports. Declare variables in the smallest practical scope and initialize them at declaration when possible.
- Use braces for every loop and conditional body. Keep conditionals on their own lines. Mark intentional traditional-switch fall-through with `// Fallthrough`.
- Write comments in American English. Add descriptive Javadoc for every public class and public method, except simple getters/setters, exact inherited overrides, and test code. Start method summaries with third-person verbs such as “Returns”, “Adds”, or “Creates”; use a blank line before tags and end tag descriptions with punctuation.

## Verify before handoff

Review changed Java files for the rules above, then run the project's relevant build or test command with Java 25. Explain any intentional exception to the standard.
