---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding rules when writing, changing, or reviewing Java code in this project.
---

# SE-EDU Java Coding Standard

Follow the basic and intermediate rules in the
[SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html).
Use the Google Java Style Guide only for topics the SE-EDU standard does not cover.

## Apply the standard

- Put every class in a suitable lower-case package and group related classes together.
- Use PascalCase for classes and enums, camelCase for methods and variables, and
  SCREAMING_SNAKE_CASE for constants. Method names must be verbs and boolean names
  should read as boolean expressions.
- Indent with four spaces, never tabs. Keep lines within 120 characters and aim
  for 110. Indent wrapped lines eight spaces beyond their parent line.
- Break wrapped expressions before operators and after commas when that improves
  readability.
- List imports explicitly and keep their ordering consistent. Separate static,
  Java, third-party, and project imports into logical groups.
- Declare variables in the smallest practical scope and initialize them where
  declared. Separate logical units with blank lines.
- Follow the standard layouts for conditionals, loops, switch statements, and
  try-catch statements. Include an explicit fallthrough comment when needed.
- Write English Javadoc header comments for public classes and methods. Obvious
  getters, setters, exact overrides, and test code may omit them.

## Verify changes

Inspect every changed Java file, check for lines over 120 characters, run
`gradlew test`, and run `gradlew javadoc` when production documentation changes.
Resolve violations and failures before committing.
