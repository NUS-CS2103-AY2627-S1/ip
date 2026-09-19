---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions when proposing or creating commit messages and branch names in this repository.
---

# SE-EDU Git Standard

Follow the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html).

## Commit subjects

- Write a clear subject for every commit.
- Use imperative mood and capitalize the first letter.
- Do not end the subject with a period.
- Aim for 50 characters and never exceed 72 characters.
- Add a scope or category prefix only when it improves clarity.

## Commit bodies

For non-trivial changes, separate the body from the subject with a blank line and wrap it at 72 characters.
Explain what changed and why; leave implementation mechanics to the diff.
Split the change into smaller commits if a clear message becomes too broad.

## Branch names

Use meaningful kebab-case keywords unless an assignment requires an exact branch name.
For issue-related work, prefer `issueNumber-keywords-from-title`.

Before creating a commit, inspect the staged diff, exclude generated or unrelated files, and ensure the message describes the staged change.
