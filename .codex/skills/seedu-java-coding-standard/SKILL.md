---
name: seedu-java-coding-standard
description: Use when creating, modifying, refactoring, or reviewing Java source or test code in the Verity project.
---

# SE-EDU Java Coding Standard

## Overview

Apply every basic and intermediate rule from the SE-EDU Java coding standard to all Java code in this repository. Advanced rules are optional and must not be presented as project requirements.

Before editing or reviewing Java, read [references/java-rules.md](references/java-rules.md). It is the project checklist derived from the [official basic and intermediate standard](https://se-education.org/guides/conventions/java/intermediate.html).

## Workflow

1. Inspect every Java file in the requested scope, including tests.
2. Check it against every section of `references/java-rules.md`: naming, layout, statements, and comments.
3. For a review-only request, report violations without editing. Correct violations only when the user requests changes, and preserve behavior when the request is style-only.
4. Use the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html) only for topics the SE-EDU standard does not cover.
5. Run the relevant JUnit tests with Java 25, then run the full `./gradlew test` suite and `git diff --check` before claiming compliance.

## Boundaries

- Do not invent assessed rules. The SE-EDU page does not itself require member ordering, named constants for every literal, interface-typed fields, or a maximum method length.
- Header comments are required for every class and public method, except getters/setters, test code, and overrides whose inherited Javadoc applies exactly.
- The SE-EDU rule says "public methods" and does not explicitly include constructors. Document a constructor when its contract is non-obvious or another project instruction requires it; do not claim every constructor Javadoc is an assessed SE-EDU requirement.
- If an override changes the inherited contract, document the difference or use `{@inheritDoc}` with the additional detail.
- Preserve the repository's public API unless the user explicitly authorizes a behavior or interface change.

## Common mistakes

- Applying only basic rules and overlooking intermediate acronym, wrapping, whitespace, and blank-line rules.
- Treating advanced rules as mandatory.
- Copying fully qualified package names into ordinary prose instead of writing clear English or using `{@link Type}`.
- Calling code compliant after formatting it without checking naming, comments, imports, and control-flow braces.

## Non-assessed repository hygiene

When edits are authorized, remove accidental duplicate Javadocs and keep a final newline in Java source files. These are repository-hygiene recommendations, not SE-EDU or Google Java Style requirements.
