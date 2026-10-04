---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions when proposing, preparing, reviewing, or creating commits for this project.
---

# SE-EDU Git standard

Use this skill whenever preparing, reviewing, proposing, or creating a commit
in this repository. It summarizes the
[SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html).
Project-level Git requirements and explicit user instructions take precedence.

## Commit subject

- Write a well-formed subject in imperative mood, starting with a capital
  letter and without a final period.
- Aim for 50 characters; never exceed 72 characters.
- Add an optional clear scope or category when helpful, for example
  `Storage: Preserve escaped task details` or `chore: Update release date`.

## Commit body

For a non-trivial commit, add a body separated from the subject by one blank
line. Wrap body lines at 72 characters and use blank lines to separate
paragraphs or bullets.

Explain **what** changes and **why**, not implementation mechanics already
visible in the diff. A useful sequence is: describe the current situation in
present tense, explain why it needs to change, state the change in imperative
mood, and explain the rationale. If that explanation becomes unwieldy,
consider splitting the work into smaller commits.

## Branches

Use meaningful, kebab-case branch names based on relevant keywords, for
example `refactor-ui-tests`. For an issue-related branch, use
`issue-number-keywords`, for example `1234-ui-freeze-error`. When the project
or platform requires a branch prefix, retain it before that descriptive name.
