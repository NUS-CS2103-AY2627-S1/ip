---
name: seedu-java-coding-standard
description: Apply the SE-EDU intermediate Java coding standard when creating, editing, refactoring, or reviewing Java production and test code in this project.
---

# SE-EDU Java Coding Standard

Use this skill for every Java code creation, edit, refactor, or review in this
project.

## Apply the standard

1. Read [references/rules.md](references/rules.md) completely before changing
   or reviewing Java code.
2. Inspect nearby code before editing so that choices not settled by the
   standard remain locally consistent.
3. Apply the rules to production and test code. Do not broaden the requested
   change solely to reformat unrelated code.
4. Give all public classes and nontrivial public methods descriptive JavaDoc.
   Document non-obvious fields and private methods where doing so explains
   intent that the code cannot express clearly.
5. After editing, check formatting and documentation:

   ```bash
   git diff --check
   rg -n $'\t| +$' src/main/java src/test/java
   rg -n '^.{121,}$' src/main/java src/test/java
   rg -n '^import .+\.\*;' src/main/java src/test/java
   ./gradlew javadoc test
   ```

6. Treat an `rg` command that finds nothing as a successful check. Resolve
   JavaDoc warnings that concern changed code.
7. For an application-code change, also follow the project JUnit and
   `$test-ui` requirements in `AGENTS.md`.

If a user instruction conflicts with this skill, follow the user instruction
and state the deviation briefly.
