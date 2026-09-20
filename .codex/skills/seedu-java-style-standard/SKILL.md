---
name: seedu-java-style-standard
description: Review and lint this project's Java code using the SE-EDU coding conventions together with the project's established JavaDoc standard.
---

# Seedu Java Style Standard

This is the composite Java style skill for this project. Use it whenever a Java change needs both coding-standard and JavaDoc review.

## Composite instructions

Apply both companion skills before making or approving a Java change:

- Read and apply `.codex/skills/seedu-java-coding-standard/SKILL.md` for naming, layout, statements, imports, visibility, comments, and review expectations.
- Read and apply `.codex/skills/seedu-java-javadoc-standard/SKILL.md` for JavaDoc coverage, wording, tags, formatting, and JavaDoc linting.

Do not treat the JavaDoc rules as a replacement for the coding standard, or the coding standard as a replacement for JavaDoc review. Resolve findings in the smallest change that preserves behavior and follows both standards.

## Validation workflow

1. Inspect the changed Java production and test files, then classify findings as coding-style, JavaDoc, or behavior-related.
2. Run the project's Checkstyle tasks with Java 25:
   - Windows: `gradlew.bat checkstyleMain checkstyleTest`
   - Unix-like shells: `./gradlew checkstyleMain checkstyleTest`
3. Review Checkstyle output against the changed lines. Fix violations rather than adding suppressions unless an exception is explicitly justified by the project or the user.
4. Manually verify JavaDoc contracts, especially `@param`, `@return`, and `@throws` descriptions, because Checkstyle cannot prove that documentation is semantically accurate.
5. Run the relevant build and tests after the style fixes, as required by the coding-standard skill. Report the exact validation commands and distinguish pre-existing failures from failures caused by the change.

## Handoff

Summarize the files reviewed, the meaningful style or JavaDoc corrections made, the lint/build/test commands run, and any remaining findings. Do not commit or push unless the user explicitly asks.
