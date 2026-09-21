---
name: java-coding-standards
description: Apply this project's Java coding standard when writing or reviewing Java, using Google Java Style only for topics the course standard does not cover.
---

# Java Coding Standards

Use this skill for Java code changes and Java style reviews in this repository. The
course standard is the primary authority; Google Java Style fills only gaps.

Read [the project standard](references/course-java-standard.md) before editing or
reviewing Java code. Apply the relevant rules rather than mechanically changing
unrelated code.

Run the deterministic formatting check every time this skill is invoked:

```bash
python3 skills/java-coding-standards/scripts/check_java_format.py
```

Run it again after Java edits. It checks only rules that can be verified safely
without a full Java parser: indentation, tabs, trailing whitespace, hard line
length, explicit imports, selected spacing and brace rules, modifier order, and
array-bracket placement. Apply the remaining course rules through review.

## Working approach

- Keep names, braces, declarations, comments, and imports compliant with the
  project standard. Preserve the existing package structure unless the task
  requires a structural change.
- Keep ordinary code at or below the course's 120-character hard limit (110 is
  preferred). Use four-space indentation and its continuation-indentation rule;
  do not apply Google's two-space indentation or 100-character limit here.
- For rules absent from the project standard, follow the current
  [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).
  In particular, use `@Override` whenever legal and do not silently ignore
  caught exceptions.
- Include Javadoc for public classes and public or nontrivial private methods
  unless an exception in the course standard applies. Keep comments useful;
  do not add comments that merely repeat the code.
- Do not reformat unrelated files or lines solely for consistency. Reformat a
  wider area only when the user asks or when it is necessary to keep edited code
  readable.

## VS Code support

When Visual Studio Code is available, use **Format Document** after a Java edit
and inspect the resulting diff. Its Java extension can use an Eclipse formatter
profile. The official VS Code documentation shows how to configure the Google
Style profile, but do so only if the user asks to add workspace settings: Google
Style's indentation and column limit conflict with this project's standard.

For live diagnostics, suggest (but do not install or configure without approval)
the **Checkstyle for Java** extension with a project-specific configuration, or
**SonarLint** for quality and security analysis. See the
[VS Code Java formatting and linting guide](https://code.visualstudio.com/docs/java/java-linting).

## Review output

Report only actionable discrepancies. State the violated project rule, show the
smallest suitable correction, and distinguish a definite violation from an
optional Google Style recommendation.
