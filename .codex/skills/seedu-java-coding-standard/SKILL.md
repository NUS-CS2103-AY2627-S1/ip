---
name: seedu-java-coding-standard
description: Apply the SE-EDU Java coding standard, basic plus intermediate rules, to Java code in this project. Use for Java code edits, code reviews, formatting passes, Javadoc/header comments, naming, imports, layout, braces, and test method naming in this repository.
---

# SE-EDU Java Coding Standard

Apply the SE-EDU Java coding standard for this project:
https://se-education.org/guides/conventions/java/intermediate.html

Use this skill whenever creating, editing, or reviewing Java source or test code in this repository.

## Core Checklist

- Use package names in lower case; keep every class in a package.
- Use `PascalCase` for classes and enums, `camelCase` for methods and variables, and `SCREAMING_SNAKE_CASE` for constants.
- Name boolean variables and methods with boolean-sounding prefixes such as `is`, `has`, `was`, `can`, or `should`.
- Use plural names for collections.
- Keep imports explicit; do not use wildcard imports.
- Initialize variables where they are declared and keep declarations in the smallest reasonable scope.
- Use 4 spaces for indentation and K&R braces.
- Keep lines under 120 characters, preferably under 110 where readable.
- Wrap long lines for readability, with wrapped lines indented 8 spaces more than the parent line.
- Use braces for all `if`, `else`, `for`, `while`, and `do-while` bodies.
- Indent `case` labels inside `switch` blocks and include `// Fallthrough` for intentional fall-through.
- Keep comments in English, use American spelling, and indent comments with surrounding code.

## Javadoc/Header Comments

- Write descriptive Javadoc header comments for all production classes and all non-private production methods.
- Also document non-trivial private production methods.
- Getter/setter Javadocs may be omitted only when they add no value.
- Overridden method Javadocs may be omitted only when the parent Javadoc applies exactly; otherwise use `{@inheritDoc}` or write a specific comment.
- Test classes and test methods may omit Javadoc comments.
- Use standard Javadoc blocks:

```java
/**
 * Returns the parsed task number.
 *
 * @param command User command text.
 * @return Parsed task number.
 * @throws DukeException If the command does not contain a valid task number.
 */
```

- Start method summaries with a verb such as `Returns`, `Adds`, `Parses`, `Saves`, or `Loads`.
- Include `@param`, `@return`, and `@throws` only when they add useful information; if using `@param`, document all parameters.

## Tests

- JUnit test method names may use `featureUnderTest_testScenario_expectedBehavior()`.
- Keep tests readable and explicit; avoid testing trivial getters/setters unless they protect important behavior.
