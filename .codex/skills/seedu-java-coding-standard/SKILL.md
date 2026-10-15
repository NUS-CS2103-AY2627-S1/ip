---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding standard to Java code in this project.
---

# SE-EDU Java Coding Standard

Use this skill for every Java code change in this project. Follow the
[SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html)
and use the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
for topics not covered there.

Apply these requirements while editing and review the complete affected file
before finishing:

- Put every class in a lowercase package; use PascalCase nouns for classes and
  enums, camelCase verbs for methods, camelCase variables, and
  SCREAMING_SNAKE_CASE constants.
- Use English names and comments, American spelling, boolean names such as
  `is`, `has`, `can`, `should`, or `was`, and plural names for collections.
- Use four spaces, K&R braces, consistent whitespace, blank lines between
  logical units, and lines no longer than 120 characters. Wrap long lines with
  eight-space continuation indentation.
- Keep imports explicit and consistently ordered. Put array brackets on the
  type, initialize variables at declaration when practical, and keep variables
  in the smallest useful scope.
- Always use braces for loops and conditionals, and add `// Fallthrough` when a
  switch case intentionally falls through.
- Add descriptive Javadoc to public classes and public methods, except for
  getters/setters, applicable overrides, and test code. Include useful
  `@param`, `@return`, and `@throws` tags.
- Test method names may use the three-part form
  `featureUnderTest_testScenario_expectedBehavior()`.

Do not change observable behavior merely to satisfy formatting; make the
smallest safe refactoring needed to meet the standard.
