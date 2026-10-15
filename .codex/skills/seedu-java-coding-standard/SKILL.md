---
name: seedu-java-coding-standard
description: Apply and review the SE-EDU basic and intermediate Java coding standard for all Java code in this project.
---

# SE-EDU Java Coding Standard

Apply the [SE-EDU basic and intermediate Java standard](https://se-education.org/guides/conventions/java/intermediate.html)
whenever creating, editing, or reviewing Java code in this project. Use the Google Java Style Guide only for topics
the SE-EDU standard does not cover.

## Required checks

- Put every class in a lowercase package that reflects the project structure.
- Use PascalCase nouns for classes and enums, camelCase verbs for methods, camelCase for variables, and
  SCREAMING_SNAKE_CASE for constants.
- Give boolean values and methods boolean-sounding names, and use plural names for collections.
- Indent with four spaces and never tabs. Keep lines below 120 characters, aiming for 110 or fewer. Indent wrapped
  lines by eight additional spaces and break after commas or before operators.
- Use K&R braces. Always use braces for loops and conditionals, including single-statement bodies.
- Keep imports explicit, minimal, grouped consistently, and separated from the package declaration by a blank line.
- Declare and initialize variables in the smallest practical scope. Keep fields private unless they are constants or
  belong to a behavior-free data class.
- Write English comments using American spelling. Add descriptive Javadocs to all classes and public methods except
  obvious getters/setters, exact overrides, and test code.
- Name tests with `featureUnderTest_testScenario_expectedBehavior` where the three-part form adds clarity.

Before finishing a Java change, inspect all changed Java lines for these rules and run the relevant Gradle tests.
