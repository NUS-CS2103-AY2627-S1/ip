---
name: seedu-java-coding-standard
description: Apply and review the SE-EDU basic and intermediate Java coding standard for Java code in this project. Use whenever creating, editing, refactoring, or reviewing Java source or tests in this repository.
---

# SE-EDU Java Coding Standard

Follow the [SE-EDU basic and intermediate Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html). Use the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html) only for topics the SE-EDU standard does not cover. Preserve existing behavior unless the task requires a behavior change.

## Required rules

- Use lowercase package names. Use nouns in PascalCase for classes and enums, verbs in camelCase for methods, camelCase for variables, and SCREAMING_SNAKE_CASE for constants. Keep names in English, write acronyms as words inside names, make collection names plural, and make boolean names read as predicates (for example, `isDone` or `hasTasks`). Test methods may use `featureUnderTest_testScenario_expectedBehavior`.
- Indent with four spaces and never tabs. Aim for at most 110 characters per line and never exceed 120. Indent wrapped lines eight spaces beyond their parent line, break after commas or before operators, and keep a method name attached to its opening parenthesis.
- Use K&R braces and always brace loop and conditional bodies. Put conditional bodies on separate lines. Add `// Fallthrough` for an intentional colon-style switch fall-through.
- Put every class in a package. Use explicit imports, keep imports minimal, and order them consistently: static imports, `java`/`javax`, third-party libraries, then project imports, with blank lines between groups.
- Attach array brackets to the type. Initialize variables where declared when a valid value is available, and declare them in the smallest useful scope. Do not expose mutable class variables publicly unless the class is intentionally a behavior-free data class.
- Surround operators with spaces, put spaces after keywords, commas, and `for` semicolons, and separate logical units in a block with one blank line.
- Write comments in English using American spelling. Add descriptive Javadoc to every production class and public method, except getters/setters and overrides whose inherited documentation applies exactly. Test classes and test methods are exempt. Start method Javadoc summaries with a third-person verb such as “Returns”, “Adds”, or “Creates”; use complete punctuation in tag descriptions.

## Workflow

When Java code is changed or reviewed, inspect all affected Java files against these rules, correct violations within the requested scope, update valuable JUnit tests if behavior changes, and run the relevant Java 25 checks required by `AGENTS.md`.
