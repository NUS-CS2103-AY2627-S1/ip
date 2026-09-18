---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding standard to this project's production and test Java code.
metadata:
  short-description: Apply the project's Java coding standard
---

# SE-EDU Java Coding Standard

Apply this skill whenever creating, changing, refactoring, or reviewing Java source in this repository. Follow the SE-EDU [basic + intermediate Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html). For matters it does not cover, use the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).

## Required rules

- Use meaningful `PascalCase` names for classes, interfaces, and enums; meaningful `camelCase` names for methods and variables; and `UPPER_SNAKE_CASE` for constants. Name boolean values and methods with an `is`, `has`, `was`, `can`, or `should` prefix. Use plural names for collections; reserve `i`, `j`, and `k` for loop indices (nested loops use `j`, then `k`).
- Use four spaces for indentation, K&R braces, a soft 110-character line limit and a hard 120-character limit. Indent continuations eight spaces beyond the parent line; break after commas and before operators or dot separators when that improves readability.
- Put every class in a package. Keep imports explicit, minimal, and consistently ordered; do not use wildcard imports. Attach array brackets to the type.
- Declare variables in the smallest practical scope and initialise them at declaration when a valid initial value exists. Do not expose mutable public fields except in a behavior-free data class; constants are exempt.
- Put spaces around binary and ternary operators, after commas, and after reserved words. Separate logical units with one blank line.
- Always put loop and conditional bodies on separate lines enclosed in braces. Use an explicit `// Fallthrough` comment for intentional fall-through in traditional `switch` statements.
- Write all comments in English with American spelling. Add Javadoc headers for public classes and public methods, except simple getters/setters, test code, and overrides whose inherited documentation applies unchanged. Use a short third-person first sentence (for example, `Returns ...` or `Adds ...`), followed by useful `@param`, `@return`, and `@throws` tags with punctuation. Keep Javadoc aligned and do not put a blank line between it and its declaration.

## Before finishing

Review all changed Java files for the rules above, including test sources. Preserve existing behavior unless the task requests behavior changes, and use the project-required build and UI verification steps.
