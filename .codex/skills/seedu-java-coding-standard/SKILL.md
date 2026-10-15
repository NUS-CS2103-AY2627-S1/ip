---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding conventions to Java code in this project.
---

# SE-EDU Java coding standard

Apply this skill to every Java source change in this project, including production
code and tests. The authoritative guide is:
https://se-education.org/guides/conventions/java/intermediate.html

Use the following rules when writing or reviewing code:

- Put every class in a lowercase package. Use PascalCase nouns for classes and
  enums, camelCase for variables and verb methods, and SCREAMING_SNAKE_CASE for
  constants. Use English names, spell acronyms in normal mixed case (`Html`,
  `Dvd`), use boolean-sounding names such as `isOpen` or `hasData`, and use
  plural names for collections.
- Use four spaces for indentation, K&R braces, spaces around operators and
  after commas, and a blank line between logical units. Keep lines at or below
  120 characters where possible, wrapping with an additional eight spaces of
  indentation. Keep method and constructor names attached to their opening
  parenthesis.
- Keep `if`, loop, switch, and try-catch formatting explicit and readable. Put
  braces around every conditional and loop body, including single statements.
  Add `// Fallthrough` for intentional fall-through cases. Declare and
  initialize variables in the smallest scope that is practical.
- Order imports consistently, list imported classes explicitly, and attach array
  brackets to the type (`String[]`, not `String []` or `String []`-style variable
  declarations). Do not expose class variables publicly unless the class is a
  behavior-free data class; constants are exempt.
- Write English comments using American spelling. Add descriptive Javadoc to
  every public class and public method, except getters/setters, overrides when
  the inherited Javadoc applies, and test classes/methods. Start method
  summaries with a clear present-tense verb such as `Returns`, `Creates`, or
  `Displays`; use correctly formatted `@param`, `@return`, and `@throws` tags
  when they add useful information.

When the SE-EDU guide does not cover a topic, follow the Google Java Style Guide
linked by that guide.
