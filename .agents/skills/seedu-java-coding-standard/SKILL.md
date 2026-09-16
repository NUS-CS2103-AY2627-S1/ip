---
name: seedu-java-coding-standard
description: Apply the SE-EDU Java coding standard (basic and intermediate rules) to this project. Use whenever creating, modifying, refactoring, reviewing, or generating Java production or test code in this repository, including formatting, naming, imports, control-flow layout, and Javadoc.
---

# Apply the SE-EDU Java Coding Standard

Read [references/rules.md](references/rules.md) before working with Java code in this project.

## Workflow

1. Inspect every affected Java file, including related tests.
2. Follow every applicable rule in the reference while preserving behavior unless the task requires a behavior change.
3. Prefer clear code over comments that merely repeat the implementation.
4. Use the Google Java Style Guide only for topics the SE-EDU rules do not cover.
5. Before finishing, check all Java files for tabs and lines over 120 characters:

   ```bash
   rg -n '\t' src -g '*.java'
   rg -n '^.{121,}$' src -g '*.java'
   ```

6. Run the relevant build and tests with Java 25.

Treat these rules as mandatory for both new code and code changed during a task. When reviewing code without authorization to edit it, report violations with file and line references instead.
