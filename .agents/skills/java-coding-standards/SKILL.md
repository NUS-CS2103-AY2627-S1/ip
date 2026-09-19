---
name: java-coding-standards
description: Apply the SE-EDU Java coding standard when editing or reviewing Java source and tests in this project.
---

# Java Coding Standards

Follow the [SE-EDU Java coding standard (all rules)](https://se-education.org/guides/conventions/java/index.html).
Consult the live source when a relevant detail is not covered below or when the user requests a full conventions audit.

## Naming

- Use lowercase package names and English noun-based PascalCase names for classes and enums.
- Use English camelCase names for variables and verb-based camelCase names for methods.
- Do not capitalize an entire abbreviation or acronym when it forms part of a name, such as `exportHtmlSource`.
- Use SCREAMING_SNAKE_CASE for constants.
- Give associated constants a common prefix.
- Use longer variable names for larger scopes and short scratch names only for small, obvious scopes.
- Name booleans so they read as booleans, normally with prefixes such as `is`, `has`, `was`, `can`, or `should`.
- Use plural names for collections. Use `i`, then `j` and `k`, only as loop iterators where appropriate.
- Name tests using `featureUnderTest_testScenario_expectedBehavior` when underscores improve clarity.

## Layout and statements

- Indent with 4 spaces, not tabs. Indent wrapped lines by 8 additional spaces.
- Keep lines below 110 characters where practical and never exceed 120 characters.
- Wrap for readability: normally break after commas and before operators, keep a method name attached to its opening parenthesis, and prefer higher-level breaks.
- Use K&R braces. Always use braces for loop and conditional bodies, including one-line bodies.
- Keep conditionals and their bodies on separate lines.
- Put each class in an appropriate package when the assignment and current project structure permit it. Do not reorganize existing packages outside the requested increment.
- Group related classes into packages.
- Import classes explicitly, keep imports minimal, use a consistent ordering, and do not use wildcard imports.
- Organize class members as documentation, declaration, class variables by access, instance variables by access, constructors, then methods.
- Put an access modifier first in a method's modifier list.
- Attach array brackets to the type, such as `int[] values`.
- Initialize variables where declared when practical and declare them in the smallest useful scope.
- Keep fields non-public unless they are constants or belong to a behavior-free data class.
- Use `this` only when a field is shadowed by a parameter or local variable.
- Mark intentional switch fallthrough with `// Fallthrough`.
- Use spaces around operators, after Java keywords and commas, and after semicolons in `for` statements.
- Separate logical units within a block with a blank line.

## Comments and Javadocs

- Write comments in English using American spelling and indent them with the surrounding code.
- Write header comments for public classes and methods, except obvious getters/setters, exact overrides, and test code where the standard permits omission.
- Write header comments for non-trivial private methods.
- Follow the SE-EDU Javadoc layout: use a concise summary sentence, align `*` characters, separate descriptions from tags with a blank line, punctuate tag descriptions, and include all useful `@param` tags or none when names are self-explanatory.
- For uncovered topics, follow the Google Java Style Guide unless an assignment or repository rule says otherwise.

## Completion check

Before reporting a Java change complete:

1. Review changed code against the applicable conventions.
2. Run the Java 25 checks or tests required by `AGENTS.md`.
3. Confirm generated files such as `.class` files are not included in Git changes.
4. Report convention exceptions and test results.
