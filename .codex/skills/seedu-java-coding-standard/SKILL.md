---
name: seedu-java-coding-standard
description: Apply the required SE-EDU basic and intermediate Java coding conventions when writing, editing, formatting, or reviewing Java code in this project.
---

# SE-EDU Java Coding Standard

Follow the [SE-EDU basic and intermediate Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html). Use Google Java Style only for topics that the SE-EDU standard does not cover.

## Required conventions

- Use lowercase package names, PascalCase noun names for classes and enums, camelCase verb names for methods, camelCase variable names, and SCREAMING_SNAKE_CASE constants.
- Name booleans so they read as conditions, normally with `is`, `has`, `can`, `should`, or a similar prefix. Use plural names for collections.
- Indent with four spaces, use K&R braces, and keep lines within 120 characters; aim for 110 characters where practical. Indent wrapped lines eight spaces beyond their parent.
- Group imports consistently: static imports, `java`, `javax`, third-party libraries, and project imports, with a blank line between groups. Use explicit imports and remove unused imports.
- Put every class in a package. Declare variables in the smallest practical scope and initialize them where declared.
- Use spaces around operators and after commas and Java keywords. Separate logical units with blank lines.
- Indent `case` labels one level inside `switch` blocks and indent their bodies one further level.
- Keep fields private unless subclass access is required. Prefer immutable fields when their values do not change.
- Write comments that explain intent or rationale; do not narrate obvious code. Use Javadoc for public APIs and non-trivial methods where it improves understanding.

## Review workflow

1. Inspect every changed Java file, including tests.
2. Correct violations without changing behavior unless the task requires a behavioral change.
3. Check for lines longer than 120 characters and inconsistent imports.
4. Run the relevant Gradle and UI tests before handing off the change.
