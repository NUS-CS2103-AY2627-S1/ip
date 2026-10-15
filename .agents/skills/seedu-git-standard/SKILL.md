---
name: seedu-git-standard
description: Apply SE-EDU Git conventions when creating or reviewing commits and branch names in this project.
---

# SE-EDU Git standard

Use this skill whenever a task involves preparing, reviewing, or creating a Git commit, or naming a branch in this project. It is based on the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html).

## Commit subjects

- Write a clear subject line in the imperative mood.
- Prefer no more than 50 characters; never exceed 72 characters.
- Capitalize the first letter and do not end the subject with a period.
- Add a relevant scope or category prefix only when it improves clarity.

Examples: `Add README.md`, `Storage: Handle missing file`, `chore: Update release date`.

## Commit bodies

For every non-trivial commit, separate the body from the subject with a blank line and wrap body lines at 72 characters. Use blank lines between paragraphs and bullets where they improve readability.

Explain what changed and why. A useful order is:

1. Describe the current situation in the present tense.
2. Explain why it needs to change.
3. State what is being done in the imperative mood.
4. Explain why that approach was chosen.
5. Add other relevant information if needed.

Do not use the body to narrate implementation details that the diff already shows. Keep the message concise enough that it describes the commit as one coherent change.

## Branch names

Use meaningful kebab-case branch names. For issue-related branches, use `<issue-number>-<keywords-from-issue-title>`.

## Before committing

Review the staged diff and the complete commit message against these rules. Do not create a commit unless the user has explicitly requested it; when committing is authorized, use a message that follows this skill.
