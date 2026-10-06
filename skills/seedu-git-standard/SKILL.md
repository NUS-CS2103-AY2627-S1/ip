---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions when preparing commits, writing commit messages, or creating project branches.
---

# Seedu Git Standard

Apply the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html) whenever preparing a commit, writing or amending a commit message, or creating a branch in this repository.

## Commit messages

- Write a meaningful subject in imperative mood. Capitalize its first letter and do not end it with a period.
- Aim for at most 50 characters; never exceed 72 characters. A useful scope or category prefix is allowed when it improves clarity, for example `Parser: Reject empty deadlines`.
- For non-trivial commits, add a body separated from the subject by a blank line. Wrap body lines at 72 characters and separate paragraphs with blank lines.
- Explain what changed and why it matters, not implementation mechanics that the diff already shows. If the explanation is unwieldy, consider whether the work should be split into smaller commits.
- Use present tense for the current situation and imperative mood for the proposed change. Use bullets when they improve readability.

## Branch names

- Use meaningful, kebab-case names made from relevant keywords, such as `refactor-ui-tests`.
- For issue-related branches, use `issueNumber-keywords-from-issue-title`, such as `1234-ui-freeze-error`.

Before committing, review the staged diff and ensure the commit contains one coherent change. Do not commit or push without the user's explicit authorization.
