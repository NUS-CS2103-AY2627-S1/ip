---
name: seedu-git-standard
description: Enforce the SE-EDU Git conventions for commit messages and branch names in this project.
---

# Seedu Git Standard

Use this skill whenever creating, amending, or reviewing a commit or branch
name in this repository. It is based on the
[SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html).

## Commit subject

- Write a clear subject for every commit.
- Keep it to 50 characters where possible; never exceed 72 characters.
- Use imperative mood, capitalize the first letter, and do not end with a
  period. A meaningful `<scope>:` or `<category>:` prefix is allowed when it
  improves clarity.
- Examples: `Add README.md`, `Update parser tests`,
  `Storage: Handle missing data file`.

## Commit body

Non-trivial commits must have a body separated from the subject by one blank
line. Wrap body lines at 72 characters, use blank lines between paragraphs,
and use bullets where they improve readability.

Explain WHAT the commit changes and WHY it is needed; do not spend the body
explaining HOW, since the diff shows the implementation. A useful structure
is:

1. Describe the current situation in present tense.
2. Explain why it needs to change.
3. Describe the change in imperative mood.
4. Explain why that approach is used.
5. Add other relevant context only when it helps review the commit.

Avoid redundant words such as `currently` and `originally`. If the message
becomes too long, consider splitting the work into smaller commits.

## Branch names

Use meaningful kebab-case names made from relevant keywords, such as
`refactor-ui-tests`. For issue-related branches, use
`<issue-number>-<keywords-from-issue-title>`.

## Review checklist

Before committing:

1. Check that the subject is imperative, capitalized, period-free, and within
   the length limit.
2. Add a 72-column-wrapped WHAT/WHY body for non-trivial work.
3. Check that the branch name is meaningful and kebab-case.
4. Confirm the staged diff contains only the intended changes.
