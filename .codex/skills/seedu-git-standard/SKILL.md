---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions for this project when creating branches, commits, or commit messages.
---

# SE-EDU Git Standard

Use this skill for future Git work in this project.

Source: https://se-education.org/guides/conventions/git.html

## Commit Message Subject

- Every commit must have a well-written subject line.
- Try to keep the subject within 50 characters. The hard limit is 72
  characters.
- Use the imperative mood, e.g. `Add README.md`, not `Added README.md`.
- Capitalize the first letter of the subject.
- Do not end the subject with a period.
- A scope or category prefix may be used when useful, e.g.
  `Parser: Handle empty deadline`.

## Commit Message Body

- Non-trivial commits should include a body.
- Separate the subject and body with a blank line.
- Wrap body lines at 72 characters.
- Explain what changed and why. Do not repeat implementation details that are
  obvious from the diff.
- Prefer present tense for the current situation and imperative mood for the
  change being made.
- Use paragraphs or bullet lists when they improve clarity.
- Split the work into smaller commits if the body becomes too long.

## Branch Names

- Use meaningful branch names in kebab-case, e.g. `refactor-ui-tests`.
- For issue-related branches, use
  `issueNumber-some-keywords-from-issue-title`, e.g.
  `1234-ui-freeze-error`.
