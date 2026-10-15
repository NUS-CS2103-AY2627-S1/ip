---
name: present-changes-visually
description: Presents code differences, git changes, or refactorings in an easy-to-read visual format, generating an HTML report with side-by-side or styled diff blocks and structured summaries.
---

# Present Changes Visually

This skill presents code differences and modifications visually rather than just plain text diffs.

## Workflow
1. Inspect the modified files, diffs, or commits.
2. Group the changes logically by file or architectural concern.
3. For each change:
   - Identify the rationale and coding standard rule or requirement motivating the change.
   - Present a visual "Before" vs "After" diff highlighting the exact lines modified.
4. Generate a standalone visual HTML report in `_temp/` (e.g. `_temp/code-changes.html` or `_temp/interesting-differences-in-PRs.html`) with clean styling, syntax highlights, and annotations.
5. Provide a clear summary in the chat response or markdown artifact linking to the files and referencing the generated visual report.
