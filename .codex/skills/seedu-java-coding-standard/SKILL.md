---
name: seedu-java-coding-standard
description: Apply the SE-EDU intermediate Java coding standard to all production and test Java code in this project.
---

# SE-EDU Java coding standard

Use this skill for every Java addition or modification in this repository.

## Naming and packages

- Keep every class in a lower-case, logically named package rooted at `alexa`.
- Name classes and enums with English PascalCase nouns; name variables and methods with English camelCase names. Method names must be verbs.
- Use uppercase underscore-separated names only for constants. Use readable lower-case acronyms inside names, such as `html` and `dvd`.
- Give boolean variables and methods an `is`, `has`, `was`, or similarly boolean-sounding prefix. Use plural names for collections.
- Test methods may use `featureUnderTest_testScenario_expectedBehavior` names.

## Layout and statements

- Indent each block with four spaces; do not use tabs. Use K&R braces, including for one-statement loops and conditionals.
- Keep lines within 120 characters, aiming for 110 where practical. Wrap for readability: break after commas and before operators, and indent continuations eight spaces beyond their parent line.
- Use spaces around operators, after commas, and after Java keywords. Separate logical units with one blank line.
- Keep imports explicit, minimal, and consistently ordered: static imports, Java imports, then third-party imports, separated into groups when applicable. Never use wildcard imports.
- Declare and initialize variables in the smallest practical scope. Keep state private unless a public constant or a behavior-free data class genuinely requires otherwise.

## Documentation

- Write English, American-spelled comments.
- Give every public class and public method a descriptive Javadoc header, except simple getters/setters, exact overrides, and test code.
- Start a method summary with a third-person verb such as `Returns`, `Adds`, or `Creates`. Put periods after Javadoc parameter, return-value, and exception descriptions.

For topics not covered above, follow the Google Java Style Guide, as directed by the SE-EDU guide.