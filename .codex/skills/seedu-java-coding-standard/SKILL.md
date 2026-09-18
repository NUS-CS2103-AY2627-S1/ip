---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding standard to Java code in this project.
---

# SE-EDU Java coding standard

Apply every rule below to Java source and tests in this project. The canonical source is the [SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html); use the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html) for topics not covered here.

## Naming

- Use lowercase package names; use the project/group name as the root package rather than `edu.nus.comp`.
- Name classes and enums as PascalCase nouns.
- Name variables in camelCase, methods as camelCase verbs, and constants in SCREAMING_SNAKE_CASE.
- Test methods may use `featureUnderTest_testScenario_expectedBehavior`.
- Keep abbreviations and acronyms mixed case rather than all uppercase in names, and write all names in English.
- Use longer names for larger scopes and short names only for nearby scratch variables or indices; use `i`, `j`, `k` only for iterators/nested loops.
- Give booleans names that read as booleans, preferably with `is`, `has`, `was`, or similar prefixes. Boolean setters use `setFound(boolean isFound)` form.
- Use plural names for collections and common prefixes for associated constants.

## Layout and whitespace

- Use four spaces for indentation, never tabs.
- Keep lines at or below 120 characters, aiming for below 110; wrap with eight-space continuation indentation.
- For wrapped lines, break after commas and before operators/operator-like dots, ampersands, and catch pipes; keep method names attached to `(` and prefer higher-level breaks. Use one of the documented readable ternary layouts.
- Use K&R braces. Format method definitions, if/else, loops, switch, and try/catch/finally in the documented brace layout.
- Mark intentional switch fall-through with `// Fallthrough`.
- Put spaces around operators, after reserved words, after commas, around binary/ternary colons, and after `for` semicolons.
- Separate logical units in a block with one blank line.

## Statements and declarations

- Put every class in a package.
- Keep import ordering consistent and list imported classes explicitly; never use wildcard imports and keep imports minimal.
- Attach array brackets to the type (`int[] values`).
- Initialize variables at declaration when possible and declare them in the smallest possible scope.
- Do not declare class variables public, except constants or behavior-free data classes.
- Always wrap loop bodies and conditional bodies in braces, including single-statement bodies; put conditional bodies on separate lines.

## Comments and Javadoc

- Write all comments in English, use American spelling, and avoid local slang.
- Add descriptive, succinct header Javadocs to every class and public method; in this project also add them to non-private methods and non-trivial private methods. Getters/setters, overriding methods whose inherited documentation applies exactly, and test code may omit them under the source standard, but project requests take precedence.
- Start method summaries with `Returns`, `Sends`, `Adds`, or another third-person verb form. Use an opening `/**` on its own line, aligned `*` markers, a blank line before tags, punctuation in parameter descriptions, and no blank line before the declaration. Add `@param`, `@return`, and `@throws` when they add useful information.
- Indent comments with their code and keep comments descriptive rather than narrating obvious syntax.

When changing code, review the complete file against this checklist, preserve behavior unless the standard requires a correction, and run the project verification workflow.
