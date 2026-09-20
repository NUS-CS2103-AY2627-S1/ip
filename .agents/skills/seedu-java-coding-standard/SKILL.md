---
name: seedu-java-coding-standard
description: Enforce the SE-EDU basic and intermediate Java coding standard whenever creating, editing, refactoring, or reviewing Java code in this project.
---

# SE-EDU Java Coding Standard

Follow the [SE-EDU basic and intermediate Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html) for every Java change. Use Google Java Style only for topics the SE-EDU standard does not cover.

## Required workflow

1. Inspect the surrounding package and code before editing.
2. Apply every relevant rule in the checklist below to production and test code.
3. Correct safe, directly related violations in code touched by the change.
4. Run the verification checks before handing off the change.

## Naming and organization

- Use lowercase package names and place every class in a package.
- Use `PascalCase` nouns for classes and enums.
- Use `camelCase` verbs for methods and `camelCase` nouns for variables.
- Use `SCREAMING_SNAKE_CASE` for constants.
- Treat acronyms as words, for example `XmlParser`, not `XMLParser`.
- Give booleans `is`, `has`, or `was` names where suitable, and use plural names for collections.
- Use explicit imports and keep import ordering consistent throughout the project.
- Attach array brackets to the type, for example `String[] arguments`.

## Layout

- Indent with four spaces; never use tabs.
- Keep lines at or below the 120-character hard limit and aim for 110 characters.
- Indent continuation lines by eight spaces.
- Break after commas and before operators when wrapping expressions.
- Use K&R braces and put braces around every loop and conditional body.
- Indent `case` and `default` labels inside their `switch` block.
- Separate logical units with blank lines, without adding decorative whitespace.

## Code structure

- Declare and initialize variables at the smallest practical scope.
- Do not expose public fields except constants or intentional data objects.
- Put separate conditional branches on separate lines.
- Mark intentional `switch` fall-through with a comment.
- Prefer straightforward control flow and self-explanatory code.

## Comments and documentation

- Write clear English comments using American spelling and no slang.
- Add Javadoc to public classes and public methods, except obvious getters, setters, valid overrides, and test methods.
- Add comments only when they explain purpose, rationale, or non-obvious behavior.

## Verification

Run these checks after Java changes:

```bash
rg -n $'\t' src/main/java src/test/java
rg -n '.{121}' src/main/java src/test/java
./gradlew checkstyleMain checkstyleTest
./gradlew test
```

Run `./gradlew javadoc` after changing public APIs or documentation. Run the project `/test-ui` skill when user-visible command behavior could be affected. Fix any reported standard violation or test failure before handing off.
