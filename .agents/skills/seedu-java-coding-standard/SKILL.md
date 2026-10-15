---
name: seedu-java-coding-standard
description: Apply and audit the SE-EDU basic and intermediate Java coding standard when writing, modifying, refactoring, or reviewing Java code in this project.
---

# Follow the SE-EDU Java coding standard

Use the required basic and intermediate rules from the
[SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html).
Read [references/rules.md](references/rules.md) before changing or reviewing Java code.

## Workflow

1. Inspect the Java files in scope before editing and preserve their behavior unless the user
   requested a behavioral change.
2. Apply every relevant required rule in the reference. Keep unrelated rewrites out of
   feature-specific changes; perform a repository-wide cleanup only when explicitly requested.
3. Check the completed Java diff for tabs, trailing whitespace, lines over 120 characters,
   wildcard imports, inconsistent naming, missing braces, and missing required header comments.
4. Run the relevant Gradle and project tests after code changes.

Use 4-space indentation, explicit imports, K&R braces, a 120-character hard line limit, and
descriptive English names. New public classes and methods require JavaDoc except for the
documented exemptions in the reference.
