---
name: seedu-java-coding-standard
description: Apply the SE-EDU Java coding standard basic and intermediate rules to this project. Use whenever Codex creates, edits, reviews, formats, or tests Java source code, Java test code, package structure, imports, names, comments, or code layout in this repository.
---

# SE-EDU Java Coding Standard

## Overview

Follow the SE-EDU Java coding standard basic and intermediate rules for all Java
code in this project. Use the Google Java Style Guide for topics not covered by
SE-EDU. When something is not covered by the given standard or convention, choose
a reasonable style and keep it consistent with the existing code.

## Source

Primary reference:
https://se-education.org/guides/conventions/java/intermediate.html

## Workflow

1. Before editing Java code, scan the touched files for violations of the rules
   below.
2. Prefer small, local formatting fixes while making the requested code change.
3. After editing Java code, review touched Java files again for naming, imports,
   layout, comments, and line length.
4. Keep behavior unchanged unless the user requested a behavior change or the
   standard reveals a clear bug.

## Naming

- Use lowercase package names. For school projects, use the project or group name
  as the root package, followed by logical group names.
- Use PascalCase nouns for class and enum names.
- Use camelCase for variables and methods.
- Use SCREAMING_SNAKE_CASE for constants.
- Use verb phrases for method names.
- Use boolean names that sound boolean, such as `isExit`, `hasData`, or
  `shouldAbort`.
- Use plural names for collections.
- Use English names. Abbreviations and acronyms should not be all-uppercase when
  used inside a name.
- Test method names may use
  `featureUnderTest_testScenario_expectedBehavior()`.

## Layout

- Use 4 spaces for indentation. Do not use tabs.
- Keep lines at or below 120 characters; prefer below 110 characters.
- When wrapping lines, indent continuation lines by 8 spaces relative to the
  parent line.
- Break after commas and before operators when wrapping.
- Use K&R braces:

```java
if (condition) {
    doSomething();
} else {
    doSomethingElse();
}
```

- Separate logical units with blank lines.
- Put conditionals on separate lines and always use braces.
- Always use braces for loop bodies.
- Add `// Fallthrough` for switch cases that intentionally fall through.

## Packages and Imports

- Put every class in a package.
- Keep import ordering consistent with the project.
- Use explicit imports; do not use wildcard imports.
- Keep imports minimal and up to date.

## Variables and Types

- Attach array specifiers to the type, such as `String[] args`.
- Declare variables in the smallest reasonable scope.
- Initialize variables where they are declared when there is a valid initial
  value.
- Do not declare public class variables unless the class is a data class with no
  behavior. Constants are allowed.

## Comments

- Write comments in English using American spelling.
- Write Javadoc comments for all public classes and public methods.
- Javadocs may be omitted for getters and setters, test methods, and overridden
  methods when the parent Javadoc applies exactly.
- Start Javadoc summary sentences with forms such as `Returns`, `Adds`,
  `Creates`, or `Shows`.
- Include `@param`, `@return`, and `@throws` only when they add value; if using
  `@param`, document all parameters.
- Prefer explaining what and why. Avoid comments that simply restate obvious code.
