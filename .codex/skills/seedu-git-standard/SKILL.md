---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions when creating branches, commits, tags, or commit messages in this project.
---

# SE-EDU Git Standard

Apply the conventions from https://se-education.org/guides/conventions/git.html to all Git work in this project.

## Commit subjects

- Write a meaningful subject of 72 characters or fewer; aim for 50 characters.
- Use imperative mood, capitalize the first letter, and do not end with a period.
- Add a concise scope or category prefix only when it improves clarity.

## Commit bodies

- Use a body for non-trivial commits, separated from the subject by a blank line.
- Wrap body lines at 72 characters.
- Explain what changed and why; do not describe implementation steps that the diff already shows.
- Keep the message focused. Split unrelated work into separate commits.

## Branches

- Use meaningful kebab-case names based on the work, such as `refactor-ui-tests`.
- If an issue number applies, prefix the name with the issue number.

Before creating a commit, review the staged diff and confirm the message follows these rules. Do not commit generated binaries or unrelated local data.
