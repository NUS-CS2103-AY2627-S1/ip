---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding standard when writing, editing, or reviewing Java code in this project.
---

# SE-EDU Java Coding Standard

Keep Java code consistent with the SE-EDU basic and intermediate standard:
https://se-education.org/guides/conventions/java/intermediate.html

Use the Google Java Style Guide only for topics the SE-EDU standard does not
cover. Project requirements and explicit user instructions take precedence.

## Apply the standard

Before writing or reviewing Java, read [references/rules.md](references/rules.md)
completely and apply every relevant rule to production and test code.

For an audit:

1. Inspect all Java files in scope, including tests.
2. Check naming, layout, imports, declarations, control flow, and comments.
3. Correct violations without changing behavior unless the user authorizes a
   behavior or design change.
4. Run `gradlew test` and `gradlew javadoc` after changes. Follow the project's
   console UI testing instructions whenever `src/main/java` changes.
5. Report material corrections and any judgment calls that remain.

Do not add comments that merely restate code. Test classes and test methods do
not require Javadocs, but their names and formatting still follow the standard.
