---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions when writing commit messages or naming branches in this project.
---

# SE-EDU Git Standard

Follow the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html)
for every commit message and branch name in this repository.

## Commit subject

- Write a clear subject for every commit.
- Use imperative mood, such as `Add validation` rather than `Added validation`.
- Capitalize the first letter and do not end the subject with a period.
- Aim for 50 characters and never exceed 72 characters.
- Add a scope or category prefix only when it improves clarity.

## Commit body

- Add a body for every non-trivial commit.
- Separate the subject and body with a blank line.
- Wrap every body line at 72 characters or fewer.
- Separate paragraphs with blank lines and use bullet points when useful.
- Explain what changed and why it changed. Leave implementation details to the
  diff unless they are important to the rationale.
- Keep the explanation detailed enough to assess the change without reading the
  diff. Split an overly broad change into smaller commits when practical.
- Prefer present tense for the existing situation and imperative mood for the
  change being made.

## Branch names

- Use meaningful keywords in kebab case, such as `refactor-ui-tests`.
- For an issue-related branch, use
  `issueNumber-keywords-from-issue-title`, such as `1234-ui-freeze-error`.

## Commit workflow

Before proposing or creating a commit:

1. Confirm the commit contains one coherent change.
2. Check the subject style and length.
3. Add and wrap a body when the change is non-trivial.
4. Ensure the message explains the change's purpose and rationale.
