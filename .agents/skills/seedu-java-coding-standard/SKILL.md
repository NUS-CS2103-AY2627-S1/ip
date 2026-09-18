---
name: seedu-java-coding-standard
description: Apply the SE-EDU Java coding standard to Java code, tests, and reviews in this project.
---

# SE-EDU Java coding standard

Use this skill for every Java implementation, refactoring, test, or code review in
this repository. The authoritative reference is the
[SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html).

Apply these rules unless an existing project requirement is more specific:

- Keep every class in a meaningful lower-case package.
- Use PascalCase for classes and enums, camelCase for variables and methods, and
  SCREAMING_SNAKE_CASE for constants.
- Name methods as verbs and boolean values with readable prefixes such as `is`,
  `has`, `can`, or `should`.
- Use four-space indentation, K&R braces, explicit imports, and a hard line limit
  of 120 characters. Prefer shorter lines when practical.
- Use braces for every conditional and loop body, even for one statement.
- Declare and initialize variables in the smallest reasonable scope.
- Add descriptive Javadocs to all classes and public methods. Include useful
  `@param`, `@return`, and `@throws` tags; getters, setters, and test methods may
  omit them when the behavior is obvious.
- Name JUnit methods using `featureUnderTest_testScenario_expectedBehavior()` when
  a descriptive multi-part name improves clarity.

Run the Gradle checks, including Checkstyle and JUnit tests, after Java changes.
