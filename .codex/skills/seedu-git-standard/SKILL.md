---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions when naming branches and creating or reviewing commits in this project.
---

# SE-EDU Git Standard

## High-priority commit requirement

For every commit, every commit-body line must be no more than
72 characters long, including spaces and punctuation. This is
mandatory, not optional.
Before running `git commit`, measure each body line and rewrap
any line that exceeds 72 characters; only commit after the
check passes. Apply this rule to every commit, including trivial
and follow-up commits.

Use this skill for Git work in this repository. Follow the
[SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html)
and keep changes scoped to the user's request.

## Commit subjects

- Every commit must have a clear subject line.
- Aim for 50 characters and never exceed 72 characters.
- Use the imperative mood, capitalize the first letter, and do not end with
  a period.
- Add a short scope or category prefix when it improves clarity, such as
  `Parser: Handle empty input` or `chore: Update test plan`.

## Commit bodies

- Add a body for non-trivial commits, separated from the subject by one blank
  line.
- Wrap every body line at no more than 72 characters and use blank lines
  between paragraphs.
- Treat the 72-character limit as a hard requirement; verify it before
  committing.
- Explain WHAT changed and WHY it changed; the diff explains HOW.
- Prefer this order: current situation, why it needs to change, what to do,
  why that approach is appropriate, and any other relevant information.
- Use present tense for the current situation and imperative mood for the
  change. Avoid unnecessary words such as `currently` and `originally`, and
  avoid repeating information already clear from code comments.
- Use bullets when they make multiple changes easier to scan.

## Branch names

- Use meaningful kebab-case names containing relevant keywords, such as
  `refactor-ui-tests`.
- For issue-related branches, use
  `issueNumber-keywords-from-issue-title`, such as
  `1234-ui-freeze-error`.

Before committing, review the staged diff and ensure the message describes
the complete staged change. Do not stage unrelated files.
