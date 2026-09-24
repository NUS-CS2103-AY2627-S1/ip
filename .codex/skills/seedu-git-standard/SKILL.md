---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions when naming branches or proposing and creating commits in this project.
---

# SE-EDU Git standard

Follow the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html) whenever naming a branch or proposing, reviewing, or creating a commit in this repository.

## Commit messages

- Write a clear imperative subject line. Capitalize its first letter, do not end it with a period, and keep it to 50 characters where practical (72 characters maximum).
- Add a meaningful scope or category prefix when it improves clarity, for example `Parser: Reject blank commands` or `chore: Update release date`.
- Give every non-trivial commit a body. Leave a blank line after the subject, wrap body lines at 72 characters, and separate paragraphs with blank lines.
- Explain what changed and why it was necessary, not implementation mechanics already apparent in the diff. A useful body describes the current situation, why it must change, the intended change, and why that approach fits.
- Split an overlong explanation into smaller, focused commits when the change can be separated safely.

## Branch names

- Use meaningful relevant keywords in kebab case, for example `refactor-ui-tests`.
- For issue-linked work, use `issueNumber-keywords-from-issue-title`, for example `1234-ui-freeze-error`.

## Before committing

Review the proposed subject and body against these rules, and show the user the final commit message for approval unless they explicitly authorized the commit itself. Do not treat this skill as authorization to commit, push, rewrite history, or modify branches.
