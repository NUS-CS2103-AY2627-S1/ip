---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding standard when creating, editing, refactoring, or reviewing Java code in this project. Use for every Java code change or coding-standard audit; use Google Java Style only for topics the SE-EDU standard does not cover.
---

# Apply the SE-EDU Java coding standard

Read [references/rules.md](references/rules.md) completely before assessing or changing Java code.

Apply every applicable basic and intermediate rule to production and test code. Preserve behavior unless the user also requests a behavioral change. When an existing public API or assignment requirement conflicts with a naming rule, call out the conflict instead of silently breaking compatibility.

For matters not covered by the project reference, follow the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html). Prefer the SE-EDU rule if the two guides differ.

When reviewing or updating code:

1. Check the whole affected Java file, not only the changed lines.
2. Correct nearby violations that can be fixed safely without broadening the requested behavior.
3. Verify at minimum line length, indentation, braces, whitespace, imports, naming, variable scope, and required header comments.
4. Run the repository-required Java 25 tests and UI regression workflow after code changes.
5. Report any violation intentionally left in place and why changing it would be unsafe or out of scope.
