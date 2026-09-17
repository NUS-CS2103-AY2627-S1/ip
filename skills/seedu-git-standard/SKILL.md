---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions when creating commits or naming branches in this project.
---

# SE-EDU Git Standard

Apply this skill to Git commits and branches in this repository. The authoritative
reference is the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html).

## Commit messages

- Write a clear subject for every commit.
- Keep the subject at 50 characters when possible and never over 72 characters.
- Use imperative mood, capitalize the first letter, and do not end the subject with a period.
- Add a scope or category when useful, such as `Parser: Add command validation`.
- For non-trivial commits, separate the subject and body with a blank line.
- Wrap the body at 72 characters and explain what changed and why; avoid merely describing
  implementation steps.
- Structure a detailed body around the current situation, why the change is needed, what
  should be done, and why that approach was chosen.

## Branch names

- Use meaningful kebab-case names, such as `refactor-command-parser`.
- When a branch relates to an issue, use `<issue-number>-<keywords>`.

## Verification

Before committing, inspect the staged diff and commit message, confirm unrelated changes
are not included, and run the relevant project tests when code is affected.
