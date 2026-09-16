---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions when naming branches or preparing commits in this project.
---

# SE-EDU Git standard

Use this skill whenever creating a branch, proposing a commit message, or creating a commit in this repository. Apply the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html).

## Commit subjects

- Write an imperative, capitalized subject without a final period.
- Prefer 50 characters or fewer; never exceed 72 characters.
- Add a relevant scope or category prefix only when it improves clarity.

## Commit bodies

For non-trivial commits, include a body separated from the subject by a blank line. Wrap body lines at 72 characters and explain what changed and why, rather than implementation details. Use blank lines or bullets when they improve readability. Split a change into smaller commits if its explanation becomes unwieldy.

## Branch names

Use meaningful, kebab-case names made from relevant keywords. For issue work, use `issueNumber-keywords-from-issue-title`. Preserve any project-required branch prefix while keeping the remainder meaningful and kebab-case.

## Before committing

Review the staged diff and confirm the message follows these rules. Do not commit or push without the user's explicit authorization.
