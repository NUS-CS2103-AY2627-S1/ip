---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions when naming branches or preparing commits in this project.
---

# SE-EDU Git Standard

Apply this skill whenever creating a branch, proposing a commit message, or
preparing a commit in this repository. Follow the
[SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html).
This skill guides Git conventions only; retain all existing project rules about
authorization, commits, tags, merges, and pushes.

## Branch names

- Use meaningful, relevant keywords in kebab case, for example
  `refactor-ui-tests`.
- For work tied to an issue, use `issueNumber-keywords-from-issue-title`, for
  example `1234-ui-freeze-error`.
- Honor a branch name explicitly requested by the user, even if it differs
  from this convention.

## Commit messages

- Write an imperative, capitalized subject without a final period. Aim for 50
  characters and never exceed 72. An optional descriptive scope or category
  may precede the subject, for example `Parser: Reject blank task names`.
- Give every non-trivial commit a body, separated from the subject by a blank
  line. Wrap body lines at 72 characters and use blank lines or bullets where
  they improve readability.
- Explain **what** changed and **why**, rather than implementation mechanics.
  Give enough context for a reviewer to evaluate the change without opening
  the diff. If that explanation becomes too long, consider whether the work
  should be split into focused commits.
- Structure longer bodies around the present situation, the reason for change,
  the intended change, and any relevant trade-offs or information.

## Before committing

Review the staged diff and confirm the message accurately represents its
scope. Do not commit, push, tag, or alter history unless the user has given the
required authorization.
