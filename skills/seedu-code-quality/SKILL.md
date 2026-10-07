---
name: seedu-code-quality
description: Review and improve source code using the SE-EDU code-quality principles. Use for code-quality audits, readability refactors, or final review of source changes in this repository; use a language-specific coding-standard skill separately for mechanical style rules.
---

# SE-EDU code quality

Use the [CS2103/T Code Quality chapter](https://nus-cs2103-ay2627-s1.github.io/website/se-book-adapted/chapters/codeQuality.html#code-quality)
as the authoritative reference. Preserve behavior unless the user requests a behavior change, and prefer the
smallest refactor that materially improves understandability.

## Review workflow

1. Inspect the changed code and enough surrounding context to understand its role and existing conventions.
2. Identify concrete readability or safety problems; do not refactor merely to express personal preference.
3. Fix justified issues without adding speculative abstractions or premature optimizations.
4. Run the repository's required formatting, static-analysis, and test commands after source changes.
5. Report the important improvements and any unresolved concerns with file and line references.

## Quality checklist

- Keep methods focused; when a method grows beyond roughly 30 lines, consider extracting cohesive operations.
- Avoid deep nesting. Use guard clauses when they make the normal path easier to follow.
- Break complicated expressions into well-named intermediate values and replace unexplained literals with
  named constants.
- Make control flow and data flow explicit. Keep related statements together and at one level of abstraction.
- Prefer a simple sufficient implementation (KISS); optimize only for a demonstrated need.
- Name classes and variables with nouns and methods with verbs. Names should explain purpose, use standard
  words, distinguish singular from plural values, and avoid misleading similarities.
- Include a genuine default branch in each switch. Do not recycle parameters or variables for another purpose,
  leave catch blocks empty, or retain dead code.
- Declare variables in the narrowest useful scope and avoid copy-paste duplication when a clear shared operation
  exists.
- Make code self-explanatory first. Comments should tell the reader what a non-obvious contract is or why a
  decision exists, not narrate how obvious statements execute.

For Java files, also apply `skills/seedu-java-coding-standard/SKILL.md`; code quality and coding style are related
but separate checks.
