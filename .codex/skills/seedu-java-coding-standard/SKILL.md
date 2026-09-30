---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding standard when creating, editing, formatting, or reviewing Java code in this project.
---

# SE-EDU Java Coding Standard

Follow the [SE-EDU basic and intermediate Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html).
For topics it does not cover, follow the Google Java Style Guide.

## Apply the standard

- Put every class in a logical, all-lowercase package below its source root.
- Use PascalCase noun names for classes and enums, camelCase verb names for methods,
  camelCase names for variables, and SCREAMING_SNAKE_CASE for constants.
- Treat acronyms as words in names, use English, use American spelling, give
  boolean names a boolean meaning, and use plural names for collections.
- Use 4-space indentation with no tabs. Prefer lines below 110 characters and
  never exceed 120. Indent wrapped lines 8 spaces beyond the parent line,
  breaking after commas and before operators where practical.
- Use K&R braces. Always brace loop and conditional bodies, including
  single-statement bodies. Keep conditions and bodies on separate lines.
- Use consistent, explicit imports in this order: static imports, `java`,
  `javax`, third-party imports, then project imports; separate groups with one
  blank line. Do not use wildcard imports.
- Declare variables in the smallest practical scope and initialize them at
  declaration when a valid value is available. Keep non-constant class fields
  non-public.
- Separate logical units with one blank line and use spaces around operators,
  after commas, and after Java keywords.
- Write descriptive Javadocs for all classes and public methods, except where
  the standard explicitly permits omission. Start with a short third-person
  summary, keep `/**` on its own line for header comments, document all
  non-obvious parameters consistently, and punctuate tag descriptions.
- Use test method names in the form
  `featureUnderTest_testScenario_expectedBehavior`, omitting parts only when
  they add no value.

## Check changed code

Review all changed Java lines rather than mechanically reformatting unrelated
files. Before handing off:

1. Check changed Java files for tabs, wildcard imports, non-American wording,
   and lines over 120 characters.
2. Run the relevant Gradle compilation, tests, and Javadoc or style tasks
   available in the project.
3. Preserve program behavior unless the user requested a behavior change.
