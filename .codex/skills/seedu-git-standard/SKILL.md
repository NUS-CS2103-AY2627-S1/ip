---
name: seedu-git-standard
description: Create and review this project's Git commits and branch names using the SE-EDU Git conventions.
---

# Seedu Git Standard

Apply this skill whenever proposing, preparing, or creating a commit, and whenever naming a project branch. The source guide is [SE-EDU's Git conventions](https://se-education.org/guides/conventions/git.html).

## Commit subject

- Write a meaningful subject in the imperative mood, such as `Add README.md`.
- Capitalize the first letter and do not end the subject with a period.
- Prefer 50 characters or fewer; never exceed 72 characters.
- Add a relevant `<scope>:` or `<category>:` prefix when it improves clarity.

## Commit body

Non-trivial commits must have a body separated from the subject by one blank line. Wrap body lines at 72 characters and use blank lines or bullet points to separate ideas.

Explain what the commit changes and why it is needed, not how the implementation works. A useful structure is:

1. Describe the current situation in the present tense.
2. Explain why it needs to change.
3. State what the commit does in the imperative mood.
4. Explain why that approach was chosen.
5. Add other relevant context only when necessary.

Avoid repeating details already obvious from code comments or the diff. Avoid words such as `currently` and `originally` when they add no information.

## Branch names

- Use meaningful keywords in kebab-case, such as `refactor-ui-tests`.
- When a branch addresses an issue, use `<issue-number>-<keywords-from-issue-title>`.

Before creating a commit, review the staged changes and confirm that the subject, body, and branch name follow these rules. Do not create or amend commits unless the user explicitly requests it.
