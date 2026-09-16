---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding conventions to Java source and tests in this project.
metadata:
  short-description: Enforce the project's SE-EDU Java style
---

# SE-EDU Java Coding Standard

Apply this skill to every Java code change in this repository, including production code and tests. The source standard is the [SE-EDU Java coding standard (basic + intermediate)](https://se-education.org/guides/conventions/java/intermediate.html). Use the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html) for topics that the SE-EDU standard does not cover.

## Required conventions

- Keep package names lowercase. Name classes and enums as PascalCase nouns, variables and methods as camelCase, and constants as SCREAMING_SNAKE_CASE. Use English names, avoid uppercase abbreviations inside names, use boolean-sounding names such as `isDone` or `hasData`, and use plural names for collections.
- Use four spaces for indentation, K&R braces, and no lines longer than 120 characters. Prefer a soft limit of 110 characters; wrap long lines with an additional eight spaces of indentation and break at readable boundaries.
- Surround operators, commas, and applicable colons with spaces. Separate logical units in a block with one blank line.
- Put every class in a package and keep imports explicit and consistently ordered. Never use wildcard imports. Attach array brackets to the type, such as `String[] values`.
- Initialize variables at declaration when practical and keep them in the smallest possible scope. Do not expose class variables publicly unless the class is a behavior-free data class; constants are exempt.
- Always use braces for `if`, `else`, `for`, `while`, and `do-while` bodies, including one-line bodies. Put conditional bodies on separate lines. Mark intentional switch fall-through with `// Fallthrough`.
- Write English comments using American spelling. Give every public class and public method a descriptive Javadoc header, except getters/setters, exact overrides whose inherited documentation applies, and test classes/methods. Start method summaries with an action such as “Returns”, “Adds”, or “Sends”. Keep Javadoc aligned, separated from the declaration by no blank line, and punctuate parameter and return descriptions.
- Name test methods with the form `featureUnderTest_testScenario_expectedBehavior()` when the scenario benefits from being explicit.

## Review checklist

Before completing a Java change, inspect the changed files for line length, indentation, braces, import wildcards/order, package declarations, naming, variable scope, public fields, and Javadoc coverage. Preserve behavior while making style-only changes, and update focused JUnit tests when behavior changes.
