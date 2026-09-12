---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions when naming branches or creating, reviewing, or amending commits in this project.
---

# Seedu Git Standard

Apply this standard to every branch name and commit message in this repository.
The authoritative reference is the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html).

## Branch names

- Use meaningful kebab-case names containing relevant keywords, such as
  `refactor-ui-tests`.
- When a branch addresses an issue, use
  `issueNumber-some-keywords-from-issue-title`.

## Commit subject

- Write a subject for every commit.
- Keep it within 50 characters when possible and never exceed 72 characters.
- Use imperative mood, capitalize the first letter, and do not end with a period.
- Add a scope or category prefix only when it improves clarity, for example
  `Parser: Validate missing dates` or `chore: Update release metadata`.

## Commit body

- Give every non-trivial commit a body separated from the subject by one blank
  line.
- Wrap body lines at 72 characters and separate paragraphs with blank lines.
- Explain what changed and why it was needed, not the mechanical implementation
  steps.
- Structure the explanation around the current situation, reason for change,
  requested change, rationale, and any relevant additional information.
- Use bullets when they make multiple changes easier to review.
- Avoid redundant commentary already captured by code comments.

## Commit review

Before committing, inspect the staged diff and confirm that it contains only the
intended changes. Apply this skill to the final subject and body, and preserve
the repository's existing history unless the user explicitly requests history
rewriting.
