---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding standard when creating, editing, or reviewing Java code in this project.
---

# SE-EDU Java Coding Standard

Follow the [SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html)
for every Java change in this project. Preserve program behavior when the task is a standards-only cleanup.

## Naming

- Use lowercase package names based on the project and logical component.
- Use PascalCase nouns for classes and enums.
- Use camelCase verbs for methods and camelCase nouns for variables.
- Use SCREAMING_SNAKE_CASE for constants.
- Name boolean variables and methods like conditions, preferably with `is`, `has`, `can`, or `should`.
- Use plural names for collections.
- Test methods may use `featureUnderTest_testScenario_expectedBehavior`.

## Layout and statements

- Indent with four spaces and never tabs.
- Keep lines below 120 characters; prefer less than 110 characters.
- Indent wrapped continuation lines by eight spaces from their parent.
- Use K&R braces and braces for every loop and conditional body.
- Indent `case` labels inside their `switch` block.
- Add `// Fallthrough` whenever execution intentionally continues into the next `case`.
- Put spaces around operators and after commas and Java keywords.
- Separate logical units with blank lines.

## Packages, imports, and variables

- Put every class in a suitable package while retaining the configured source root.
- List imports explicitly and use one consistent grouping and ordering scheme.
- Declare variables in the smallest useful scope and initialize them where declared when practical.
- Keep fields non-public unless they are constants or belong to a behavior-free data class.

## Comments and Javadoc

- Write comments in clear English using American spelling and no local slang.
- Add descriptive Javadoc to every public class and method.
- Javadoc may be omitted for obvious getters/setters, inherited overrides whose parent documentation applies exactly,
  and test code.
- Start method Javadoc with a short verb-led summary such as `Returns`, `Adds`, or `Displays`.
- Use `@param`, `@return`, and `@throws` when they provide information not already clear from the summary and names.

## Validation

After changing Java code:

1. Check the diff for tabs, lines over 120 characters, inconsistent imports, missing braces, and undocumented public APIs.
2. Run `./gradlew test`.
3. Follow the repository's UI-test requirements when application source code changed.
