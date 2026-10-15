---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions when creating branches or writing commit messages for this project.
---

# SE-EDU Git standard

Apply this skill whenever you create a branch or commit changes in this project. The authoritative reference is the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html).

## Branches

- Use a meaningful kebab-case branch name made from relevant keywords.
- When a branch relates to an issue, prefer `issueNumber-keywords-from-issue-title`.

## Commit subjects

- Write a clear subject for every commit.
- Use imperative mood, capitalize the first letter, and do not end with a period.
- Keep the subject close to 50 characters and never exceed 72 characters.
- Add a concise scope or category prefix only when it improves clarity.

## Commit bodies

- Add a body for every non-trivial commit, separated from the subject by one blank line.
- Wrap body lines at 72 characters and separate paragraphs with blank lines.
- Explain what changed and why; do not describe implementation steps that the diff already shows.
- Describe the current situation in present tense, the reason for change, the change in imperative mood, and the rationale for the chosen approach.
- Use bullet points when they make several related changes easier to scan.

Before committing, review the staged diff and check that the subject and body follow these rules. Do not commit unless the user has authorized committing.
