---
name: seedu-java-coding-standard
description: Apply and review the SE-EDU basic and intermediate Java coding standard for every Java code change in this project.
---

# SE-EDU Java Coding Standard

Apply the [SE-EDU basic and intermediate Java rules](https://se-education.org/guides/conventions/java/intermediate.html) to all production and test Java code in this repository. Use the Google Java Style Guide only for topics the SE-EDU standard does not cover.

## Required checks

- Use lowercase package names, PascalCase noun class and enum names, camelCase verb method names, camelCase variables, and SCREAMING_SNAKE_CASE constants. Keep acronyms mixed-case inside identifiers, boolean names predicate-like, and collection names plural.
- Indent with four spaces and continuation lines with eight additional spaces. Aim for at most 110 characters and never exceed 120. Use K&R braces and readable high-level line breaks.
- Use conventional whitespace, one statement per line, braces around every loop and conditional body, and blank lines between logical units.
- Put every class in a package. Keep imports explicit, minimal, consistently ordered, and free of wildcards.
- Attach array brackets to the type. Initialize variables at declaration when a valid initial value exists and keep their scope as small as possible. Do not expose mutable class variables publicly.
- Write comments in clear American English and align them with the code they describe. Remove temporary or change-history comments.
- Add descriptive Javadoc to every public class and public method except getters/setters, exact overrides, and test code. Start method summaries with a third-person verb such as `Returns`, `Adds`, or `Saves`; document non-obvious parameters, return values, and exceptions.

Before finishing a Java change, inspect every touched Java file against these checks. Do not reformat unrelated code unless the task is specifically a standards audit or cleanup.
