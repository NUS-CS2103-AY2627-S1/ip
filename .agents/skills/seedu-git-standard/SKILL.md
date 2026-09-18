---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions when proposing, creating, amending, or reviewing commit messages and branch names in this project.
---

# SE-EDU Git Standard

Follow the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html) for new commits and branches. Preserve existing history unless the user explicitly requests a history-changing operation.

## Commit subjects

- Write a specific subject that summarizes the change.
- Use imperative mood, capitalize the first letter, and do not end with a period.
- Aim for at most 50 characters and never exceed 72 characters.
- Add an optional `<scope>:` or `<category>:` prefix only when it improves clarity.

## Commit bodies

- Add a body for non-trivial changes, separated from the subject by one blank line.
- Wrap body text at 72 characters and separate distinct ideas with blank lines or useful bullet points.
- Explain what situation motivates the change, why it matters, what the commit changes, and why that approach was chosen.
- Focus on what and why; let the diff show how.
- Avoid redundant details already clear from code comments or the diff.
- Split unrelated concerns into separate commits when one message would become unfocused.

## Branch names

- Use meaningful kebab-case names, such as `refactor-ui-tests`.
- When a branch corresponds to an issue, prefer `issueNumber-keywords`, such as `1234-fix-ui-freeze`.
- Do not rename an existing course-required or user-selected branch unless explicitly requested.

## Applying the standard

Inspect the staged diff before proposing a message. Ensure each commit contains one coherent change and describe its rationale accurately. Show the proposed message before committing when the user asks to review it. Do not create, amend, tag, or push a commit without explicit user authorization.
