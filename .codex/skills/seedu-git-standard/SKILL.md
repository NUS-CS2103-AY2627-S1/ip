---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions when creating branches or preparing, reviewing, or making commits in this project.
---

# SE-EDU Git Standard

Apply the rules in the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html) for all future commits and branches in this project.

## Commit subjects

- Write an imperative, capitalized subject without a final period.
- Target 50 characters; never exceed 72 characters.
- Add an optional scope or category when it improves clarity, for example `Storage: Load saved tasks`.

## Commit bodies

- Give non-trivial commits a body separated from the subject by one blank line.
- Wrap body lines at 72 characters and separate paragraphs with blank lines.
- Explain what changed and why, rather than implementation details already visible in the diff.
- If a message needs excessive explanation, consider whether the change should be split into smaller commits.

## Branch names

- Use meaningful, lowercase kebab-case names, for example `refactor-ui-tests`.
- For issue-related branches, use `issueNumber-keywords`, for example `1234-ui-freeze-error`.

Before committing, review the proposed subject and body against these checks. This skill governs message and branch naming only; it does not grant permission to commit, amend, force-push, or otherwise change Git history.
