---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding conventions to Java code in this project.
---

# SE-EDU Java Coding Standard

Apply these rules to every new or modified Java file in this project. The
authoritative source is the [SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html).
Use the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
for topics not covered by SE-EDU.

## Required conventions

- Put every class in a lower-case package named for the project or a logical
  subcomponent. Keep the directory structure consistent with the package.
- Name classes and enums as nouns in `PascalCase`; methods and variables in
  `camelCase`; constants in `SCREAMING_SNAKE_CASE`.
- Use normal mixed case for abbreviations, English and American spelling, and
  boolean names that read like predicates (`isDone`, `hasData`). Use plural
  names for collections.
- Use four spaces, never tabs; K&R braces; explicit imports; and arrays with
  brackets attached to the type. Keep lines at most 120 characters, with a
  soft target below 110, and indent wrapped continuations by eight spaces.
- Separate logical units with one blank line. Initialize variables at their
  declarations and keep them in the smallest practical scope.
- Keep fields non-public to preserve encapsulation. Always use braces around
  loop and conditional bodies, including single-statement bodies.
- Write descriptive Javadoc for all classes and public methods. A method
  summary should begin with a concise verb such as `Returns`, `Adds`, or
  `Sends`; add useful `@param`, `@return`, and `@throws` tags and punctuate
  their descriptions. Getters/setters, exact overrides, and test code may use
  the standard exceptions.

## Review before finishing

Inspect changed Java files for naming, package, layout, visibility, brace,
import, line-length, and Javadoc violations. Prefer the smallest change that
brings the touched code into compliance, and preserve existing behavior.
