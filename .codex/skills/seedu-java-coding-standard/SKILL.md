---
name: seedu-java-coding-standard
description: Apply the SE-EDU intermediate Java coding standard when creating, editing, or reviewing Java code in this project.
---

# SE-EDU Java coding standard

Use this skill for every Java source or test change in this repository. Apply the [SE-EDU intermediate Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html); use the Google Java Style Guide for topics that standard does not cover.

## Required rules

- Put each class in a lowercase project package. Use PascalCase nouns for types, camelCase verbs for methods, camelCase for variables, and SCREAMING_SNAKE_CASE for constants.
- Name boolean variables and methods with a boolean prefix such as `is`, `has`, `was`, `can`, or `should`. Use plural names for collections. Test method names may use `featureUnderTest_testScenario_expectedBehavior`.
- Use four-space indentation, K&R braces, explicit imports, braces around every loop and conditional body, and a blank line between logical units. Keep lines at 120 characters or fewer; prefer 110 or fewer.
- Keep declarations in the smallest practical scope and initialize them where declared. Do not expose mutable class variables publicly.
- Write all comments in English with American spelling. Document all public classes and public methods unless they are a getter/setter, a test method, or an override whose inherited Javadoc applies unchanged. Document non-trivial private methods when their purpose is not obvious.
- Use standard multi-line Javadocs: place `/**` on its own line, begin with a short verb-led summary, and separate the description from tags with a blank line. When tags are helpful, document every parameter, return value, and thrown exception; end tag descriptions with punctuation.

## Before finishing

Review each changed Java file for the rules above. Run the relevant Gradle checks and inspect the diff for formatting issues.
