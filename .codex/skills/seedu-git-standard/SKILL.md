---
name: seedu-git-standard
description: Apply the se-education Git conventions when preparing branches or commits in this project.
---

# se-education Git standard

Apply the [se-education Git conventions](https://se-education.org/guides/conventions/git.html) whenever preparing a branch, commit message, or commit for this repository.

## Branches

Use meaningful kebab-case branch names made from relevant keywords, such as `refactor-ui-tests`. For issue-related branches, use `issueNumber-keywords-from-issue-title`, such as `1234-ui-freeze-error`.

## Commit messages

- Write a subject in imperative mood, capitalize its first letter, and do not end it with a period.
- Aim for a subject of 50 characters or fewer; it must not exceed 72 characters. A descriptive scope or category prefix is allowed when useful.
- For a non-trivial commit, add a body after a blank line. Wrap body lines at 72 characters and separate paragraphs with blank lines.
- Explain what changed and why, rather than repeating implementation details visible in the diff. If the explanation is too long, consider whether the changes should be split into smaller commits.

## Before committing

Review the staged diff and proposed message against these rules. Do not create a commit unless the user has authorized it. This skill sets commit conventions; it does not grant permission to commit, push, switch branches, or rewrite history.
