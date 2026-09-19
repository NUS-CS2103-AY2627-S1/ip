---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding standard when creating, changing, or reviewing Java code in this project.
---

# SE-EDU Java Coding Standard

Follow the [SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html) for every Java code change and review in this project. Use the Google Java Style Guide only for topics the SE-EDU standard does not cover.

## Review checklist

- Use lowercase package names, PascalCase noun names for classes and enums, camelCase verb names for methods, camelCase variable names, and SCREAMING_SNAKE_CASE constants.
- Give boolean variables and methods boolean-sounding names, collection variables plural names, and all identifiers English names.
- Indent with four spaces, keep lines within 120 characters and preferably within 110, wrap continuation lines by eight spaces, and use K&R braces.
- Break wrapped expressions after commas or before operators. Keep logical units separated by a blank line.
- Put every class in a package. List imports explicitly and keep their ordering consistent.
- Declare variables in the smallest practical scope and initialize them where declared. Keep mutable fields non-public and as private as practical.
- Always use braces for loop and conditional bodies, with the condition and body on separate lines.
- Indent switch case labels one level inside the switch. End colon-style cases with a break, return, throw, or an explicit `// Fallthrough` comment.
- Write English Javadocs for every public class and public method, except getters, setters, overrides whose inherited documentation applies, and test code. Start method summaries with a third-person verb such as `Returns`, `Adds`, or `Runs`.

Before finishing, inspect every changed Java file against this checklist. Fix violations in the changed scope and report any deliberately deferred violation.
