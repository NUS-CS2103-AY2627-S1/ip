---
name: seedu-java-coding-standard
description: Apply the SE-EDU intermediate Java coding standard when writing, reviewing, or refactoring Java in this project.
---

# Seedu Java Coding Standard

Apply the [SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html)
to every production and test Java change in this repository. Preserve existing
behavior unless the user requests a behavioral change.

- Use lowercase package names; PascalCase nouns for types; camelCase verbs for
  methods; and camelCase variables. Use `is`, `has`, `can`, or similar prefixes
  for booleans and plural names for collections. Test methods may use
  `featureUnderTest_testScenario_expectedBehavior`.
- Use four spaces for indentation, K&R braces, braces for every loop and
  conditional body, and a maximum line length of 120 characters (prefer 110).
  Wrap for readability, with continuation indentation eight spaces beyond the
  parent line; break after commas and before operators.
- Keep imports explicit and consistently grouped: Java imports first, followed
  by a blank line and project or third-party imports. Do not use wildcard
  imports.
- Declare variables in the smallest useful scope and initialize them at
  declaration when a valid initial value is available. Do not expose mutable
  public fields.
- Write comments in English using American spelling. Give every public class
  and public method a JavaDoc header unless it is a getter, setter, an exact
  override, or test code. Begin the summary with a verb such as “Returns” or
  “Adds”, use complete sentences, and end `@param`, `@return`, and `@throws`
  descriptions with punctuation.

Before handing off Java changes, check that the changed files meet these rules
and run the project verification required by `AGENTS.md`.
