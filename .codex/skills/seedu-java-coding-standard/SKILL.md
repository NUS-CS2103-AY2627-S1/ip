---
name: seedu-java-coding-standard
description: Review and maintain this project's Java code and Javadocs using SE-EDU conventions.
---

# SE-EDU Java Coding Standard

Use this skill when creating, reviewing, or modifying Java code in this
project. Apply the [SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html)
and the [SE-EDU Javadoc guidance](https://nus-cs2103-ay2627-s1.github.io/website/se-book-adapted/chapters/documentation.html#javadoc).
Use the Google Java Style Guide for topics that the SE-EDU guidance does not
cover.

## Java coding conventions

- Keep every class in a package. Use lowercase package names.
- Use PascalCase nouns for classes and enums, camelCase for variables and
  verb-based methods, and `SCREAMING_SNAKE_CASE` for constants.
- Use the three-part test naming form
  `featureUnderTest_testScenario_expectedBehavior` for JUnit test methods.
- Write abbreviations and acronyms in mixed case when they appear in names,
  such as `CsvSaver` and `LuckyNoCli`, rather than `CSVSaver` and
  `LuckyNoCLI`.
- Use explicit imports; never use wildcard imports. Keep imports grouped and
  ordered consistently: static imports, `java`/`javax`, third-party imports,
  then project imports.
- Use four spaces for indentation, K&R braces, braces around all conditional
  and loop bodies, and spaces around operators and after commas.
- Keep lines at or below 120 characters. Wrap long lines at readable,
  higher-level boundaries and indent continuation lines by eight spaces.
- Initialize variables at declaration when practical and keep declarations in
  the smallest scope needed. Keep fields private to preserve encapsulation.
- Write comments in English using American spelling.

## Javadocs

- Add a descriptive Javadoc comment to every non-private class, record, enum,
  constructor, and method.
- Add a Javadoc comment to every non-trivial private method. A private
  constructor used only to prevent instantiation and trivial record accessors
  do not need redundant comments.
- Describe what a declaration does, its externally relevant behavior, and
  important constraints. Explain the contract rather than repeating obvious
  implementation details.
- Include `@param` for parameters, `@return` for returned values, and
  `@throws` for exceptions that callers may need to handle. Include these tags
  only where they apply.
- Document overridden methods when the implementation has behavior that is
  important to the subtype or is not clear from the inherited contract.
- Use concise, grammatical descriptions that begin with a verb such as
  “Creates”, “Returns”, or “Checks”. Use `{@code ...}` for code identifiers and
  `<p>` to separate paragraphs where needed.
- Update the Javadoc whenever a method's behavior, parameters, return value,
  exceptions, or class responsibility changes.

## Review workflow

1. Inspect the changed Java files and identify applicable naming, layout,
   import, visibility, variable-scope, and documentation issues.
2. Check that class comments explain each class's responsibility and method
   comments explain the caller-visible contract.
3. Check that applicable `@param`, `@return`, and `@throws` tags match the
   declaration.
4. Make the smallest coherent fixes that preserve application behavior.
5. Check line lengths, wildcard imports, package declarations, public fields,
   braces, and missing or stale Javadocs.
6. Run `./gradlew checkstyleMain checkstyleTest` with Java 25 and resolve all
   Checkstyle violations.
7. Run `./gradlew javadoc` with Java 25 and resolve all Javadoc warnings.
8. Run the project's Java tests after the review. For user-visible changes,
   also review and run the documented UI tests.

Do not change application behavior merely to satisfy a stylistic preference,
and do not rename externally required API names unless the project change
explicitly allows that rename.
