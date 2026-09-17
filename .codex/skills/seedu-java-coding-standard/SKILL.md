---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding standard to all Java production and test code in this project when creating, editing, or reviewing it.
---

# SE-EDU Java Coding Standard

Apply this skill to all Java production code and Java tests in this repository. Use the
[SE-EDU Java coding standard (basic + intermediate)](https://se-education.org/guides/conventions/java/intermediate.html)
as the authority. For topics it does not cover, follow the Google Java Style Guide as
the SE-EDU guide directs.

## Required conventions

- Put every class in a package. Use English names: lowercase packages, PascalCase nouns for
  classes/enums, camelCase variables and verb methods, and SCREAMING_SNAKE_CASE constants.
  Do not uppercase acronyms inside names. Name booleans to sound like booleans (`is`, `has`,
  `was`, `can`, or similar), use `setFound(boolean isFound)`-style boolean setters, and use
  plural names for collections. Test methods may use
  `featureUnderTest_testScenario_expectedBehavior()`.
- Indent with four spaces, never tabs. Use K&R braces and braces for every loop and
  conditional, including one-line bodies. Keep lines at most 120 characters, preferably
  below 110; wrap with an additional eight spaces at readable locations.
- Keep imports explicit, minimal, and consistently ordered: static imports, Java imports,
  then non-Java imports, with a blank line between groups.
- Initialise variables where declared when a valid value exists, keep them in the smallest
  practical scope, and use short iterator names only for small loop scopes. Do not expose
  mutable class state through public fields; public fields are acceptable only for data-class
  fields or constants. Add `// Fallthrough` to intentional fall-through switch cases.
- Separate logical units in a block with one blank line. Write comments in clear American
  English without local slang; remove comments that merely repeat the code.
- Add descriptive Javadocs to all public classes and public methods, except obvious getters and
  setters, exact Javadoc-preserving overrides, and test code. Start method summaries with
  a third-person verb such as “Returns”, “Adds”, or “Displays”; use complete, punctuated
  tag descriptions and a blank line before tags.

## Before finishing

Review modified Java files for the conventions above. Run the relevant Gradle verification
task with Java 25; run `javadoc` as well when Javadocs were changed.
