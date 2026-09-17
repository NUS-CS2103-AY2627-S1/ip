---
name: seedu-java-coding-standard
description: Review and write this project's Java code using the SE-EDU basic and intermediate coding conventions.
---

# Seedu Java Coding Standard

Apply this skill to every Java source and test change in this repository. Use the
[SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html)
as the primary reference; use the [Google Java style guide](https://google.github.io/styleguide/javaguide.html)
for topics that the SE-EDU guide does not cover.

## Required conventions

- Use lowercase package names; PascalCase nouns for classes, enums, and records; camelCase verbs for methods; camelCase variables; and SCREAMING_SNAKE_CASE constants.
- Keep names in English, avoid uppercase abbreviations in identifiers, and use
  descriptive names for large-scope variables. Prefix every boolean variable,
  field, parameter, record component, and method with a boolean verb such as
  `is`, `has`, `was`, `can`, or `should`. Collection names should be plural.
- Name every test method using exactly three meaningful parts separated by underscores:
  `featureUnderTest_testScenario_expectedBehavior`. Keep each part in camelCase and
  audit every `@Test` method after adding or renaming tests. Do not use a single
  camelCase phrase for a test method, even though the upstream guide permits
  omitting parts in limited cases; this project requires the full form.
- Use four spaces for indentation, K&R braces, and spaces around operators, commas, keywords, and `for` separators. Keep lines at 120 characters or fewer, preferably below 110; wrap continuation lines with eight additional spaces and break at readable boundaries.
- Separate logical units in a block with one blank line. Always use braces for loops and conditionals. Mark intentional switch fall-through with `// Fallthrough`.
- Put every class in a package. Keep imports explicit and consistently ordered; do not use wildcard imports. Put array brackets on the type, initialize variables at declaration when practical, keep variables in the smallest useful scope, and do not expose mutable class fields publicly.
- Add descriptive English, American-spelling Javadoc to public classes and public methods. Getters, setters, overriding methods whose inherited documentation applies exactly, and test methods may omit it. Put a short summary first, then a blank line before tags, with punctuation in tag descriptions.

## Review workflow

When changing code, preserve existing behavior unless the user requests a behavior change. Inspect both production and test Java files for the conventions above, especially line length, import order, identifier names, braces, variable scope, and public API documentation. Keep comments focused on intent rather than restating syntax.

After a code update, follow this project's required test workflow: review
`test/ui-test-plan.md` for affected console behavior, invoke the project
`test-ui` skill, and run the complete plan in fresh processes using Java 25.
