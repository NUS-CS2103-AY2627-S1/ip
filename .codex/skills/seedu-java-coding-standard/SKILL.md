---
name: seedu-java-coding-standard
description: Mandates the SE-EDU Java coding standard (basic + intermediate) for all code written in this project. Follow it for every Java change and when reviewing code.
---

# Java Coding Standard (SE-EDU basic + intermediate)

Authoritative reference: <https://se-education.org/guides/conventions/java/intermediate.html>
Use the Google Java style guide for anything not covered here.

## Naming

* Packages: all lowercase (e.g. `ruby.command`).
* Classes/enums: nouns in PascalCase (e.g. `TodoCommand`).
* Variables: camelCase. Constants: SCREAMING_SNAKE_CASE.
* Methods: verbs in camelCase.
* Boolean variables/methods sound like booleans (e.g. `isDone`, `hasNextCommand`).
* Test method names use `featureUnderTest_testScenario_expectedBehavior()`.
* Collections use plural names (`tasks`, `matches`); no uppercase acronyms in names.
* Scratch indices in tight loops may be `i`, `j`, `k`.

## Layout

* Indent with 4 spaces, never tabs.
* Max line length 120 chars (soft limit 110); wrap with 8-space continuation indent.
* Break after commas, before operators, and never between a method name and its `(`.
* K&R (Egyptian) braces: opening brace on the same line as the statement.

## Statements

* Explicit imports, ordered consistently (static imports first, then `java.*`,
  then third-party, then project packages). No wildcard imports.
* Array specifiers attach to the type: `int[] values`.
* Declare and initialize variables in the smallest scope; never public fields.
* Always wrap loop and conditional bodies in braces, even single statements.
* Put the condition on its own line (`if (isDone) {`); reserve words followed
  by a space; operators and commas surrounded by spaces.
* Separate logical blocks with a blank line.

## Comments

* All comments in English (US spelling).
* Javadoc headers for all classes and public methods, with `@param`, `@return`,
  and `@throws` where meaningful. Omit only for getters/setters, `@Override`
  methods whose parent Javadoc still applies, and test code.
* First sentence of a method summary starts like `Returns ...`, `Adds ...`.
* `@return`/`@param` are included for all parameters, or none.
