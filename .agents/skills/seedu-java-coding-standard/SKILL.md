---
name: seedu-java-coding-standard
description: Apply the SE-EDU intermediate Java coding conventions to all Java code in this project.
---

# Seedu Java Coding Standard

Use this skill whenever creating, editing, reviewing, or refactoring Java source or test code in this project. Follow the
[SE-EDU Java coding standard (basic + intermediate)](https://se-education.org/guides/conventions/java/intermediate.html).

## Required conventions

- Use lowercase package names, PascalCase class and enum names, camelCase variables and methods, and
  SCREAMING_SNAKE_CASE constants. Boolean names should read like booleans (`is`, `has`, `can`, or `should`).
- Use English and American spelling. Use descriptive Javadoc header comments for every class and public method,
  except getters/setters, applicable overrides, and test code. Javadoc summaries should begin with an appropriate
  third-person verb such as `Returns` or `Creates`, include useful `@param`, `@return`, and `@throws` tags, and use
  complete punctuation.
- Use four-space indentation, K&R braces, spaces around operators and after commas, and a hard maximum line length
  of 120 characters. Wrap long lines with an eight-space continuation indent and separate logical units with blank lines.
- Put every class in a package. Keep imports explicit and consistently ordered, with a blank line between import groups.
- Attach array brackets to the type, initialize variables at declaration when practical, and keep variables in the
  smallest possible scope. Keep non-constant fields non-public to preserve encapsulation.
- Always use braces for loops and conditionals, including single-statement bodies. Mark intentional switch
  fall-through with `// Fallthrough`.
- Name test methods using `featureUnderTest_testScenario_expectedBehavior()`; omit only the parts that add no value.

When a topic is not covered here, use the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
as the secondary reference. Make the smallest style-only change needed when bringing existing code into compliance,
and run the project's tests after code changes.
