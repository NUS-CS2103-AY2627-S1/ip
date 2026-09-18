---
name: seedu-java-coding-standard
description: Apply the SE-Education basic and intermediate Java coding conventions to Java source and test code in this project.
---

# Seedu Java Coding Standard

Use this skill for every Java source-code change in this repository. The authoritative
standard is the [SE-Education Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html);
use the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
for topics not covered there.

Apply these rules when writing or reviewing code:

- Use lowercase package names; PascalCase nouns for classes and enums; camelCase
  for variables and verb-named methods; and SCREAMING_SNAKE_CASE for constants.
- Name booleans with prefixes such as `is`, `has`, `was`, or `can`, and use plural
  names for collections. Test methods may use the
  `featureUnderTest_testScenario_expectedBehavior` format.
- Use four spaces for indentation, K&R braces, braces around every conditional and
  loop body, spaces around operators and after commas, and logical blank lines.
  Keep lines at 120 characters or fewer and wrap continuation lines for readability.
- Put every class in a package, keep imports explicit and consistently ordered, and
  attach array brackets to the type. Initialize variables at declaration where
  possible and keep them in the smallest useful scope. Keep class fields non-public
  unless the class is a behavior-free data class or the field is a constant.
- Write descriptive English/American-English Javadocs for every public class and
  public method, except getters/setters, exact overrides, and test code. Use a short
  summary sentence followed by relevant `@param`, `@return`, and `@throws` tags.
- Add an explicit `// Fallthrough` comment when a traditional switch case omits
  `break` intentionally.

When changing observable console behavior, review and update
`test/ui-test-plan.md`, then run the project-local `test-ui` skill. Maintain JUnit
coverage for the highest-value non-trivial public methods.
