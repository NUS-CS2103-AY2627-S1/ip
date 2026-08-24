---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding standard when creating, editing, or reviewing Java code in this project.
---

# SE-EDU Java Coding Standard

Apply the rules in the [SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html) to all Java production and test code in this project. For a rule not covered there, follow the Google Java Style Guide as directed by the standard.

## Required checks

- Keep every class in a lowercase package rooted at `ultron`; place source files under the matching package directory.
- Use PascalCase for types, camelCase for variables and methods, SCREAMING_SNAKE_CASE for constants, and boolean names that read as booleans.
- Use explicit, consistently ordered imports; do not use wildcard imports. Put static imports first, followed by Java, third-party, and project imports, with blank lines between groups.
- Use 4-space indentation, K&R braces, braces for every conditional and loop body, and lines no longer than 120 characters. Wrap lines at readable higher-level boundaries.
- Declare variables in the smallest practical scope and initialize them when declared when a valid initial value exists.
- Write English Javadocs for public production classes and public methods. Getters/setters, exact overrides, and test code are exempt.
- Name JUnit tests using `featureUnderTest_testScenario_expectedBehavior()` when the descriptive name would otherwise be long.

Review the Java files touched by the task against these checks before finishing. Use the linked standard when a detailed rule is needed.
