---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding standard when creating, editing, reviewing, or refactoring Java production or test code in this Panda project. Do not use for changes that do not touch Java code.
---

# SE-EDU Java Coding Standard

Follow the [SE-EDU basic and intermediate Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html).
Use the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html) only for topics
the SE-EDU standard does not cover. Preserve user requirements and existing behavior while applying
these conventions.

## Naming

- Use lowercase package names rooted at `panda`, followed by logical responsibility packages.
- Use English noun names in PascalCase for classes and enums.
- Use English verb names in camelCase for methods and camelCase for variables.
- Name boolean variables and methods so they read as booleans, preferably with `is`, `has`, `was`,
  `can`, or `should` where natural.
- Use plural names for arrays and collections. Limit short scratch names such as `i` and `j` to small
  scopes; reserve `j` and later letters for nested loops.
- Use SCREAMING_SNAKE_CASE for constants and a common prefix for associated constants.
- Name JUnit methods `featureUnderTest_testScenario_expectedBehavior`; omit later parts only when the
  test covers the broader feature or scenario.

## Layout

- Indent with 4 spaces and never tabs. Indent wrapped lines 8 spaces beyond their parent line.
- Keep lines below 110 characters when practical and never exceed 120 characters.
- Use K&R braces. Keep method names attached to their opening parenthesis.
- Break after commas and before operators, including `.`, `&`, and `|`. Prefer higher-level breaks.
- Separate logical units within a block with one blank line.
- Put each statement on its own line and use braces for every loop and conditional body.
- Indent `case` labels one level inside their `switch`. Add `// Fallthrough` only for intentional
  fall-through in colon-style switches.

## Packages, imports, types, and variables

- Put every class in a package matching its directory below `src/main/java` or `src/test/java`.
- List imports explicitly; never use wildcard imports. Remove unused imports.
- Keep import ordering consistent: static imports, `java`, `javax`, third-party, then project imports,
  with a blank line between groups and alphabetical ordering within each group.
- Attach array brackets to the type, such as `String[] arguments`.
- Declare variables in the smallest practical scope and initialize them at declaration when a valid
  value is available.
- Keep mutable fields non-public. Public fields are permitted only for behavior-free data classes;
  constants are exempt.

## Comments and Javadocs

- Write comments in English using American spelling and avoid slang.
- Give every non-private class and method a descriptive Javadoc header. Also document non-trivial
  private methods. Tests, simple getters/setters, and overrides may omit Javadoc when their names or
  inherited documentation state the behavior completely.
- Start a method Javadoc with a short present-tense summary such as `Returns`, `Adds`, or `Parses`.
- Put `/**` on its own line, align subsequent `*` characters, and leave no blank line between the
  comment and its declaration or annotation.
- Separate the summary from block tags with one blank Javadoc line. Either document every parameter
  or omit all self-explanatory parameters. End parameter descriptions with punctuation.
- Use `{@inheritDoc}` when an override needs to extend inherited documentation.
- Indent comments with the code they describe. Prefer comments that explain intent over comments
  that restate the code.

## Verification

Before completing a Java change:

1. Review every changed Java file against all sections above, including imports and Javadocs.
2. Scan changed Java files for tabs and lines longer than 120 characters.
3. Build and run relevant JUnit tests with Java 25 and Gradle.
4. Follow the UI-testing and error-documentation workflows mandated by `AGENTS.md` when applicable.
