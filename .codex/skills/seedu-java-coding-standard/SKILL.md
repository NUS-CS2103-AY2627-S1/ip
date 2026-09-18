---
name: seedu-java-coding-standard
description: Follow the SE-EDU Java coding standard for this CS2103/IP Java project whenever creating, editing, reviewing, or explaining Java code.
---

# SE-EDU Java Coding Standard

Use this skill for all Java code changes in this repository.

## Source

Based on the SE-EDU Java coding standard, basic + intermediate:
https://se-education.org/guides/conventions/java/intermediate.html

Use the Google Java Style Guide for topics not covered by the SE-EDU rules.

## Checklist

- Put every class in a package.
- Use package names in all lowercase, rooted at the project name when practical.
- Use `PascalCase` for classes and enums.
- Use `camelCase` for variables and methods.
- Use `SCREAMING_SNAKE_CASE` for constants.
- Name boolean variables and methods so they read as booleans, preferably with prefixes such as `is`, `has`, `can`, or `should`.
- Use plural names for collections.
- Keep imports explicit; do not use wildcard imports.
- Keep import ordering consistent: static imports first, then Java/JDK imports, then third-party imports, then project imports, with blank lines between groups.
- Use 4 spaces for indentation, not tabs.
- Keep lines under 120 characters, and aim for 110 characters or shorter.
- Wrap long lines for readability, using 8 spaces for continuation indentation.
- Use K&R braces: opening braces stay on the same line.
- Always use braces for loop and conditional bodies.
- Put conditionals on their own lines.
- Surround operators with spaces, put a space after Java keywords, and put a space after commas.
- Declare variables in the smallest reasonable scope and initialize them where they are declared.
- Avoid public fields except constants or simple data classes with no behavior.
- Separate logical units within a block using blank lines.
- Write comments in English, using American spelling and avoiding local slang.
- Write descriptive Javadocs for all public classes and methods, except simple getters/setters, inherited overrides where the parent Javadoc fully applies, and test-only code.
- Add Javadocs for non-trivial private helpers when they explain intent or contract better than the code alone.

## Javadocs

- Start Javadocs with `/**` on its own line.
- Start method summaries with verbs such as `Returns`, `Creates`, `Parses`, or `Saves`.
- Keep `*` alignment consistent.
- Include a blank line between the description and tags.
- Use punctuation at the end of `@param`, `@return`, and `@throws` descriptions.
- Either document all parameters with `@param` tags or omit all parameter tags when every parameter is self-explanatory.
