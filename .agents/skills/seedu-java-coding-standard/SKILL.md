---
name: seedu-java-coding-standard
description: Apply the SE-EDUCATION Java basic and intermediate coding conventions to Java source and test code in this project.
---

# Seedu Java Coding Standard

Apply these rules to every Java change in this project. The authoritative source is
the [SE-EDUCATION Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html);
use the Google Java Style Guide for topics not covered there.

## Naming

- Use lowercase package names and use the project name as the root package.
- Name classes and enums as PascalCase nouns.
- Name variables in camelCase and methods as camelCase verbs.
- Name constants in SCREAMING_SNAKE_CASE, with a shared prefix for related constants.
- Use English names, avoid uppercase abbreviations/acronyms, and use plural names for collections.
- Name booleans so they read as predicates (`is`, `has`, `was`, or `can`); boolean setters use
  `setName(boolean isName)`.
- Use short `i`, `j`, `k` only for small-scope iterator variables and nested loops.
- Test method names may use `featureUnderTest_testScenario_expectedBehavior`.

## Layout and statements

- Indent with four spaces, never tabs. Keep lines at most 120 characters and prefer under 110.
- For wrapped lines, indent continuation lines by eight spaces beyond the parent line; break after
  commas, before operators, and at higher-level expression boundaries where readability improves.
- Use K&R braces. Always brace methods, conditionals, loops, and try/catch blocks, including
  single-statement bodies. Keep `else` and `catch` on the closing brace line.
- Put spaces around operators, after reserved words and commas, and around binary/ternary colons.
- Separate logical units in a block with one blank line.
- Keep switch cases explicit; use `// Fallthrough` for intentional fallthrough.

## Packages, imports, types, and variables

- Put every class in a package.
- Keep imports consistently ordered, explicit, and minimal; never use wildcard imports.
- Attach array brackets to the type (`int[] values`).
- Initialize variables at declaration when a valid initial value is available, and keep them in the
  smallest possible scope.
- Do not expose class fields publicly, except constants and behaviorless data-class fields.

## Comments

- Write comments in English using American spelling and no local slang.
- Add descriptive Javadoc to every public class and public method, except getters/setters,
  exact overrides covered by inherited Javadoc, and test code. Add Javadoc to non-obvious members.
- Start a Javadoc summary with a present-tense verb such as `Returns`, `Adds`, or `Sends`.
- Put `/**` on its own line, align `*`, leave a blank line before tags, punctuate tag descriptions,
  and place no blank line between the Javadoc and its declaration.
- Indent comments with the code they describe.
