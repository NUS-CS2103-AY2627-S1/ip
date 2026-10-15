---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding standard when writing, modifying, refactoring, or reviewing Java code in this project.
---

# SE-EDU Java Coding Standard

Follow the [SE-EDU Java coding standard (basic + intermediate)](https://se-education.org/guides/conventions/java/intermediate.html)
for every Java source and test file in this project. Use the Google Java Style Guide for topics the SE-EDU standard
does not cover. Project instructions and user requirements take precedence if they explicitly conflict.

## Apply the standard

- Keep new and modified Java code compliant. When asked for a repository-wide audit, inspect every `.java` file.
- Preserve behavior during style-only work. Do not broaden a formatting task into unrelated design changes.
- Match consistent choices already used by compliant neighboring code when the standard permits alternatives.
- Use Java 25 for application and build commands, as required by this project.

## Naming

- Use lowercase package names. Keep this project's root package as `bos` unless a requested design change requires
  another logical package.
- Name classes and enums with English nouns in PascalCase. Write acronyms as words when they form part of a name,
  such as `Ui`, not `UI`.
- Name variables in camelCase and methods with camelCase verbs.
- Name constants in `SCREAMING_SNAKE_CASE`; give related constants a common prefix where useful.
- Give wider-scope variables descriptive names. Reserve `i`, `j`, and similar scratch names for short scopes and use
  `j` or later letters only for nested loops.
- Prefix boolean variables and methods with words such as `is`, `has`, `was`, `can`, or `should`. A boolean setter
  parameter should use the same boolean-style name, such as `setFound(boolean isFound)`.
- Use plural names for collections and arrays.
- Name tests as `featureUnderTest_testScenario_expectedBehavior()`. Omit later parts only when the remaining name
  still describes the test accurately.

## Layout

- Indent with 4 spaces and never tabs. Indent wrapped lines 8 spaces beyond the parent line.
- Keep lines below 110 characters when practical and never exceed 120 characters.
- Use K&R braces. Always use braces for loop and conditional bodies, including single-statement bodies.
- Put conditional bodies on lines separate from their conditions.
- Surround operators with spaces; add spaces after Java keywords, commas, and `for`-loop semicolons.
- Break wrapped lines after commas and before operators, including `.`, `&`, and `|`. Keep a method or constructor
  name attached to its opening parenthesis and prefer higher-level breaks.
- Separate logical units within a block with one blank line, but avoid extra blank lines that do not mark a logical
  boundary.
- Indent `case` labels one level inside `switch` and their statements one further level. Add `// Fallthrough` for
  intentional traditional-switch fall-through, or use a multi-label/arrow case to avoid fall-through.

## Declarations and statements

- Put every class in a package.
- List imports explicitly; do not use wildcard imports. Keep import ordering consistent: static imports first, then
  `java`, `javax`, third-party, and project imports, with blank lines between groups.
- Attach array brackets to the type, as in `int[] values`.
- Initialize variables where declared and declare them in the smallest practical scope. Do not use fake initial
  values merely to initialize a variable.
- Keep non-constant class fields non-public unless the class is deliberately a behavior-free data class.

## Comments and Javadoc

- Write comments in English using American spelling and avoid local slang.
- Add descriptive Javadoc to every class and public method. It may be omitted for getters/setters, test code, and
  overrides whose inherited documentation applies exactly.
- Start a Javadoc summary with a concise third-person verb such as `Returns`, `Creates`, or `Adds`.
- Put `/**` on its own line, align each `*`, leave one blank Javadoc line before block tags, punctuate tag
  descriptions, and place no blank line between the Javadoc and declaration.
- Include all `@param` tags or none. Omit them only when every parameter is self-explanatory and the tags add no
  value. Document non-obvious returns and thrown exceptions.
- Indent comments with the code they describe.

## Verify

Before finishing Java changes:

1. Search all affected Java files for tabs, wildcard imports, lines over 120 characters, unbraced control bodies,
   and traditional switch fall-through without `// Fallthrough`.
2. Recheck names, visibility, import grouping, Javadoc coverage, and American spelling by inspection.
3. Run the relevant Gradle checks using Java 25. For a repository-wide change, run `./gradlew test` (or
   `gradlew.bat test` on Windows).
