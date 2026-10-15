---
name: java-convention-checker
description: Review Java source files against the SE-EDU Java coding standard and report clear, file-specific violations. Use when the user asks to check Java coding conventions or Javadoc compliance.
---

# Java Convention Checker

Review changed Java source files against the [SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/index.html). Treat the repository's `AGENTS.md` as additional project context, but use the linked standard as the authority for this review.

## Workflow

1. Determine changed files relative to `HEAD`. Use `git diff --name-only HEAD -- '*.java'` for tracked files and `git ls-files --others --exclude-standard -- '*.java'` for untracked files. Exclude deleted files and non-Java files.
2. Review only those changed Java files by default. Do not scan or read unchanged Java files unless needed to understand a changed declaration, or unless the user explicitly requests a full review.
3. Include test files only when they are changed or when the user explicitly asks for a full review. Testing classes and methods are exempt from the header-comment requirements.
4. Read the selected files and, where useful, use focused searches for packages, imports, declarations, modifiers, braces, line lengths, comments, and Javadocs.
5. Check applicable rules, including naming, English/American spelling, indentation, line length, whitespace, package/import practices, class-member order, modifier order, array declarations, variable scope and initialization, visibility, `this`, braces, conditionals, loops, and comments/Javadocs.
6. Do not report permitted exceptions as violations: getters/setters and applicable overridden methods may omit header comments. Non-trivial private methods still need header comments.
7. Report findings with severity, file and line, violated rule, evidence, and a concise fix suggestion. Group repeated violations by rule when that improves readability.
8. Distinguish definite violations from judgment calls. Do not modify files unless the user explicitly requests fixes.

## Output

Start with the number of changed Java files checked and a concise overall result. If no changed Java files are found, say so and do not scan the repository. Then list findings in priority order. If no violations are found, say so and mention any limitations, such as rules requiring human judgment.

If the user explicitly requests a full review, inspect all relevant Java files instead of limiting the review to changed files.

For Javadocs, verify the opening `/**`, summary sentence, indentation, spacing, punctuation, blank line before tags, appropriate `@param`/`@return`/`@throws` tags, and that the documentation is immediately adjacent to the declaration.