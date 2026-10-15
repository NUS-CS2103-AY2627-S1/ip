---
name: seedu-java-coding-standard
description: "Use when creating, reviewing, editing, or refactoring Java code in this project. Applies the SE-EDU intermediate Java coding standard for naming, layout, statements, and Javadoc."
---

# SE-EDU Java Coding Standard

Follow the [SE-EDU Java coding standard (basic + intermediate)](https://se-education.org/guides/conventions/java/intermediate.html) for every Java change in this repository. Use the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html) only for topics not covered by SE-EDU.

## Naming

- Use lowercase package names and PascalCase noun names for classes and enums.
- Use camelCase for variables and verb names for methods. Keep acronyms lowercase when embedded in a name, such as `exportHtmlSource`.
- Use SCREAMING_SNAKE_CASE for constants and a shared prefix for associated constants.
- Name booleans with `is`, `has`, `was`, `can`, or `should`; use setters such as `setFound(boolean isFound)`.
- Use plural names for collections. Keep names English and make broad-scope variables descriptive; short iterator names are permitted only in a small loop scope.
- Name test methods `featureUnderTest_testScenario_expectedBehavior()`. Omit trailing parts only when they add no clarity.

## Layout and Statements

- Indent with four spaces, never tabs. Keep lines at or below 120 characters, aiming for 110 where practical.
- Wrap continuations with eight additional spaces from the parent indent. Break after commas and before operators or chained dots, while keeping a method name with its opening parenthesis.
- Use K&R braces. Put `else`, `catch`, and `finally` on the same line as the preceding closing brace.
- Use spaces around binary and ternary operators, after keywords, and after commas and `for` semicolons.
- Separate logical units in a block with one blank line.
- Put each class in a package; use explicit imports only, organized consistently, with Java imports before project imports.
- Attach array brackets to the type. Declare variables in the smallest possible scope and initialize them at declaration when a valid initial value exists.
- Do not expose mutable fields publicly. Always use braces for loop and conditional bodies, including single statements. Mark intentional switch fallthrough with `// Fallthrough`.

## Comments and Javadoc

- Write all comments in American English, without local slang.
- Add descriptive Javadoc headers to every public class and public method. Getters, setters, test code, and overrides may omit them when their behavior is self-evident or inherited unchanged.
- Start Javadoc summaries with a third-person verb such as `Returns`, `Adds`, or `Parses`. Add a blank line before tags, end tag descriptions with punctuation, and keep Javadoc directly adjacent to its declaration.
- Explain non-obvious fields or algorithmic blocks with concise comments; do not narrate self-evident code.

## Review Checklist

Before finishing a Java change, check affected source and tests for naming, Javadoc, import ordering, brace usage, line length, field visibility, and test-method names. Run the narrowest relevant Gradle test or compilation task.
