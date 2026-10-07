---
name: seedu-java-coding-standard
description: Apply SE-EDU basic and intermediate Java conventions when writing, modifying, or reviewing this project's production and test Java code.
---

# SE-EDU Java coding standard

## Authority

Read the [SE-EDU basic and intermediate rules](https://se-education.org/guides/conventions/java/intermediate.html)
before a compliance review or when a rule is unclear. Use its linked Google guide
only for uncovered topics. If the source is unavailable, use this checklist and
report that a complete source-based review could not be performed.

## Working checklist

- Packages: lowercase project-root names; put every class in a package.
- Types: PascalCase nouns. Methods: camelCase verbs. Variables: camelCase;
  booleans read as predicates, collections use plural names. Constants use
  uppercase underscore-separated words; related constants share a prefix.
  Normalize acronyms. Keep names meaningful for their scope; test methods may
  use feature_scenario_expectedBehavior.
- Layout: four-space indentation; continuation indentation adds eight spaces.
  Aim below 110 columns; never exceed 120. Use K&R braces, including single-statement
  loops and conditionals. Put bodies on separate lines. Separate logical units.
- Wrap after commas and before operators; keep method names beside parentheses.
  Space operators, keywords, commas and loop separators consistently.
- Imports: explicit, minimal and consistently ordered. Attach array brackets to
  types. Initialize variables where practical in their narrowest scope. Encapsulate
  mutable fields. Mark intentional switch fallthrough.
- Comments: American English; Javadoc summaries describe behavior. Use multiline
  class/method headers, aligned stars, punctuated tags and no gap before declarations.

## Project application

Read the root AGENTS.md as well. Its broader Javadoc requirement applies to all
non-private classes and declared methods (including tests and accessors), plus
non-trivial private methods. Do not remove existing useful documentation under
the upstream exceptions.

For this repository, order imports in alphabetized groups: static imports, Java
standard library, third-party libraries, then mary imports. Separate groups with
one blank line. Keep src/main/java and src/test/java as the source roots.

Preserve runtime behavior, command text and saved-data formats during style-only
changes. Update filenames, Gradle entry points, tests and current run instructions
when renaming Java types. Do not rewrite historical test transcripts.

Review changed files against the source rules, not just formatter output. Check
line lengths, imports, braces, names and Javadoc; compile with Java 25, run JUnit,
and invoke the project's test-ui skill as required by AGENTS.md. Report the checks
actually performed and any unresolved deviations. Do not commit without permission.
