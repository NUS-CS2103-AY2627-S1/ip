---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions when naming branches or preparing and reviewing commits in this project.
---

# Seedu Git Standard

Use this skill whenever you create, review, or propose a Git branch name or commit message in this repository. It is based on the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html).

## Required conventions

### Commit subject

- Write a clear subject for every commit.
- Prefer 50 characters or fewer; never exceed the 72-character hard limit.
- Use imperative mood, capitalize the first letter, and do not end with a period.
- Add a relevant scope or category prefix when it improves clarity, such as `Parser: Handle blank input` or `chore: Update dependencies`.

### Commit body

For every non-trivial commit, separate the subject from the body with one blank line and wrap body lines at 72 characters. Use blank lines between paragraphs and explain what changed and why it was needed, not how the diff implements it. A useful order is: describe the current situation in present tense, explain why it needs to change, state the change in imperative mood, and record relevant rationale or context. Use bullets when they make several related changes easier to scan. Avoid repeating information already clear from code comments.

### Branch names

Use meaningful names made from relevant keywords in kebab case, such as `refactor-ui-tests`. For issue-related work, use `issueNumber-some-keywords-from-issue-title`.

## Review workflow

Before committing, check the subject length and punctuation, imperative mood, capitalization, body wrapping, WHAT/WHY explanation, and branch-name format. Keep the commit focused; if the message becomes too long, consider splitting the work into smaller commits. This skill guides commit preparation and does not itself authorize creating or pushing a commit.
