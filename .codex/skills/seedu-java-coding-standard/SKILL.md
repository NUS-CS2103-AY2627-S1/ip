---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding standard when creating, modifying, reviewing, or refactoring Java code in this project.
---

# SE-EDU Java Coding Standard

Apply these rules to all production and test Java code in this repository. Treat them as required unless the user explicitly requests a conflicting convention. For topics not covered here, follow the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).

The authoritative project standard is the [SE-EDU Java coding standard (basic + intermediate)](https://se-education.org/guides/conventions/java/intermediate.html). This skill paraphrases its rules as a working checklist.

## Naming

- Use all-lowercase package names rooted in the project or group name, followed by logical package names.
- Name classes and enums with English nouns in PascalCase.
- Name variables in camelCase and methods with English verbs in camelCase.
- Name constants in SCREAMING_SNAKE_CASE. Give related constants a common prefix.
- In names, write acronyms as ordinary words, such as `exportHtmlSource`, not `exportHTMLSource`.
- Name boolean values and methods like predicates, preferably using prefixes such as `is`, `has`, `was`, `can`, or `should`. Name a boolean setter parameter in the same style, such as `setFound(boolean isFound)`.
- Use plural names for collections and arrays.
- Match a variable's descriptiveness to its scope. Reserve short scratch names such as `i` for a few nearby lines; use `j`, `k`, and later letters for nested loops.
- Test method names may use `featureUnderTest_testScenario_expectedBehavior`, omitting later parts when they add no information.

## Layout

- Indent with four spaces, never tabs. Use K&R braces: put the opening brace at the end of the declaration or control-statement line.
- Keep lines below the 110-character soft limit and never exceed 120 characters.
- Indent continuation lines eight spaces beyond their parent line. Break after commas and before operators, including `.`, `&` in type bounds, and `|` in multi-catch clauses. Prefer a higher-level break and keep a method or constructor name attached to its opening parenthesis.
- Surround binary and ternary operators with spaces. Put a space after Java keywords, commas, and `for`-loop semicolons.
- Use braces and separate lines for every loop and conditional body, including a one-statement body.
- Indent `case` and `default` labels inside their `switch`. Add `// Fallthrough` when a colon-style case intentionally continues into the next case. Arrow-style cases do not need this comment.
- Separate logical units within a block with one blank line.

## Declarations and statements

- Put every class in a package.
- Keep import ordering consistent, separate coherent import groups with blank lines, import classes explicitly, and never use wildcard imports.
- Attach array brackets to the type, such as `int[] values`.
- Declare variables in the smallest useful scope and initialize them at declaration when a valid initial value exists.
- Do not expose class variables publicly unless the class is a behavior-free data class. Public constants are allowed.

## Comments and Javadoc

- Write comments in English using American spelling, without local slang. Align comments with the code they describe.
- Add descriptive Javadoc to every class and public method. Javadoc may be omitted for getters and setters, tests, and overrides whose inherited documentation applies unchanged.
- Begin a Javadoc summary with a concise present-tense verb such as `Returns`, `Adds`, or `Sends`. Put `/**` on its own line for block Javadocs, align each `*`, and leave no blank line between the comment and declaration.
- When tag details add value, separate them from the description with one blank Javadoc line. Either document every parameter or omit all `@param` tags. End parameter, return, and exception descriptions with punctuation.
- Use `{@inheritDoc}` when an override needs inherited documentation plus a behavior-specific addition.

## Before finishing Java work

Review every changed Java file against the checklist above. Fix violations in the touched code, check nearby code when the task is a standards audit, and run the repository's required unit and UI tests.
